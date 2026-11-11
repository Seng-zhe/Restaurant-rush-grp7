package restaurantrush.entity;

/**
 * Formats money, which the game always stores as whole cents.
 */
public final class Money {

    private Money() {
    }

    /**
     * Formats an amount in cents as dollars with two decimals, e.g. 1830 as $18.30.
     *
     * @param cents amount in cents, zero or more
     * @return the formatted amount
     */
    public static String format(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }

    /**
     * Takes a percentage off an amount, rounding half up to the nearest cent.
     * For example, 10% off 1850 cents is 1665 cents (1665.0), and 10% off 905
     * cents is 815 cents (814.5 rounded up).
     *
     * @param cents   amount in cents, zero or more
     * @param percent discount from 0 to 100
     * @return the discounted amount in cents
     * @throws IllegalArgumentException if {@code percent} is outside 0 to 100
     */
    public static int discount(int cents, int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("A discount must be 0 to 100 percent.");
        }
        // Integer arithmetic: (cents * (100 - percent)) / 100, rounded half up.
        return (cents * (100 - percent) + 50) / 100;
    }
}
