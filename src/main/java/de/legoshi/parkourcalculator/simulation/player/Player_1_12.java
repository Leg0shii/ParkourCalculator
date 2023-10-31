package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.util.Vec3;

public class Player_1_12 extends Player {

    public Player_1_12(Vec3 position, Vec3 velocity, float startYaw) {
        super(position, velocity, startYaw);
    }
    
    @Override
    public Player clone() {
        return new Player_1_12(this.startPos.copy(), this.startVel.copy(), this.startYAW);
    }
    
}
