package restaurantrush.entity;

import restaurantrush.control.ActionResult;


public class Waiter extends StaffMember {

    public Waiter() {
        super();
    }

    /**
     * Seats a waiting customer at a free table.
     * 
     * @param restaurant The restaurant whose queue the customer is in
     * @param customer   The customer to seat
     * @param table      The table to seat them at
     * @return The outcome
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
     * Stage 3 Waiter Action: Takes an order from a seated customer,
     * layers customer type and Happy Hour discounts sequentially,
     * and locks the final calculation into a permanent Order ticket.
     * 
     * @param customer   The seated customer
     * @param restaurant The global restaurant instance containing the state
     * @return The outcome, naming the dish and final price
     */
    public ActionResult takeOrder(Customer customer, Restaurant restaurant) {
        if (hasActed()) {
            return ActionResult.failure("The Waiter has already acted this turn.");
        }
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.failure("C" + customer.getId() 
                    + " is not seated and waiting to order.");
        }

        // 1. Let the customer choose their food from the menu hub
        Orderable dish = customer.chooseFood(restaurant.getMenu());

        if (dish == null) {
            return ActionResult.failure("The customer was unable to find an item on the menu.");
        }

        // 2. Fetch the customer-specific base price (Polymorphic call: Regular vs VIP)
        int calculatedPrice = customer.calculateCustomerBasePrice(dish);

        // 3. Layer Happy Hour on top of that base price if active
        calculatedPrice = restaurant.getHappyHour().applyHappyHourDiscount(calculatedPrice);

        // 4. Instantiate the live Order tracking ticket with the permanent, locked-in price
        Order order = new Order(dish, calculatedPrice);
        customer.placeOrder(order);
        markUsed();

        return ActionResult.success("Waiter takes C" + customer.getId() + "'s order: "
                + dish.displayName() + " (" + Money.format(order.getBillCents()) + ").");
    }

    /**
     * Serves a READY dish to its customer, who starts eating.
     * 
     * @param customer The customer whose order to serve
     * @param turn     The current turn
     * @return The outcome
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
        customer.receiveDish(turn);
        markUsed();
        
        return ActionResult.success("Waiter serves " + order.getItem().displayName() 
                + " to C" + customer.getId() + ".");
    }
}