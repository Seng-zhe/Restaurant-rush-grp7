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

    private int turn = 0;

// Tracks whether a turn is currently active
private boolean turnActive = false;

// Temporary action tracking until StaffMember is implemented
private boolean waiterUsedThisTurn = false;
private boolean chefUsedThisTurn = false;
    /**
     * Step 1: advances to the next turn and adds the scheduled arrival.
     *
     * @return messages describing what happened, e.g. arrivals
     */
    public List<String> startTurn() {

    List<String> events = new ArrayList<>();

    // Cannot start another turn while one is running
    if (turnActive) {
        events.add("Current turn has not ended yet.");
        return events;
    }

    // Cannot exceed the maximum number of turns
    if (isGameOver()) {
        events.add("Game is over. No more turns allowed.");
        return events;
    }

    // Start the new turn
    turn++;
    turnActive = true;

    // Reset staff availability
    waiterUsedThisTurn = false;
    chefUsedThisTurn = false;

    // Temporary customer arrival logic
    if (turn % 3 == 1 && turn <= 16) {
        events.add("Customer C" + (turn / 3 + 1)
                + " (Regular) arrives.");
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

    if (!turnActive) {
    return ActionResult.failure("No active turn is available.");
}

    if (waiterUsedThisTurn) {
        return ActionResult.failure(
            "Waiter has already acted this turn."
        );
    }

    ActionResult result = stubAction(
        "Waiter seats C" + customerId + ".", customerId
    );

    if (result.ok()) {
        waiterUsedThisTurn = true;
    }

    return result;
}

    /**
     * Waiter task: takes a seated customer's order. The customer chooses the dish.
     *
     * @param customerId number of the seated customer
     * @return the outcome, including the dish chosen and its price
     */
    public ActionResult waiterTakeOrder(int customerId) {

    if (!turnActive) {
    return ActionResult.failure("No active turn is available.");
}

    if (waiterUsedThisTurn) {
        return ActionResult.failure("Waiter has already acted this turn.");
    }

    ActionResult result = stubAction(
        "Waiter takes C" + customerId + "'s order.", customerId
    );

    if (result.ok()) {
        waiterUsedThisTurn = true;
    }

    return result;
}

    /**
     * Waiter task: serves a customer's order that was already READY.
     *
     * @param customerId number of the customer whose order to serve
     * @return the outcome; refused if the order is not READY
     */
   public ActionResult waiterServe(int customerId) {

    if (!turnActive) {
    return ActionResult.failure("No active turn is available.");
}

    if (waiterUsedThisTurn) {
        return ActionResult.failure("Waiter has already acted this turn.");
    }

    ActionResult result = stubAction(
        "Waiter serves C" + customerId + ".", customerId
    );

    if (result.ok()) {
        waiterUsedThisTurn = true;
    }

    return result;
}

    /**
     * Waiter does nothing this turn.
     *
     * @return always successful
     */
    public ActionResult waiterWait() {

    if (!turnActive) {
    return ActionResult.failure("No active turn is available.");
}

    if (waiterUsedThisTurn) {
        return ActionResult.failure("Waiter has already acted this turn.");
    }

    waiterUsedThisTurn = true;

    return ActionResult.success("Waiter waits.");
}
    /**
     * Chef task: advances a customer's order by one preparation unit.
     *
     * @param customerId number of the customer whose order to prepare
     * @return the outcome; refused if the order is not in preparation
     */
    public ActionResult chefWork(int customerId) {

    if (!turnActive) {
    return ActionResult.failure("No active turn is available.");
}

    if (chefUsedThisTurn) {
        return ActionResult.failure("Chef has already acted this turn.");
    }

    ActionResult result = stubAction(
        "Chef works on C" + customerId + "'s order.", customerId
    );

    if (result.ok()) {
        chefUsedThisTurn = true;
    }

    return result;
}

    /**
     * Chef does nothing this turn.
     *
     * @return always successful
     */
    public ActionResult chefWait() {

    if (!turnActive) {
    return ActionResult.failure("No active turn is available.");
}

    if (chefUsedThisTurn) {
        return ActionResult.failure("Chef has already acted this turn.");
    }

    chefUsedThisTurn = true;

    return ActionResult.success("Chef waits.");
}

    /**
     * Steps 4 and 5: eating, payment, satisfaction loss and departures.
     *
     * @return messages describing what happened this turn
     */
    public List<String> endTurn() {

    if (!turnActive) {
        return List.of("No active turn to end.");
    }

    turnActive = false;

    // Temporary until restaurant end-turn logic is implemented
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
