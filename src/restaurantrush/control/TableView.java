package restaurantrush.control;

/**
 * A read-only picture of one table for display.
 *
 * @param number   table number shown to the player, starting at 1
 * @param occupant the seated customer, or {@code null} if the table is free
 */
public record TableView(int number, CustomerView occupant) {

    /**
     * Tells whether the table is free.
     *
     * @return {@code true} if nobody is seated here
     */
    public boolean isFree() {
        return occupant == null;
    }
}
