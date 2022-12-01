package de.legoshi.parkourcalculator.parkour.tick;

public class InputTick {

    public boolean W, A, S, D;
    public boolean JUMP, SPRINT, SNEAK;

    public float YAW = 0;

    private boolean selected = false;
    private boolean below = false;

    public InputTick() {
        this.W = false;
        this.A = false;
        this.S = false;
        this.D = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setBelow(boolean below) {
        this.below = below;
    }

}
