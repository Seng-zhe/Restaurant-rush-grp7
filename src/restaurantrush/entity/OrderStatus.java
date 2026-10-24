package restaurantrush.entity;

/**
 * The stage an order has reached.
 */
public enum OrderStatus {

    /** In the kitchen; some preparation units are still to be done. */
    IN_PREPARATION,

    /** All units done; waiting for the Waiter to serve it. */
    READY,

    /** Served to the customer. */
    SERVED,

    /** Paid for. */
    PAID,

    /** Cancelled because the customer left. */
    CANCELLED
}
