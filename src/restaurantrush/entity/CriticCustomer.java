package restaurantrush.entity;

/**
 * A food critic: loses 10 satisfaction per waiting turn, pays full price and
 * reviews the restaurant the moment the dish is served.
 *
 * <p><b>Special behaviour (the review).</b> When served, the Critic judges
 * the service by their satisfaction at that moment, which already reflects
 * every turn they spent waiting:</p>
 * <ul>
 *   <li>satisfaction {@value #GOOD_REVIEW_THRESHOLD} or more: a glowing
 *       review, satisfaction <b>+{@value #REVIEW_SWING}</b> (capped at 100);</li>
 *   <li>below {@value #GOOD_REVIEW_THRESHOLD}: a harsh review, satisfaction
 *       <b>-{@value #REVIEW_SWING}</b> (not below 0).</li>
 * </ul>
 * <p>A served customer no longer loses satisfaction, so the reviewed value is
 * the one recorded when the Critic pays. In practice the Critic must be
 * served after at most three waiting turns (100 - 3 x 10 = 70) for a good
 * review, which gives the manager a reason to put the Critic first.</p>
 */
public class CriticCustomer extends Customer {

    /** Satisfaction lost per waiting turn. */
    private static final int PENALTY = 12;

    /** Minimum satisfaction at serving time for a good review. */
    public static final int GOOD_REVIEW_THRESHOLD = 70;

    /** Satisfaction gained by a good review or lost by a bad one. */
    public static final int REVIEW_SWING = 15;

    /**
     * Creates a critic.
     *
     * @param id customer number shown to the player
     */
    public CriticCustomer(int id) {
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
        return "Critic";
    }

    /**
     * Writes the review: satisfaction goes up by {@value #REVIEW_SWING} if it
     * is at least {@value #GOOD_REVIEW_THRESHOLD} when served, otherwise down
     * by {@value #REVIEW_SWING}.
     *
     * @param servedOrder the order just served
     * @param turn        the turn it was served on
     * @return the review, for display
     */
    @Override
    protected String onServed(Order servedOrder, int turn) {
        int before = getSatisfaction();
        String dish = servedOrder.getItem().displayName();
        if (before >= GOOD_REVIEW_THRESHOLD) {
            adjustSatisfaction(REVIEW_SWING);
            return "C" + getId() + " writes a glowing review of the " + dish
                    + ": satisfaction " + before + " -> " + getSatisfaction() + ".";
        }
        adjustSatisfaction(-REVIEW_SWING);
        return "C" + getId() + " writes a harsh review of the slow service: satisfaction "
                + before + " -> " + getSatisfaction() + ".";
    }
}
