package restaurantrush.entity;

import restaurantrush.control.ActionResult;

/**
 * The Waiter: each turn, seats a customer, takes an order or serves a dish.
 *
 * <p>Every method checks the rules first and returns a refused
 * {@link ActionResult} without changing anything if the task is not allowed,
 * so a refused choice never uses up the Waiter's turn.</p>
 */
public class Waiter extends StaffMember {

    /**
     * Seats a waiting customer at a free table.
     *
     * @param restaurant the restaurant whose queue the customer is in
     * @param customer   the customer to seat
     * @param table      the table to seat them at
     * @return the outcome
     */
    public ActionResult seat(Restaurant restaurant, Customer customer, Table table) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.WAITING) {
            return ActionResult.failure("C" + customer.getId() + " is not in the waiting queue.");
        }
        if (!table.isFree()) {
            return ActionResult.failure("Table " + table.getNumber() + " is occupied.");
        }
        restaurant.seat(customer, table);
        markUsed();
        return ActionResult.success("Waiter seats C" + customer.getId()
                + " at Table " + table.getNumber() + ".");
    }

    /**
     * Takes a seated customer's order. The customer chooses the dish and the
     * bill is locked at the dish's current price.
     *
     * @param customer the seated customer
     * @param menu     the menu the customer chooses from
     * @return the outcome, naming the dish and price
     */
    public ActionResult takeOrder(Customer customer, Menu menu) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.failure("C" + customer.getId()
                    + " is not seated and waiting to order.");
        }
        Orderable dish = customer.chooseFood(menu);
        Order order = new Order(dish, customer.billFor(dish));
        customer.placeOrder(order);
        markUsed();
        return ActionResult.success("Waiter takes C" + customer.getId() + "'s order: "
                + dish.displayName() + " (" + Money.format(order.getBillCents()) + ").");
    }

    /**
     * Serves a READY dish to its customer, who starts eating.
     *
     * @param customer the customer whose order to serve
     * @param turn     the current turn
     * @return the outcome
     */
    public ActionResult serve(Customer customer, int turn) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        Order order = customer.getOrder();
        if (order == null || !order.isReady()) {
            return ActionResult.failure("C" + customer.getId() + " has no READY dish to serve.");
        }
        order.serve();
        String reaction = customer.receiveDish(turn);
        markUsed();
        String message = "Waiter serves " + order.getItem().displayName()
                + " to C" + customer.getId() + ".";
        return ActionResult.success(reaction.isEmpty() ? message : message + " " + reaction);
    }
}
