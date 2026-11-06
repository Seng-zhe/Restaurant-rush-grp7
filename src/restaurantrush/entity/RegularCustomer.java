package restaurantrush.entity;

/**
 * A regular customer: loses 8 satisfaction per waiting turn and pays full price.
 */
public class RegularCustomer extends Customer {

    private static final int PENALTY = 8;

    /**
     * Creates a regular customer.
     *
     * @param id customer number shown to the player
     */
    public RegularCustomer(int id) {
        super(id);
    }

    /** {@inheritDoc} */
    @Override
    public int waitingPenalty() {
        return PENALTY;
    }

    /** {@inheritDoc} */
    @Override
    public String typeName() {
        return "Regular";
    }
}
