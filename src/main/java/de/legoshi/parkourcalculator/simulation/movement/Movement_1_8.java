package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;

public class Movement_1_8 extends Movement {

    public Movement_1_8(Player player, BlockManager blockManager) {
        super(player, blockManager);
    }

    public void calculateTick(InputTick inputTick) {
        player.applyInput(inputTick);

        handleWaterMovement();
        handleLavaMovement();

        if (Math.abs(player.velocity.x) < 0.005D) player.velocity.x = 0.0D;
        if (Math.abs(player.velocity.y) < 0.005D) player.velocity.y = 0.0D;
        if (Math.abs(player.velocity.z) < 0.005D) player.velocity.z = 0.0D;

        super.calculateTick(inputTick);
    }

}