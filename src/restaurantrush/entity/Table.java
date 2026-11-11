package restaurantrush.entity;

/**
 * A table that seats one customer at a time.
 *
 * <p>It stays occupied until its customer pays or leaves.</p>
 */
public class Table {

    private final int number;
    private Customer occupant;

    /**
     * Creates an empty table.
     *
     * @param number table number shown to the player, starting at 1
     */
    public Table(int number) {
        this.number = number;
    }

    /**
     * Tells whether the table is free.
     *
     * @return {@code true} if nobody is seated here
     */
    public boolean isFree() {
        return occupant == null;
    }

    /**
     * Seats a customer here.
     *
     * @param customer the customer to seat
     * @throws IllegalStateException if the table is already occupied
     */
    public void seat(Customer customer) {
        if (!isFree()) {
            throw new IllegalStateException("Table " + number + " is occupied.");
        }
        occupant = customer;
    }

    /** Frees the table after its customer pays or leaves. */
    public void clear() {
        occupant = null;
    }

    /**
     * Returns the table number.
     *
     * @return the number, starting at 1
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the seated customer.
     *
     * @return the occupant, or {@code null} if free
     */
    public Customer getOccupant() {
        return occupant;
    }
}
