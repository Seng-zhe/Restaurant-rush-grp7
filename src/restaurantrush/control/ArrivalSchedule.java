package restaurantrush.control;

import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import restaurantrush.entity.CriticCustomer;
import restaurantrush.entity.Customer;
import restaurantrush.entity.RegularCustomer;
import restaurantrush.entity.VIPCustomer;

/**
 * Decides which customer arrives on which turn.
 *
 * <p>Base setting: one customer at the start of turns 1, 4, 7, 10, 13 and 16,
 * six in total, numbered C1 to C6 in arrival order.</p>
 *
 * <p>Stage 2 mix, fixed so every run is the same: C1 Regular, C2 VIP,
 * C3 Critic, C4 Regular, C5 VIP, C6 Critic.</p>
 */
public class ArrivalSchedule {

    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

    /** Customer type for each arrival, in order; the constructors take the id. */
    private static final List<IntFunction<Customer>> ARRIVAL_TYPES = List.of(
            RegularCustomer::new, VIPCustomer::new, CriticCustomer::new,
            RegularCustomer::new, VIPCustomer::new, CriticCustomer::new);

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
     * Creates the customer for an arrival slot, using the type listed for
     * that slot in {@link #ARRIVAL_TYPES}.
     *
     * @param id the new customer's number, starting at 1
     * @return the new customer
     */
    private Customer createCustomer(int id) {
        return ARRIVAL_TYPES.get(id - 1).apply(id);
    }
}
