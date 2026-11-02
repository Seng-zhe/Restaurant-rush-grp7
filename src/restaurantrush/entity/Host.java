package restaurantrush.entity;

import java.util.List;
import restaurantrush.control.ActionResult;

/**
 * Stage 3 Feature: Host role.
 * Seats at most one queued customer per turn, in addition to the Waiter's task.
 */
public class Host extends StaffMember {

    public Host() {
        super(); // Inherits tracking flags from StaffMember
    }

    /**
     * Seats the front customer from the queue at an available table.
     * 
     * @param customer The customer at the front of the waiting queue.
     * @param tables The list of available restaurant tables.
     * @return ActionResult indicating success or failure.
     */
    public ActionResult seat(Customer customer, List<Table> tables) {
        // Correctly calls the public parent method from StaffMember
        if (hasActed()) {
            return ActionResult.failure("The Host has already performed a task this turn.");
        }
        
        if (customer == null) {
            return ActionResult.failure("No customers are currently in the waiting queue.");
        }

        // Find the first empty table using your Table::isFree method
        Table availableTable = null;
        for (Table table : tables) {
            if (table.isFree()) {
                availableTable = table;
                break;
            }
        }

        if (availableTable == null) {
            return ActionResult.failure("Cannot seat customer: No tables are free.");
        }

        // --- COMPATIBILITY FIXES FOR YOUR CLASS PROJECT ---
        // 1. Calls your table's explicit seat() method
        availableTable.seat(customer); 
        
        // 2. Uses your enum phase marker to safely update customer state
        customer.seat();
        
        // 3. Employs the parent StaffMember's protected workflow tracker
        this.markUsed(); 
        
        // 4. Returns using your exact ActionResult record factory method and table number
        return ActionResult.success("Host successfully seated " + customer.typeName() + " at Table " + availableTable.getNumber() + ".");
    }
}
