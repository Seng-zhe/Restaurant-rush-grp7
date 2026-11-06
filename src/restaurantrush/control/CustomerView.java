package restaurantrush.control;

/**
 * A read-only picture of one customer, built by the controller for display.
 *
 * <p>The UI never receives the real {@code Customer} object, so it cannot
 * change game state by accident.</p>
 *
 * @param id           customer number shown to the player, e.g. 1 for C1
 * @param type         customer type, e.g. "Regular"
 * @param satisfaction current satisfaction, 0 to 100
 * @param status       customer status, e.g. "WAITING" or "SEATED"
 * @param item         ordered dish name, or {@code null} if no order yet
 * @param unitsDone    preparation units completed so far
 * @param unitsTotal   preparation units the dish needs
 * @param orderStatus  order status, e.g. "READY", or {@code null} if no order
 */
public record CustomerView(int id, String type, int satisfaction, String status,
                           String item, int unitsDone, int unitsTotal,
                           String orderStatus) {

    /**
     * Creates a view of a customer who has not ordered yet.
     *
     * @param id           customer number
     * @param type         customer type
     * @param satisfaction current satisfaction
     * @param status       customer status
     * @return a view with no order details
     */
    public static CustomerView withoutOrder(int id, String type,
                                            int satisfaction, String status) {
        return new CustomerView(id, type, satisfaction, status, null, 0, 0, null);
    }

    /**
     * Tells whether this customer has placed an order.
     *
     * @return {@code true} if an order exists
     */
    public boolean hasOrder() {
        return item != null;
    }
}
