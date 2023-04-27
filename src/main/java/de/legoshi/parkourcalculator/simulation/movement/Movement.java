package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public abstract class Movement {

    public ArrayList<PlayerTickInformation> playerTickInformations;

    public abstract void resetPlayer();
    public abstract List<PlayerTickInformation> updatePath(List<InputTick> inputTicks);
    public abstract PlayerTickInformation getLastTick(List<InputTick> inputTicks);

    public abstract void calculateTick(InputTick inputTick);

    public abstract Player getPlayer();

    // move to player
    public abstract List<ABlock> getCollidingBoundingBoxes(AxisAlignedBB bb);

}
