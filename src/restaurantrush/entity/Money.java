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
}
