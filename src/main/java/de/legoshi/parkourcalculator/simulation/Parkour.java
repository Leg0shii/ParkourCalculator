package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;

public abstract class Parkour {

    public static final float START_YAW = 0.0F;
    public static final Vec3 DEFAULT_START = new Vec3(-0.5, 1.0, 0.5);
    public static final Vec3 DEFAULT_VELOCITY = new Vec3(0, -0.0784000015258789, 0);

    @Getter protected Player player;
    @Getter protected Movement movement;
    @Getter protected BlockManager blockManager;

}
