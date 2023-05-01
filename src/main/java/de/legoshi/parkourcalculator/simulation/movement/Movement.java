package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class Movement {

    public ArrayList<PlayerTickInformation> playerTickInformations;

    public abstract PlayerTickInformation getLandOnBlock(List<InputTick> inputTicks, ABlock aBlock);
    public abstract void resetPlayer();
    public abstract List<PlayerTickInformation> updatePath(List<InputTick> inputTicks);
    public abstract void calculateTick(InputTick inputTick);
    public abstract Player getPlayer();
    // move to player
    public abstract List<ABlock> getCollidingBoundingBoxes(AxisAlignedBB bb);

    public PlayerTickInformation getLandTick(ABlock aBlock) {
        PlayerTickInformation playerTickInformation = null;
        PlayerTickInformation prevTick = null;
        for (PlayerTickInformation pti : playerTickInformations) {
            if (pti.isGround() && prevTick != null && !prevTick.isGround()) {
                for (AxisVecTuple axisVecTuple : aBlock.getAxisVecTuples()) {
                    AxisAlignedBB bb = axisVecTuple.getBb();
                    Vec3 pPos = pti.getPosition();
                    if (bb.minX < pPos.x && bb.maxX > pPos.x && bb.minZ < pPos.z && bb.maxZ > pPos.z) {
                        playerTickInformation = prevTick;
                        break;
                    }
                }
            }
            prevTick = pti;
        }
        return playerTickInformation;
    }

    public PlayerTickInformation getJumpTick() {
        PlayerTickInformation playerTickInformation = null;
        for (PlayerTickInformation pti : playerTickInformations) {
            if (pti.isJump()) playerTickInformation = pti;
        }
        return playerTickInformation;
    }

}
