package restaurantrush.entity;

/**
 * A member of staff who can do one task per turn.
 *
 * <p>Each subclass calls {@link #markUsed()} after a successful task, and the
 * controller calls {@link #resetForTurn()} at the start of every turn.</p>
 */
public abstract class StaffMember {

    private boolean usedThisTurn;

    /**
     * Tells whether this staff member has already done their task this turn.
     *
     * @return {@code true} if the task is used up
     */
    public boolean hasActed() {
        return usedThisTurn;
    }

    /** Makes the staff member available again for a new turn. */
    public void resetForTurn() {
        usedThisTurn = false;
    }

    /** Uses up this turn's task without doing anything (the "Wait" choice). */
    public void skipTurn() {
        markUsed();
    }

    /** Records that this turn's task has been used. */
    protected void markUsed() {
        usedThisTurn = true;
    }
}
