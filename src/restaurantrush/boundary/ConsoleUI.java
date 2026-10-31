package restaurantrush.boundary;

import java.io.PrintStream;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import restaurantrush.control.ActionResult;
import restaurantrush.control.CustomerView;
import restaurantrush.control.GameController;
import restaurantrush.control.GameSnapshot;
import restaurantrush.control.TableView;
import restaurantrush.entity.Restaurant;

/**
 * The command-line interface: shows the game state and collects the
 * manager's choices.
 *
 * <p>This class holds no game rules. It asks {@link GameController} to carry
 * out each choice and displays the {@link ActionResult}. If a choice is
 * refused, the reason is shown and the manager chooses again, so the staff
 * task is not used up.</p>
 */
public class ConsoleUI {

    private static final String LINE = "=".repeat(60);
    private static final String THIN = "-".repeat(60);

    private static final int VICTORY_PAID = Restaurant.TARGET_PAID;
    private static final int VICTORY_REVENUE_CENTS = Restaurant.TARGET_REVENUE_CENTS;
    private static final int VICTORY_SATISFACTION = Restaurant.TARGET_SATISFACTION;

    private final GameController controller;
    private final InputReader input;
    private final PrintStream out;

    /**
     * Creates the interface.
     *
     * @param controller the game logic to drive
     * @param input      reads the manager's choices
     * @param out        where the game is displayed
     */
    public ConsoleUI(GameController controller, InputReader input, PrintStream out) {
        this.controller = controller;
        this.input = input;
        this.out = out;
    }

    /** Plays a whole game, all turns, then shows the result. */
    public void run() {
        showWelcome();
        try {
            while (!controller.isGameOver()) {
                playTurn();
            }
            showFinalResult();
        } catch (InputReader.InputClosedException e) {
            out.println();
            out.println(e.getMessage() + " Game stopped.");
        }
    }

    private void playTurn() {
        printEvents(controller.startTurn());
        GameSnapshot state = controller.snapshot();
        showTurnStart(state);

        out.println();
        out.println("WAITER (one task this turn)");
        askUntilAccepted(this::waiterChoice);

        out.println();
        out.println("CHEF (one preparation unit this turn)");
        askUntilAccepted(this::chefChoice);

        out.println();
        printEvents(controller.endTurn());
        showTurnEnd(controller.snapshot());
    }

    /**
     * Repeats a choice until the controller accepts it, printing each outcome.
     */
    private void askUntilAccepted(Supplier<ActionResult> choice) {
        while (true) {
            ActionResult result = choice.get();
            out.println("  " + (result.ok() ? "" : "Not allowed: ") + result.message());
            if (result.ok()) {
                return;
            }
        }
    }

    private ActionResult waiterChoice() {
        out.println("  1. Seat a waiting customer");
        out.println("  2. Take a seated customer's order");
        out.println("  3. Serve a READY dish");
        out.println("  0. Wait");
        int option = input.readInt("  Choose: ", 0, 3);
        return switch (option) {
            case 1 -> controller.waiterSeat(input.readCustomerId("  Seat which customer? "));
            case 2 -> controller.waiterTakeOrder(input.readCustomerId("  Take whose order? "));
            case 3 -> controller.waiterServe(input.readCustomerId("  Serve whose dish? "));
            default -> controller.waiterWait();
        };
    }

    private ActionResult chefChoice() {
        out.println("  1. Prepare one unit of an order");
        out.println("  0. Wait");
        int option = input.readInt("  Choose: ", 0, 1);
        if (option == 1) {
            return controller.chefWork(input.readCustomerId("  Prepare whose order? "));
        }
        return controller.chefWait();
    }

    private void showWelcome() {
        out.println(LINE);
        out.println("  RESTAURANT RUSH");
        out.println(LINE);
        out.println("You are the manager. Each turn, choose one task for the Waiter");
        out.println("and one for the Chef. The Cashier collects payments by itself.");
        out.println("Win after turn " + GameController.MAX_TURNS + " with at least "
                + VICTORY_PAID + " paid customers,");
        out.println("revenue of " + money(VICTORY_REVENUE_CENTS)
                + " or more, and average satisfaction of " + VICTORY_SATISFACTION + "+.");
    }

    private void showTurnStart(GameSnapshot s) {
        out.println();
        out.println(LINE);
        out.printf("  TURN %d of %d   |   Revenue %s   |   Avg satisfaction %s%n",
                s.turn(), s.maxTurns(), money(s.revenueCents()),
                average(s.averageSatisfaction()));
        out.println(LINE);

        out.println("Waiting queue:");
        if (s.waitingQueue().isEmpty()) {
            out.println("  (empty)");
        }
        for (CustomerView c : s.waitingQueue()) {
            out.println("  " + describe(c));
        }

        out.println("Tables:");
        for (TableView t : s.tables()) {
            out.println("  Table " + t.number() + ": "
                    + (t.isFree() ? "free" : describe(t.occupant())));
        }
        out.println("Staff: Waiter 1 task | Chef 1 preparation unit | Cashier automatic");
        out.println(THIN);
        showMenu(new restaurantrush.entity.Menu());
    }

    private void showTurnEnd(GameSnapshot s) {
        out.println(THIN);
        out.printf("End of turn %d: served %d, unhappy departures %d, revenue %s,%n",
                s.turn(), s.servedCount(), s.departures(), money(s.revenueCents()));
        out.printf("  avg satisfaction %s, turns used %d of %d%n",
                average(s.averageSatisfaction()), s.turn(), s.maxTurns());
    }

    private void showFinalResult() {
        GameSnapshot s = controller.snapshot();
        out.println();
        out.println(LINE);
        out.println(controller.isVictory() ? "  VICTORY!" : "  DEFEAT");
        out.println(LINE);
        out.printf("Paid customers:   %d (target %d)%n", s.paidCount(), VICTORY_PAID);
        out.printf("Revenue:          %s (target %s)%n",
                money(s.revenueCents()), money(VICTORY_REVENUE_CENTS));
        out.printf("Avg satisfaction: %s (target %d)%n",
                average(s.averageSatisfaction()), VICTORY_SATISFACTION);
    }

    private void printEvents(List<String> events) {
        for (String event : events) {
            out.println("> " + event);
        }
    }

    private static String describe(CustomerView c) {
        String text = String.format("C%d %-8s satisfaction %3d  %s",
                c.id(), c.type(), c.satisfaction(), c.status());
        if (c.hasOrder()) {
            text += String.format("  | %s %d/%d units, %s",
                    c.item(), c.unitsDone(), c.unitsTotal(), c.orderStatus());
        }
        return text;
    }

    /**
     * Formats an amount in cents as dollars with two decimals, e.g. 1830 as $18.30.
     *
     * @param cents amount in cents
     * @return the formatted amount
     */
    static String money(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
    private void showMenu(restaurantrush.entity.Menu menu) {
        out.println("Available Menu Items:");
        for (restaurantrush.entity.Orderable item : menu.items()) {
            out.printf("  - %-7s : %5s (%d turns)%n", 
                item.displayName(), 
                money(item.priceCents()), 
                item.prepUnits()
            );
        }
        out.println(THIN);
    }
    private static String average(OptionalDouble value) {
        return value.isPresent() ? String.format("%.1f", value.getAsDouble()) : "N/A";
    }
}
