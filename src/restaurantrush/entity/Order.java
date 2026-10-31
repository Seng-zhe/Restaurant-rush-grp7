package restaurantrush.entity;

/**
 * One customer's order: the dish, its kitchen progress and its locked bill.
 *
 * <p>The bill is fixed when the order is taken, so later price changes (such
 * as Happy Hour in Stage 3) never affect an order already placed. Status only
 * changes through the methods below, which reject invalid steps.</p>
 */
public class Order {

    private final Orderable item;
    private final int billCents;
    private int unitsDone;
    private OrderStatus status;

    /**
     * Creates an order and locks its bill.
     *
     * @param item      the dish ordered
     * @param billCents the amount the customer will pay, in cents
     * @throws IllegalArgumentException if {@code item} is null or the bill is negative
     */
    public Order(Orderable item, int billCents) {
        if (item == null) {
            throw new IllegalArgumentException("An order needs a dish.");
        }
        if (billCents < 0) {
            throw new IllegalArgumentException("A bill cannot be negative.");
        }
        this.item = item;
        this.billCents = billCents;
        this.status = OrderStatus.IN_PREPARATION;
    }

    /**
     * Does one preparation unit. When the last unit is done the order becomes
     * READY.
     *
     * @throws IllegalStateException if the order is not in preparation
     */
    public void advance() {
        requireStatus(OrderStatus.IN_PREPARATION, "prepare");
        unitsDone++;
        if (unitsDone == item.prepUnits()) {
            status = OrderStatus.READY;
        }
    }

    /**
     * Marks a READY order as served.
     *
     * @throws IllegalStateException if the order is not READY
     */
    public void serve() {
        requireStatus(OrderStatus.READY, "serve");
        status = OrderStatus.SERVED;
    }

    /**
     * Marks a served order as paid.
     *
     * @throws IllegalStateException if the order has not been served
     */
    public void markPaid() {
        requireStatus(OrderStatus.SERVED, "charge for");
        status = OrderStatus.PAID;
    }

    /**
     * Cancels an unfinished order because the customer left.
     *
     * @throws IllegalStateException if the order was already served or paid
     */
    public void cancel() {
        if (status == OrderStatus.SERVED || status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel an order that was " + status + ".");
        }
        status = OrderStatus.CANCELLED;
    }

    /**
     * Tells whether the order is ready to serve.
     *
     * @return {@code true} if all units are done and it is not yet served
     */
    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    /**
     * Returns the dish ordered.
     *
     * @return the dish
     */
    public Orderable getItem() {
        return item;
    }

    /**
     * Returns the locked bill.
     *
     * @return amount in cents
     */
    public int getBillCents() {
        return billCents;
    }

    /**
     * Returns how many preparation units are done.
     *
     * @return units done so far
     */
    public int getUnitsDone() {
        return unitsDone;
    }

    /**
     * Returns how many preparation units the dish needs in total.
     *
     * @return total units
     */
    public int getUnitsTotal() {
        return item.prepUnits();
    }

    /**
     * Returns the current status.
     *
     * @return the order status
     */
    public OrderStatus getStatus() {
        return status;
    }

    private void requireStatus(OrderStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException(
                    "Cannot " + action + " an order that is " + status + ".");
        }
    }
}
