package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.simulation.potion.PotionEffect;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;

import java.util.List;

public abstract class Parkour {

    public static final float START_YAW = 0.0F;
    public static final Vec3 DEFAULT_START = new Vec3(-0.5, 1.0, 0.5);
    public static final Vec3 DEFAULT_VELOCITY = new Vec3(0, -0.0784000015258789, 0);
    public static final List<PotionEffect> DEFAULT_POTION_EFFECTS = List.of(
            new PotionEffect(Potion.moveSpeed, 0.20000000298023224D),
            new PotionEffect(Potion.moveSlowdown, -0.15000000596046448D),
            new PotionEffect(Potion.jump, -1)
    );

    @Getter protected Player player;
    @Getter protected Movement movement;
    @Getter protected BlockManager blockManager;
    
    public abstract Parkour clone();

}
