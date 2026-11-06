package restaurantrush.entity;

/**
 * The three dishes on the base menu.
 *
 * <p>Prices are stored in cents so money is never a floating-point value.</p>
 */
public enum MenuItem implements Orderable {

    /** Salad: $9.00, 1 preparation unit. */
    SALAD("Salad", 900, 1),

    /** Burger: $15.00, 1 preparation unit. */
    BURGER("Burger", 1500, 1),

    /** Pasta: $18.00, 2 preparation units. */
    PASTA("Pasta", 1800, 2);

    private final String displayName;
    private final int priceCents;
    private final int prepUnits;

    MenuItem(String displayName, int priceCents, int prepUnits) {
        this.displayName = displayName;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /** {@inheritDoc} */
    @Override
    public int priceCents() {
        return priceCents;
    }

    /** {@inheritDoc} */
    @Override
    public int prepUnits() {
        return prepUnits;
    }

    /** {@inheritDoc} */
    @Override
    public String displayName() {
        return displayName;
    }
}
