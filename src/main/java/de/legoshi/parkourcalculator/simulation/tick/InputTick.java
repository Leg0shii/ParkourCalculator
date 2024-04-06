package de.legoshi.parkourcalculator.simulation.tick;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class InputTick {

    public boolean W, A, S, D;
    public boolean JUMP, SPRINT, SNEAK;
    public boolean ELYTRA;
    public float YAW;

    public InputTick copy() {
        InputTick inputTick = new InputTick();
        inputTick.W = this.W;
        inputTick.A = this.A;
        inputTick.S = this.S;
        inputTick.D = this.D;
        inputTick.JUMP = this.JUMP;
        inputTick.SPRINT = this.SPRINT;
        inputTick.SNEAK = this.SNEAK;
        inputTick.ELYTRA = this.ELYTRA;
        inputTick.YAW = this.YAW;
        return inputTick;
    }

}
