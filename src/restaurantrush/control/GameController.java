package restaurantrush.control;

import java.util.ArrayList;
import java.util.List;
import restaurantrush.entity.Customer;
import restaurantrush.entity.Order;
import restaurantrush.entity.Restaurant;
import restaurantrush.entity.Table;

/**
 * Runs the six steps of each turn and applies all game rules.
 *
 * <p>Built so far: arrivals (step 1), the state snapshot, and the end-of-turn
 * satisfaction loss and departures (step 5). Still stubbed: the Waiter and
 * Chef actions (steps 2 and 3), eating and payment (step 4). The public
 * method signatures are the contract with {@code ConsoleUI}; keep them.</p>
 */
public class GameController {

    /** Last turn of the base setting. */
    public static final int MAX_TURNS = 18;

    /** Number of tables in the base setting. */
    public static final int TABLE_COUNT = 2;

    private final Restaurant restaurant = new Restaurant(TABLE_COUNT);
    private final ArrivalSchedule arrivals = new ArrivalSchedule();
    private int turn;

    /**
     * Step 1: advances to the next turn and adds the scheduled arrival to the
     * waiting queue.
     *
     * @return messages describing what happened
     */
    public List<String> startTurn() {
        turn++;
        List<String> events = new ArrayList<>();
        arrivals.customerArrivingOn(turn).ifPresent(c -> {
            restaurant.addArrival(c);
            events.add(label(c) + " arrives and joins the waiting queue.");
        });
        return events;
    }

    /**
     * Returns the current state for display.
     *
     * @return a read-only snapshot of the game
     */
    public GameSnapshot snapshot() {
        List<CustomerView> queueViews = new ArrayList<>();
        for (Customer c : restaurant.getQueue()) {
            queueViews.add(view(c));
        }
        List<TableView> tableViews = new ArrayList<>();
        for (Table t : restaurant.getTables()) {
            tableViews.add(new TableView(t.getNumber(),
                    t.isFree() ? null : view(t.getOccupant())));
        }
        return new GameSnapshot(turn, MAX_TURNS, restaurant.getRevenueCents(),
                restaurant.averageSatisfaction(), restaurant.getPaidCount(),
                restaurant.getServedCount(), restaurant.getDepartures(),
                queueViews, tableViews);
    }

    /**
     * Waiter task: seats a waiting customer at a free table.
     *
     * @param customerId number of the customer to seat
     * @return the outcome; refused if the customer is not waiting or no table is free
     */
    public ActionResult waiterSeat(int customerId) {
        return notBuiltYet();
    }

    /**
     * Waiter task: takes a seated customer's order. The customer chooses the dish.
     *
     * @param customerId number of the seated customer
     * @return the outcome, including the dish chosen and its price
     */
    public ActionResult waiterTakeOrder(int customerId) {
        return notBuiltYet();
    }

    /**
     * Waiter task: serves a customer's order that was already READY.
     *
     * @param customerId number of the customer whose order to serve
     * @return the outcome; refused if the order is not READY
     */
    public ActionResult waiterServe(int customerId) {
        return notBuiltYet();
    }

    /**
     * Waiter does nothing this turn.
     *
     * @return always successful
     */
    public ActionResult waiterWait() {
        return ActionResult.success("Waiter waits.");
    }

    /**
     * Chef task: advances a customer's order by one preparation unit.
     *
     * @param customerId number of the customer whose order to prepare
     * @return the outcome; refused if the order is not in preparation
     */
    public ActionResult chefWork(int customerId) {
        return notBuiltYet();
    }

    /**
     * Chef does nothing this turn.
     *
     * @return always successful
     */
    public ActionResult chefWait() {
        return ActionResult.success("Chef waits.");
    }

    /**
     * Steps 4 and 5: eating and payment (not built yet), then satisfaction
     * loss for everyone still waiting for service, and unhappy departures.
     *
     * @return messages describing what happened this turn
     */
    public List<String> endTurn() {
        List<String> events = new ArrayList<>();
        // Step 4 (eating and payment) goes here.

        // Step 5: waiting customers lose satisfaction; anyone at 0 leaves.
        for (Customer c : restaurant.presentCustomers()) {
            if (c.applyWaitingLoss()) {
                restaurant.recordDeparture(c);
                events.add(label(c) + " has run out of patience and leaves without paying.");
            }
        }
        return events;
    }

    /**
     * Tells whether all turns have been played.
     *
     * @return {@code true} after the last turn
     */
    public boolean isGameOver() {
        return turn >= MAX_TURNS;
    }

    /**
     * Step 6: checks all three victory targets.
     *
     * @return {@code true} if the player has won
     */
    public boolean isVictory() {
        return restaurant.isVictory();
    }

    private static CustomerView view(Customer c) {
        Order o = c.getOrder();
        if (o == null) {
            return CustomerView.withoutOrder(c.getId(), c.typeName(),
                    c.getSatisfaction(), c.getStatus().name());
        }
        return new CustomerView(c.getId(), c.typeName(), c.getSatisfaction(),
                c.getStatus().name(), o.getItem().displayName(), o.getUnitsDone(),
                o.getUnitsTotal(), o.getStatus().name());
    }

    private static String label(Customer c) {
        return "C" + c.getId() + " (" + c.typeName() + ")";
    }

    private static ActionResult notBuiltYet() {
        return ActionResult.failure("This action is not built yet. Choose 0 (Wait).");
    }
}
