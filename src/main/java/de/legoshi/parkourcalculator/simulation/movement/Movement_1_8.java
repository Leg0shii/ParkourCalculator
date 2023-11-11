package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;

public class Movement_1_8 extends Movement {

    public Movement_1_8(Player player, BlockManager blockManager) {
        super(player, blockManager);
    }

    @Override
    public Movement clone() {
        Player_1_8 player_1_8 = (Player_1_8) player.clone();
        return new Movement_1_8(player_1_8, blockManager);
    }

    @Override
    public int tierCalc(double height) {
        double sum = 1.24919;
        double n = 0.00301;
        int i;
        for (i = 4; sum >= height; i++) {
            sum = sum + n;
            n = (n - 0.08) * 0.98;
        }
        return i+1;
    }

    @Override
    public boolean evalDistance(double distance) {
        return distance > 1.24919;
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