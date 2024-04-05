package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_12;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_12;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_8;
import de.legoshi.parkourcalculator.simulation.player.Player_1_12;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;

public class Parkour_1_12 extends Parkour {

    public Parkour_1_12() {
        this.player = new Player_1_12(DEFAULT_START, DEFAULT_VELOCITY, START_YAW, DEFAULT_POTION_EFFECTS);
        this.blockManager = new BlockManager_1_12();
        this.movement = new Movement_1_12(player, blockManager);
    }
    
    @Override
    public Parkour clone() {
        Player_1_12 clonePlayer = (Player_1_12) player.clone();
        BlockManager_1_12 cloneBlockManager = (BlockManager_1_12) blockManager.clone();
        Movement_1_12 cloneMovement = new Movement_1_12(clonePlayer, cloneBlockManager);
        return new Parkour_1_8(clonePlayer, cloneBlockManager, cloneMovement);
    }
    
}
