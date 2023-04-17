package de.legoshi.parkourcalculator.parkour.tick;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InputTick {

    public boolean W, A, S, D;
    public boolean JUMP, SPRINT, SNEAK = false;

    public float YAW = 0;

    public InputTick() {
        this.W = false;
        this.A = false;
        this.S = false;
        this.D = false;
    }

}
