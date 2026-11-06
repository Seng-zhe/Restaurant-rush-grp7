package restaurantrush.entity;

/**
 * Where a customer is in their visit, from arrival to leaving.
 */
public enum CustomerStatus {

    /** In the waiting queue, not yet seated. */
    WAITING,

    /** Seated at a table, order not yet taken. */
    SEATED,

    /** Order taken, waiting for the dish to be served. */
    ORDERED,

    /** Dish served; the customer is eating. */
    SERVED,

    /** Finished eating, waiting for the Cashier. */
    READY_TO_PAY,

    /** Has paid and left the table. */
    PAID,

    /** Left unhappy without paying. */
    LEFT;

    /**
     * Tells whether a customer in this status is still waiting for service
     * and so loses satisfaction at the end of each turn.
     *
     * @return {@code true} for WAITING, SEATED and ORDERED
     */
    public boolean isAwaitingService() {
        return this == WAITING || this == SEATED || this == ORDERED;
    }
}
