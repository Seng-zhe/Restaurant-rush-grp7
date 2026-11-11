package restaurantrush.entity;

import java.util.Optional;

/**
 * The Cashier: works automatically in step 4 of each turn, charging one
 * customer who is ready to pay.
 *
 * <p>Like every staff member, the Cashier does one task per turn. If more
 * than one customer can pay, the one at the lowest-numbered table pays first
 * and the others wait until the next turn.</p>
 */
public class Cashier extends StaffMember {

    /**
     * Charges the first seated customer who can pay this turn, adds the bill
     * to revenue and frees the table.
     *
     * @param restaurant the restaurant to collect in
     * @param turn       the current turn
     * @return the customer who paid, or empty if nobody could pay
     */
    public Optional<Customer> collectPayment(Restaurant restaurant, int turn) {
        if (hasActed()) {
            return Optional.empty();
        }
        for (Table table : restaurant.getTables()) {
            Customer customer = table.getOccupant();
            if (customer != null && customer.canPay(turn)) {
                customer.pay();
                restaurant.recordPayment(customer);
                markUsed();
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }
}
