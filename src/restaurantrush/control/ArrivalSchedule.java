package restaurantrush.control;

import java.util.Optional;
import restaurantrush.entity.Customer;
import restaurantrush.entity.RegularCustomer;

/**
 * Decides which customer arrives on which turn.
 *
 * <p>Base setting: one customer at the start of turns 1, 4, 7, 10, 13 and 16,
 * six in total, numbered C1 to C6 in arrival order.</p>
 */
public class ArrivalSchedule {

    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

    private int nextId = 1;

    /**
     * Returns the customer who arrives at the start of a turn, if any.
     *
     * @param turn the turn that is starting
     * @return the new customer, or empty if nobody arrives this turn
     */
    public Optional<Customer> customerArrivingOn(int turn) {
        for (int arrivalTurn : ARRIVAL_TURNS) {
            if (arrivalTurn == turn) {
                return Optional.of(createCustomer(nextId++));
            }
        }
        return Optional.empty();
    }

    /**
     * Creates the customer for an arrival slot. Stage 2 changes this to bring
     * in VIP and Critic customers.
     *
     * @param id the new customer's number
     * @return the new customer
     */
    private Customer createCustomer(int id) {
        return new RegularCustomer(id);
    }
}
