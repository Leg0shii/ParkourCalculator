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

    public InputTick copy() {
        InputTick inputTick = new InputTick();
        inputTick.W = this.W;
        inputTick.A = this.A;
        inputTick.S = this.S;
        inputTick.D = this.D;
        inputTick.JUMP = this.JUMP;
        inputTick.SPRINT = this.SPRINT;
        inputTick.SNEAK = this.SNEAK;
        inputTick.YAW = this.YAW;
        return inputTick;
    }

}
