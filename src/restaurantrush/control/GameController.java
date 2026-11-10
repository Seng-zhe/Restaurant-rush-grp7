package restaurantrush.control;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import restaurantrush.entity.Cashier;
import restaurantrush.entity.Chef;
import restaurantrush.entity.Customer;
import restaurantrush.entity.Money;
import restaurantrush.entity.Order;
import restaurantrush.entity.Restaurant;
import restaurantrush.entity.StaffMember;
import restaurantrush.entity.Table;
import restaurantrush.entity.Waiter;
import restaurantrush.entity.VictoryChecker; 
/**
 * Runs the six steps of each turn and applies all game rules.
 *
 * <ol>
 *   <li>{@link #startTurn()}: the scheduled customer arrives.</li>
 *   <li>Waiter task: seat, take an order, serve, or wait.</li>
 *   <li>Chef task: one preparation unit, or wait.</li>
 *   <li>{@link #endTurn()}: customers finish eating; the Cashier charges one
 *       customer who is ready to pay.</li>
 *   <li>{@link #endTurn()}: customers still waiting for service lose
 *       satisfaction; anyone at 0 leaves.</li>
 *   <li>{@link #isVictory()}: after the last turn, the targets are checked.</li>
 * </ol>
 *
 * <p>The public method signatures are the contract with {@code ConsoleUI}.</p>
 */
public class GameController {

    /** Last turn of the base setting. */
    public static final int MAX_TURNS = 18;

    /** Number of tables in the base setting. */
    public static final int TABLE_COUNT = 2;

    private final Restaurant restaurant = new Restaurant(TABLE_COUNT);
    private final ArrivalSchedule arrivals = new ArrivalSchedule();
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();
    private final List<StaffMember> staff = List.of(waiter, chef, cashier);
    private int turn;
    private boolean turnActive;
    private final VictoryChecker victoryChecker = new VictoryChecker();

    /**
     * Step 1: advances to the next turn, makes every staff member available
     * again and adds the scheduled arrival to the waiting queue.
     *
     * @return messages describing what happened
     */
    public List<String> startTurn() {
        List<String> events = new ArrayList<>();
        if (turnActive) {
            events.add("The current turn has not ended yet.");
            return events;
        }
        if (isGameOver()) {
            events.add("The game is over. No more turns can be played.");
            return events;
        }
        turn++;
        turnActive = true;
        staff.forEach(StaffMember::resetForTurn);
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
     * Waiter task: seats a waiting customer at the first free table.
     *
     * @param customerId number of the customer to seat
     * @return the outcome; refused if the customer is not waiting or no table is free
     */
    public ActionResult waiterSeat(int customerId) {
        Optional<ActionResult> refused = checkTurn(waiter, "Waiter");
        if (refused.isPresent()) {
            return refused.get();
        }
        Optional<Customer> customer = restaurant.findPresentCustomer(customerId);
        if (customer.isEmpty()) {
            return notHere(customerId);
        }
        Optional<Table> table = restaurant.freeTable();
        if (table.isEmpty()) {
            return ActionResult.failure("No table is free.");
        }
        return waiter.seat(restaurant, customer.get(), table.get());
    }

    /**
     * Waiter task: takes a seated customer's order. The customer chooses the dish.
     *
     * @param customerId number of the seated customer
     * @return the outcome, including the dish chosen and its price
     */
    public ActionResult waiterTakeOrder(int customerId) {
        Optional<ActionResult> refused = checkTurn(waiter, "Waiter");
        if (refused.isPresent()) {
            return refused.get();
        }
        Optional<Customer> customer = restaurant.findPresentCustomer(customerId);
        if (customer.isEmpty()) {
            return notHere(customerId);
        }
        return waiter.takeOrder(customer.get(), restaurant.getMenu(),null);
    }

    /**
     * Waiter task: serves a customer's order that was already READY.
     *
     * @param customerId number of the customer whose order to serve
     * @return the outcome; refused if the order is not READY
     */
    public ActionResult waiterServe(int customerId) {
        Optional<ActionResult> refused = checkTurn(waiter, "Waiter");
        if (refused.isPresent()) {
            return refused.get();
        }
        Optional<Customer> customer = restaurant.findPresentCustomer(customerId);
        if (customer.isEmpty()) {
            return notHere(customerId);
        }
        ActionResult result = waiter.serve(customer.get(), turn);
        if (result.ok()) {
            restaurant.recordServed();
        }
        return result;
    }

    /**
     * Waiter does nothing this turn.
     *
     * @return successful unless no turn is running or the Waiter already acted
     */
    public ActionResult waiterWait() {
        Optional<ActionResult> refused = checkTurn(waiter, "Waiter");
        return refused.orElseGet(() -> {
            waiter.skipTurn();
            return ActionResult.success("Waiter waits.");
        });
    }

    /**
     * Chef task: advances a customer's order by one preparation unit.
     *
     * @param customerId number of the customer whose order to prepare
     * @return the outcome; refused if the order is not in preparation
     */
    public ActionResult chefWork(int customerId) {
        Optional<ActionResult> refused = checkTurn(chef, "Chef");
        if (refused.isPresent()) {
            return refused.get();
        }
        Optional<Customer> customer = restaurant.findPresentCustomer(customerId);
        if (customer.isEmpty()) {
            return notHere(customerId);
        }
        return chef.prepare(customerId, customer.get().getOrder());
    }

    /**
     * Chef does nothing this turn.
     *
     * @return successful unless no turn is running or the Chef already acted
     */
    public ActionResult chefWait() {
        Optional<ActionResult> refused = checkTurn(chef, "Chef");
        return refused.orElseGet(() -> {
            chef.skipTurn();
            return ActionResult.success("Chef waits.");
        });
    }

    /**
     * Steps 4 and 5: customers finish eating and the Cashier charges one
     * customer who is ready to pay; then everyone still waiting for service
     * loses satisfaction and anyone at 0 leaves.
     *
     * @return messages describing what happened
     */
    public List<String> endTurn() {
        List<String> events = new ArrayList<>();
        if (!turnActive) {
            events.add("No turn is running.");
            return events;
        }
        turnActive = false;

        // Step 4a: the Cashier charges a customer who finished eating on an earlier turn.
        cashier.collectPayment(restaurant, turn).ifPresent(c -> events.add(
                "Cashier charges " + label(c) + " " + Money.format(c.getOrder().getBillCents())
                + " (satisfaction " + c.getSatisfaction() + "). Their table is free."));

        // Step 4b: customers served on an earlier turn finish eating.
        for (Customer c : restaurant.presentCustomers()) {
            if (c.finishEatingIfDone(turn)) {
                events.add(label(c) + " finishes eating and is ready to pay.");
            }
        }

        // Step 5: waiting customers lose satisfaction; anyone at 0 leaves.
        for (Customer c : restaurant.presentCustomers()) {
            if (c.applyWaitingLoss(null)) {
                restaurant.recordDeparture(c);
                events.add(label(c) + " has run out of patience and leaves without paying.");
            }
        }
        return events;
    }

    /**
     * Tells whether all turns have been played.
     *
     * @return {@code true} once the last turn has ended
     */
    public boolean isGameOver() {
        return turn >= MAX_TURNS && !turnActive;
    }

    /**
     * Step 6: checks all three victory targets.
     *
     * @return {@code true} if the player has won
     */
    public boolean isVictory() {
        return victoryChecker.check(restaurant);
    }

    private Optional<ActionResult> checkTurn(StaffMember member, String role) {
        if (!turnActive) {
            return Optional.of(ActionResult.failure("No turn is running."));
        }
        if (member.hasActed()) {
            return Optional.of(ActionResult.failure("The " + role + " has already acted this turn."));
        }
        return Optional.empty();
    }

    private static ActionResult notHere(int customerId) {
        return ActionResult.failure("There is no customer C" + customerId + " in the restaurant.");
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
}
