package restaurantrush.entity;

public abstract class StaffMember {
    protected boolean usedThisTurn;

    public StaffMember() {
        this.usedThisTurn = false;
    }

    public boolean hasActed() {
        return this.usedThisTurn;
    }

    public void resetForTurn() {
        this.usedThisTurn = false;
    }
}