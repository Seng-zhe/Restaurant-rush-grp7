package restaurantrush.entity;

import java.util.List;
import java.util.Random;

/**
 * Utility helper class responsible for generating customer food orders.
 * Employs seeded randomness to ensure game runs remain perfectly deterministic.
 */
public class OrderGenerator {

    // A fixed seed guarantees identical choice sequences on every program run
    private static final long SEED = 42L;
    private static final Random random = new Random(SEED);

    /**
     * Inspects the menu options, randomly picks a dish using a seeded generator,
     * builds the new Order tracking instance, and assigns it safely via the customer's state pipeline.
     * 
     * @param customer The Customer placing the food order
     * @param menu The game's active restaurant Menu context
     */
    public static void generateRandomOrder(Customer customer, Menu menu) {
        // Safety check to ensure the customer is actually seated and ready to place an order
        if (customer.getStatus() != CustomerStatus.SEATED) {
            return;
        }

        List<Orderable> availableItems = menu.items();
        if (availableItems.isEmpty()) {
            throw new IllegalStateException("Cannot select a random dish from an empty menu configuration.");
        }

        // Generate a random choice index bounded securely by the current menu layout
        int randomIndex = random.nextInt(availableItems.size());
        Orderable chosenDish = availableItems.get(randomIndex);

        // Construct the new order linking the customer with their selected dish
        // Note: Assumes Order constructor matches standard syntax: Order(Customer c, Orderable o)
        Order newOrder = new Order(chosenDish, chosenDish.priceCents());

        // Hands the completed order back to your teammate's state validation framework
        customer.placeOrder(newOrder);
    }
}