package restaurantrush.entity;

import restaurantrush.control.ActionResult;

/**
 * The Chef: each turn, does one preparation unit of one order.
 */
public class Chef extends StaffMember {

    /**
     * Advances an order by one preparation unit.
     *
     * @param customerId number of the customer who placed the order
     * @param order      the order to work on, or {@code null} if none was taken
     * @return the outcome; refused if the order is not in preparation
     */
    public ActionResult prepare(int customerId, Order order) {
        if (hasActed()) {
            return ActionResult.failure("The Chef has already acted this turn.");
        }
        if (order == null || order.getStatus() != OrderStatus.IN_PREPARATION) {
            return ActionResult.failure("C" + customerId + " has no order in preparation.");
        }
        order.advance();
        markUsed();
        String progress = order.getUnitsDone() + "/" + order.getUnitsTotal();
        return ActionResult.success("Chef prepares C" + customerId + "'s "
                + order.getItem().displayName() + " (" + progress + ")"
                + (order.isReady() ? ", now READY." : "."));
    }
}
