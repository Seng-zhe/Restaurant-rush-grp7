package restaurantrush.entity;

/**
 * Stage 3 Feature: ComboMeal.
 * Combines two existing Orderable menu items into a package at a 10% discount.
 */
public class ComboMeal implements Orderable {
    private final String name;
    private final Orderable item1;
    private final Orderable item2;

    /**
     * Constructs a Combo Meal using two existing items from the menu.
     */
    public ComboMeal(String name, Orderable item1, Orderable item2) {
        this.name = name;
        this.item1 = item1;
        this.item2 = item2;
    }

    // --- CHANGED TO MATCH YOUR INTERFACE ---
    @Override
    public String displayName() {
        return this.name;
    }

    // --- CHANGED TO MATCH YOUR INTERFACE ---
    @Override
    public int prepUnits() {
        return item1.prepUnits() + item2.prepUnits();
    }

    // This method is already correct!
    @Override
    public int priceCents() {
        int combinedSum = item1.priceCents() + item2.priceCents();
        return (int) (combinedSum * 0.9);
    }
}