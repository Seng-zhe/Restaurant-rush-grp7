package restaurantrush.entity;

import java.util.OptionalDouble;

/**
 * Handles the verification of end-of-game victory targets.
 * Decouples scoring parameters from the core Restaurant data state.
 */
public class VictoryChecker {

    // Victory target configurations
    public static final int TARGET_PAID = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_SATISFACTION = 60;

    /**
     * Checks all three victory targets against the provided restaurant state.
     * @param restaurant The current restaurant instance containing game running metrics.
     * @return true if all target metrics are met or exceeded, false otherwise.
     */
    public boolean check(Restaurant restaurant) {
        if (restaurant == null) {
            return false;
        }

        int paidCount = restaurant.getPaidCount();
        int revenue = restaurant.getRevenueCents();
        OptionalDouble avgSatisfaction = restaurant.averageSatisfaction();

        return paidCount >= TARGET_PAID
                && revenue >= TARGET_REVENUE_CENTS
                && avgSatisfaction.orElse(0) >= TARGET_SATISFACTION;
    }
}