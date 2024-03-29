package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.util.Vec3;

public class Player_1_8 extends Player {

    public Player_1_8(Vec3 position, Vec3 velocity, float startYAW) {
        super(position, velocity, startYAW);
    }
    
    @Override
    public Player clone() {
        return new Player_1_8(this.startPos.copy(), this.startVel.copy(), this.startYAW);
    }
    
}
