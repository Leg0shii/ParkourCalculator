package de.legoshi.parkourcalculator.parkour.tick;

public class InputTick {

    public boolean W, A, S, D;
    public boolean JUMP, SPRINT, SNEAK;

    public float YAW = 0;

    public InputTick() {
        this.W = false;
        this.A = false;
        this.S = false;
        this.D = false;
    }

}
