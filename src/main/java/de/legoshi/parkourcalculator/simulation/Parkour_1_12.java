package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_12;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_12;
import de.legoshi.parkourcalculator.simulation.player.Player_1_12;

public class Parkour_1_12 extends Parkour {

    public Parkour_1_12() {
        this.player = new Player_1_12(DEFAULT_START, DEFAULT_VELOCITY, START_YAW);
        this.blockManager = new BlockManager_1_12();
        this.movement = new Movement_1_12(player, blockManager);
    }

}
