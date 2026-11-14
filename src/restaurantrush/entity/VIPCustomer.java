package restaurantrush.entity;

/**
 * A VIP customer: more patient than a Regular, losing 5 satisfaction per
 * waiting turn, and rewarded with a 10% discount on every bill.
 *
 * <p>Only the two overridden methods differ from {@link Customer}; the
 * Waiter and the controller treat a VIP like any other customer.</p>
 */
public class VIPCustomer extends Customer {

    private static final int PENALTY = 5;
    private static final int DISCOUNT_PERCENT = 10;

    /**
     * Creates a VIP customer.
     *
     * @param id customer number shown to the player
     */
    public VIPCustomer(int id) {
        super(id);
    }

    /** {@inheritDoc} */
    @Override
    public int waitingPenalty() {
        return PENALTY;
    }

    /** {@inheritDoc} */
    @Override
    public int discountPercent() {
        return DISCOUNT_PERCENT;
    }

    /** {@inheritDoc} */
    @Override
    public String typeName() {
        return "VIP";
    }
}
