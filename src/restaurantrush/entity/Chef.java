package restaurantrush.entity;

import restaurantrush.control.ActionResult;

public class Chef extends StaffMember {

    public Chef() {
        super();
    }

    // Stage 1 Action: Cook an order to advance its preparation units
    public ActionResult prepare(Order order) {
        if (hasActed()) {
            return new ActionResult(false, "Chef has already performed an action this turn.");
        }
        if (order.getStatus() != OrderStatus.IN_PREPARATION) {
            return new ActionResult(false, "Order is not currently in preparation state.");
        }

        // Call your teammate's verified advance method from Order.java
        order.advance(); 
        
        // Mark that this staff member has used their single action for this turn
        this.usedThisTurn = true;

        return new ActionResult(true, "Advanced preparation units for the order successfully.");
    }
}