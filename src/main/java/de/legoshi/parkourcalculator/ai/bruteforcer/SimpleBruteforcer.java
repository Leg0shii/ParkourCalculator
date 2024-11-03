package de.legoshi.parkourcalculator.ai.bruteforcer;

import de.legoshi.parkourcalculator.ai.BruteforceOptions;
import de.legoshi.parkourcalculator.ai.InputGenerator;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;

public class SimpleBruteforcer implements Callable<Boolean> {

    private static final Logger logger = LogManager.getLogger(SimpleBruteforcer.class.getName());

    @Getter private List<List<InputTick>> possibleSolutions = new ArrayList<>();

    @Setter private BruteforceOptions bruteforceOptions;
    @Setter private InputGenerator inputGenerator;

    private long startTime;

    private Parkour parkour;
    private final Vec3 startPos;
    private final ABlock endBlock;

    public SimpleBruteforcer(Parkour parkour, Vec3 startPos, ABlock endBlock) {
        this.parkour = parkour;
        this.startPos = startPos;
        this.endBlock = endBlock;
    }

    @Override
    public Boolean call() {
        this.parkour = parkour.clone();

        double maxY = parkour.getBlockManager().getBlock(startPos).getAxisVecTuples().get(0).getBb().maxY;
        Vec3 playerStartPos = new Vec3(startPos.x, maxY, startPos.z);

        this.parkour.getPlayer().setStartPos(playerStartPos.copy());
        this.parkour.getPlayer().setPosition(playerStartPos.copy());
        Vec3 startVelocity = Parkour.DEFAULT_VELOCITY.copy();

        // TODO: check if velocity is applied in correct direction
        startVelocity.x = endBlock.getX() <= startPos.getX() ? 0.25 : -0.25;
        startVelocity.z = endBlock.getZ() <= startPos.getZ() ? 0.25 : -0.25;

        float facing = 0;
        if (startVelocity.x < 0) facing = 90;
        if (startVelocity.x > 0) facing = -90;
        if (startVelocity.z < 0) facing = 180;

        this.parkour.getPlayer().setStartVel(startVelocity);
        this.parkour.getPlayer().setStartYAW(facing);

        this.clearBruteforce();
        return this.findPath();
    }

    private boolean findPath() {
        for (int i = 0; i < bruteforceOptions.getNumberOfTrials(); i++) {
            List<InputTick> sample = new ArrayList<>();
            for (int j = 0; j < bruteforceOptions.getTicksPerTrial(); j++) {
                sample.add(inputGenerator.getNextTick());
            }
            possibleSolutions.add(sample);
        }

        for (List<InputTick> ticks : possibleSolutions) {
            if (validate(ticks)) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(List<InputTick> inputTicks) {
        List<PlayerTickInformation> playerInfo = parkour.getMovement().updatePath(inputTicks);
        for (PlayerTickInformation information : playerInfo) {
            Vec3 unRoundedVec = information.getPosition().copy();
            Vec3 roundedVec = getRoundedVec(unRoundedVec);

            Vec3 possibleLBPos = roundedVec.copy();
            possibleLBPos.y--;
            ABlock possibleLB = parkour.getBlockManager().getBlock(possibleLBPos);

            if (!(possibleLB instanceof Air) && possibleLB.getVec3().equals(endBlock.getVec3())) {
                logger.debug("Solution found in: {}ms", System.currentTimeMillis() - startTime);
                return true;
            }
        }
        return false;
    }

    private Vec3 getRoundedVec(Vec3 unRoundedVec) {
        double DIMENSION = bruteforceOptions.getDimension();
        double roundedX = Math.floor(unRoundedVec.x / DIMENSION) * DIMENSION;
        double roundedY = Math.floor(unRoundedVec.y / DIMENSION) * DIMENSION;
        double roundedZ = Math.floor(unRoundedVec.z / DIMENSION) * DIMENSION;

        return new Vec3(roundedX, roundedY, roundedZ);
    }

    private void clearBruteforce() {
        this.startTime = System.currentTimeMillis();
        this.possibleSolutions.clear();
    }
}
