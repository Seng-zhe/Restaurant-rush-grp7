package restaurantrush.entity;

/**
 * A customer visiting the restaurant, from arrival to payment or leaving.
 *
 * <p>Behaviour that differs between customer types is a method that
 * subclasses override (for example {@link #waitingPenalty()}), so the game
 * controller never needs to check which type of customer it has.</p>
 *
 * <p>The status only moves forward through the methods below; each one rejects
 * a step that is out of order, so the customer can never, for example, eat
 * before being served.</p>
 */
public abstract class Customer {

    /** Satisfaction every customer starts with. */
    public static final int MAX_SATISFACTION = 100;

    private final int id;
    private int satisfaction = MAX_SATISFACTION;
    private CustomerStatus status = CustomerStatus.WAITING;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;

    /**
     * Creates a customer who joins the waiting queue with full satisfaction.
     *
     * @param id customer number shown to the player, e.g. 1 for C1
     */
    protected Customer(int id) {
        this.id = id;
    }

    /**
     * Returns how many satisfaction points this customer loses for each turn
     * spent waiting for service.
     *
     * @return points lost per waiting turn
     */
    public abstract int waitingPenalty();

    /**
     * Returns the customer type shown to the player.
     *
     * @return the type name, e.g. "Regular"
     */
    public abstract String typeName();

    /**
     * Seats this customer.
     *
     * @throws IllegalStateException if the customer is not waiting
     */
    public void seat() {
        requireStatus(CustomerStatus.WAITING, "seat");
        status = CustomerStatus.SEATED;
    }

    /**
     * Records the order the Waiter has taken.
     *
     * @param newOrder the order placed
     * @throws IllegalStateException if the customer is not seated
     */
    public void placeOrder(Order newOrder) {
        requireStatus(CustomerStatus.SEATED, "take the order of");
        this.order = newOrder;
        status = CustomerStatus.ORDERED;
    }

    /**
     * Receives the served dish and starts eating.
     *
     * @param turn the turn it was served on
     * @throws IllegalStateException if the customer has not ordered
     */
    public void receiveDish(int turn) {
        requireStatus(CustomerStatus.ORDERED, "serve");
        servedTurn = turn;
        status = CustomerStatus.SERVED;
    }

    /**
     * Finishes eating if a full turn has passed since being served.
     *
     * @param turn the current turn
     * @return {@code true} if the customer became READY_TO_PAY this turn
     */
    public boolean finishEatingIfDone(int turn) {
        if (status == CustomerStatus.SERVED && servedTurn < turn) {
            status = CustomerStatus.READY_TO_PAY;
            readyToPayTurn = turn;
            return true;
        }
        return false;
    }

    /**
     * Tells whether the Cashier can charge this customer this turn. A
     * customer who became READY_TO_PAY this turn pays next turn at the earliest.
     *
     * @param turn the current turn
     * @return {@code true} if ready to pay since an earlier turn
     */
    public boolean canPay(int turn) {
        return status == CustomerStatus.READY_TO_PAY && readyToPayTurn < turn;
    }

    /**
     * Pays the bill. The current satisfaction is the value recorded for
     * the average.
     *
     * @throws IllegalStateException if the customer is not ready to pay
     */
    public void pay() {
        requireStatus(CustomerStatus.READY_TO_PAY, "charge");
        order.markPaid();
        status = CustomerStatus.PAID;
    }

    /**
     * Applies the end-of-turn satisfaction loss if still waiting for service.
     * A customer who reaches 0 leaves and any unfinished order is cancelled.
     *
     * @return {@code true} if the customer left this turn
     */
    public boolean applyWaitingLoss() {
        if (!status.isAwaitingService()) {
            return false;
        }
        satisfaction = Math.max(0, satisfaction - waitingPenalty());
        if (satisfaction == 0) {
            if (order != null) {
                order.cancel();
            }
            status = CustomerStatus.LEFT;
            return true;
        }
        return false;
    }

    /**
     * Returns the customer number.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current satisfaction.
     *
     * @return satisfaction from 0 to 100
     */
    public int getSatisfaction() {
        return satisfaction;
    }

    /**
     * Returns the current status.
     *
     * @return the customer status
     */
    public CustomerStatus getStatus() {
        return status;
    }

    /**
     * Returns this customer's order.
     *
     * @return the order, or {@code null} if none has been taken
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Changes satisfaction by the given amount, kept between 0 and 100.
     * Subclasses and later stages use this for reactions and recovery.
     *
     * @param change points to add (negative to subtract)
     */
    protected void adjustSatisfaction(int change) {
        satisfaction = Math.max(0, Math.min(MAX_SATISFACTION, satisfaction + change));
    }

    private void requireStatus(CustomerStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException("Cannot " + action + " C" + id
                    + " who is " + status + ".");
        }
    }
}
