package restaurantrush.control;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Runs the six steps of each turn and applies all game rules.
 *
 * <p><b>STUB.</b> This version only returns sample data so the console UI can
 * be built and tested before the real rules exist. The public method
 * signatures are the agreed contract between the UI and the game logic:
 * Member A replaces the method bodies, keeping the signatures.</p>
 */
public class GameController {

    /** Last turn of the base setting. */
    public static final int MAX_TURNS = 18;

    private int turn;

    /**
     * Step 1: advances to the next turn and adds the scheduled arrival.
     *
     * @return messages describing what happened, e.g. arrivals
     */
    public List<String> startTurn() {
        turn++;
        List<String> events = new ArrayList<>();
        if (turn % 3 == 1 && turn <= 16) {
            events.add("Customer C" + (turn / 3 + 1) + " (Regular) arrives.");
        }
        return events;
    }

    /**
     * Returns the current state for display.
     *
     * @return a read-only snapshot of the game
     */
    public GameSnapshot snapshot() {
        // Sample data only, so the UI has something to draw.
        CustomerView waiting = CustomerView.withoutOrder(2, "Regular", 100, "WAITING");
        CustomerView seated = new CustomerView(1, "Regular", 84, "ORDERED",
                "Pasta", 1, 2, "IN_PREPARATION");
        return new GameSnapshot(turn, MAX_TURNS, 1800, OptionalDouble.of(76.0),
                1, 1, 0, List.of(waiting),
                List.of(new TableView(1, seated), new TableView(2, null)));
    }

    /**
     * Waiter task: seats a waiting customer at a free table.
     *
     * @param customerId number of the customer to seat
     * @return the outcome; refused if the customer is not waiting or no table is free
     */
    public ActionResult waiterSeat(int customerId) {
        return stubAction("Waiter seats C" + customerId + ".", customerId);
    }

    /**
     * Waiter task: takes a seated customer's order. The customer chooses the dish.
     *
     * @param customerId number of the seated customer
     * @return the outcome, including the dish chosen and its price
     */
    public ActionResult waiterTakeOrder(int customerId) {
        return stubAction("Waiter takes C" + customerId + "'s order.", customerId);
    }

    /**
     * Waiter task: serves a customer's order that was already READY.
     *
     * @param customerId number of the customer whose order to serve
     * @return the outcome; refused if the order is not READY
     */
    public ActionResult waiterServe(int customerId) {
        return stubAction("Waiter serves C" + customerId + ".", customerId);
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
        return stubAction("Chef works on C" + customerId + "'s order.", customerId);
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
     * Steps 4 and 5: eating, payment, satisfaction loss and departures.
     *
     * @return messages describing what happened this turn
     */
    public List<String> endTurn() {
        return List.of("(stub) End of turn " + turn + ".");
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
        return false;
    }

    private ActionResult stubAction(String message, int customerId) {
        if (customerId < 1) {
            return ActionResult.failure("There is no customer C" + customerId + ".");
        }
        return ActionResult.success("(stub) " + message);
    }
}
