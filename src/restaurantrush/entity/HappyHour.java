package restaurantrush.entity;

public class HappyHour {
	private int turnsLeft = 0;
    private boolean used = false;

    public boolean activate() {
        if (used) return false;
        this.turnsLeft = 2;
        this.used = true;
        return true;
    }

    public boolean isActive() {
        return turnsLeft > 0;
    }

    public void tick() {
        if (turnsLeft > 0) turnsLeft--;
    }

    /**
     * Takes the base price already calculated by the customer type 
     * and layers the 20% Happy Hour discount on top of it.
     */
    public int applyHappyHourDiscount(int basePriceCents) {
        if (isActive()) {
            return (int) (basePriceCents * 0.8);
        }
        return basePriceCents;
    }
}

