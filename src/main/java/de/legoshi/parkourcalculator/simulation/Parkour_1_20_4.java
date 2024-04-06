package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_12;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_20_4;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_12;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_20_4;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_12;
import de.legoshi.parkourcalculator.simulation.player.Player_1_20_4;

public class Parkour_1_20_4 extends Parkour {

    public Parkour_1_20_4() {
        this.player = new Player_1_12(DEFAULT_START, DEFAULT_VELOCITY, START_YAW, DEFAULT_POTION_EFFECTS);
        this.blockManager = new BlockManager_1_12();
        this.movement = new Movement_1_12(player, blockManager);
    }

    public Parkour_1_20_4(Player player, BlockManager blockManager, Movement movement) {
        this.player = player;
        this.blockManager = blockManager;
        this.movement = movement;
    }

    @Override
    public Parkour clone() {
        Player_1_20_4 clonePlayer = (Player_1_20_4) player.clone();
        BlockManager_1_20_4 cloneBlockManager = (BlockManager_1_20_4) blockManager.clone();
        Movement_1_20_4 cloneMovement = new Movement_1_20_4(clonePlayer, cloneBlockManager);
        return new Parkour_1_20_4(clonePlayer, cloneBlockManager, cloneMovement);
    }

}
