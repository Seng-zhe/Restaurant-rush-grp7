package restaurantrush.entity;

/**
 * Anything a customer can order: a single menu item now, and a combo meal
 * in Stage 3.
 *
 * <p>{@link Order} depends only on this interface, so new kinds of dish can be
 * added without changing {@code Order}.</p>
 */
public interface Orderable {

    /**
     * Returns the name shown to the player.
     *
     * @return the dish name, e.g. "Pasta"
     */
    String displayName();

    /**
     * Returns the full price before any discount.
     *
     * @return price in cents
     */
    int priceCents();

    /**
     * Returns how many Chef turns the dish needs.
     *
     * @return preparation units, at least 1
     */
    int prepUnits();
}
