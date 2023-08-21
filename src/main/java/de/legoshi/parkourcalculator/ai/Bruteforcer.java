package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.List;

public class Bruteforcer {

    private final PositionVisualizer positionVisualizer;
    private final InputTickManager inputTickManager;

    private final Parkour parkour;
    private ABlock startBlock;
    private ABlock endBlock;

    private double precision;

    public Bruteforcer(PositionVisualizer positionVisualizer, InputTickManager inputTickManager, Parkour parkour) {
        this.positionVisualizer = positionVisualizer;
        this.inputTickManager = inputTickManager;
        this.parkour = parkour;
    }

    public void applyAndBruteforce(ABlock s, ABlock e, double p) {
        this.startBlock = s;
        this.endBlock = e;
        this.precision = p;
        this.bruteforce();
    }

    private void bruteforce() {
        Vec3 bestStartPos = parkour.getPlayer().getStartPos();

        for (AxisVecTuple startTuple : startBlock.getAxisVecTuples()) {
            AxisAlignedBB startAABB = startTuple.getBb();
            for (double startZ = startAABB.minZ-0.3f; startZ <= startAABB.maxZ+0.3f; startZ += precision) {
                Vec3 startPos = new Vec3(bestStartPos.x, bestStartPos.y, startZ);
                PlayerTickInformation landTick = getLandTick(startPos);
                if (landTick != null) {
                    if (isImprovement(startPos, bestStartPos)) {
                        bestStartPos = startPos;
                    }
                }
            }
        }

        parkour.getPlayer().setStartPos(bestStartPos.copy());
        positionVisualizer.generatePlayerPath();
    }

    private boolean isImprovement(Vec3 newStartPos, Vec3 bestStartPos) {
        return newStartPos.z >= bestStartPos.z;
    }

    private PlayerTickInformation getLandTick(Vec3 vec3) {
        parkour.getPlayer().setStartPos(vec3.copy());
        Movement movement = parkour.getMovement();
        List<ABlock> blocks =  movement.getCollidingBoundingBoxes(parkour.getPlayer().getStartBB());
        boolean isInBlock = !blocks.isEmpty();
        if (isInBlock) return null;
        return movement.getLandOnBlock(inputTickManager.getInputTicks(), endBlock);
    }

}
