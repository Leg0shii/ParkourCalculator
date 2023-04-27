package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_8;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;

public class Parkour_1_8 extends Parkour {

    public Parkour_1_8() {
        this.player = new Player_1_8(DEFAULT_START, DEFAULT_VELOCITY, START_YAW);
        this.blockManager = new BlockManager_1_8();
        this.movement = new Movement_1_8(player, blockManager);
    }

}
