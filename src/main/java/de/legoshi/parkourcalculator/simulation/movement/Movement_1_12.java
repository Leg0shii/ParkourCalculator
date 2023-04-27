package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;

import java.util.List;

public class Movement_1_12 extends Movement {

    public Movement_1_12(Player player, BlockManager blockManager) {
        super();
    }

    @Override
    public void resetPlayer() {

    }

    @Override
    public List<PlayerTickInformation> updatePath(List<InputTick> inputTicks) {
        return null;
    }

    @Override
    public PlayerTickInformation getLastTick(List<InputTick> inputTicks) {
        return null;
    }

    @Override
    public void calculateTick(InputTick inputTick) {

    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public List<ABlock> getCollidingBoundingBoxes(AxisAlignedBB bb) {
        return null;
    }

}
