package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.potion.PotionEffect;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Player_1_8 extends Player {

    public Player_1_8(Vec3 position, Vec3 velocity, float startYAW, List<PotionEffect> eFs) {
        super(position, velocity, startYAW, eFs);
    }
    
    @Override
    public Player clone() {
        return new Player_1_8(this.startPos.copy(), this.startVel.copy(), this.startYAW, new ArrayList<>(this.potionEffects.values()));
    }
    
}
