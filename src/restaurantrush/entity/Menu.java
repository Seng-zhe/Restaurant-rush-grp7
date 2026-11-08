package restaurantrush.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the available dishes and combo meals in the restaurant.
 * Designed to dynamically expand as new items or combos are added to the game.
 */
public class Menu {
    private final List<Orderable> items;

    /**
     * Initializes the menu and pre-populates it with the standard 
     * core dishes: Salad, Burger, and Pasta.
     */
    public Menu() {
        this.items = new ArrayList<>();
        // Automatically adds all baseline Enum items (Salad, Burger, Pasta)
        Collections.addAll(this.items, MenuItem.values());
    }

    /**
     * Returns an unmodifiable view of the current menu options.
     * Protects the collection from being modified directly outside this class.
     * 
     * @return A list containing all available Orderable dishes
     */
    public List<Orderable> items() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Adds a new combination meal to the active selection list.
     * Used dynamically when initializing or processing Stage 3 mechanics.
     * 
     * @param combo The ComboMeal option to make available to customers
     */
    public void addComboMeal(ComboMeal combo) {
        if (combo != null && !items.contains(combo)) {
            this.items.add(combo);
        }
    }
}