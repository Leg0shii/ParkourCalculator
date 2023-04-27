package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.util.Vec3;

public abstract class Parkour {

    protected Player player;
    protected Movement movement;
    protected BlockManager blockManager;

    public static final float START_YAW = 0.0F;
    public static final Vec3 DEFAULT_START = new Vec3(-0.5, 1.0, 0.5);
    public static final Vec3 DEFAULT_VELOCITY = new Vec3(0, -0.0784000015258789, 0);

    public Player getPlayer() {
        return player;
    }

    public Movement getMovement() {
        return movement;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

}
