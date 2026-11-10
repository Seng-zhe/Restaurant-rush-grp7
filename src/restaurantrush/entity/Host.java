package restaurantrush.entity;

import restaurantrush.control.ActionResult;

/**
 * Stage 3 Feature: Host role.
 * Seats at most one queued customer per turn,
 * independently of the Waiter's task.
 */
public class Host extends StaffMember {

    public Host() {
        super();
    }

    /**
     * Seats a waiting customer at the first free table.
     */
    public ActionResult seat(Customer customer, Restaurant restaurant) {

        if (hasActed()) {
            return ActionResult.failure(
                "The Host has already performed a task this turn."
            );
        }

        if (customer == null) {
            return ActionResult.failure(
                "No customers are currently in the waiting queue."
            );
        }

        if (!restaurant.getQueue().contains(customer)) {
            return ActionResult.failure(
                "Customer C" + customer.getId() + " is not waiting."
            );
        }

        var availableTable = restaurant.freeTable();

        if (availableTable.isEmpty()) {
            return ActionResult.failure(
                "Cannot seat customer: No tables are free."
            );
        }

        restaurant.seat(customer, availableTable.get());
        markUsed();

        return ActionResult.success(
            "Host seats C" + customer.getId()
            + " at Table " + availableTable.get().getNumber() + "."
        );
    }
}