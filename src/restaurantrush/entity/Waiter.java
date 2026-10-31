package restaurantrush.entity;

import restaurantrush.control.ActionResult;

public class Waiter extends StaffMember {

    public Waiter() {
        super();
    }

    // Stage 1 Action: Seat a customer from the queue at a free table
    public ActionResult seat(Customer customer, Table table) {
        if (hasActed()) {
            return new ActionResult(false, "Waiter has already performed an action this turn.");
        }
        if (!table.isFree()) {
            return new ActionResult(false, "Table is already occupied.");
        }
        
        table.seat(customer);
        customer.seat(); 
        
        this.usedThisTurn = true;
        return new ActionResult(true, "Customer seated successfully.");
    }

    // Stage 1 Action: Take a seated customer's order using the Orderable interface
    public ActionResult takeOrder(Customer customer, Orderable item) {
        if (hasActed()) {
            return new ActionResult(false, "Waiter has already performed an action this turn.");
        }
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return new ActionResult(false, "Customer is not ready to place an order.");
        }

        // Initialize a new order with the item choice and lock its bill price
        Order order = new Order(item, item.priceCents());
        
        // Link the order to the customer
        customer.placeOrder(order);

        this.usedThisTurn = true;
        return new ActionResult(true, "Order taken successfully for the customer.");
    }

    // Stage 1 Action: Serve a ready order to the customer
    public ActionResult serve(Customer customer) {
        if (hasActed()) {
            return new ActionResult(false, "Waiter has already performed an action this turn.");
        }
        
        Order order = customer.getOrder();
        if (order == null || order.getStatus() != OrderStatus.READY) {
            return new ActionResult(false, "There is no ready order to serve this customer.");
        }

        // Advance the order status to SERVED
        order.serve();
        
        // TODO: Update customer's eating status transition method here once verified below line 72 in Customer.java
        
        this.usedThisTurn = true;
        return new ActionResult(true, "Order served successfully.");
    }
}