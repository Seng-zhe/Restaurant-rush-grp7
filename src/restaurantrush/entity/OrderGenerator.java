package restaurantrush.entity;

import java.util.List;
import java.util.Random;

/**
 * Decides which dish a customer orders.
 *
 * <p>Uses a {@link Random} with a fixed seed, so the sequence of dishes is the
 * same on every run and demo files replay identically. With seed 42 and the
 * base menu the orders taken are, in order: Pasta, Salad, Salad, Pasta, Salad,
 * Burger (C1 to C6 when orders are taken in arrival order).</p>
 */
public final class OrderGenerator {

    /** Fixed seed so every game run is reproducible. */
    public static final long SEED = 42L;

    private static final Random RANDOM = new Random(SEED);

    private OrderGenerator() {
    }

    /**
     * Picks the next dish from the menu.
     *
     * @param menu the menu to choose from
     * @return the chosen dish
     * @throws IllegalStateException if the menu is empty
     */
    public static Orderable chooseDish(Menu menu) {
        List<Orderable> items = menu.items();
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot choose a dish from an empty menu.");
        }
        return items.get(RANDOM.nextInt(items.size()));
    }
}
