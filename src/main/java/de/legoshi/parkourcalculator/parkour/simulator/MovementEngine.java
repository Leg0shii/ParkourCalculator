package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MovementEngine {

    public Player player;
    public ArrayList<PlayerTickInformation> playerTickInformations;
    public Environment environment;

    public MovementEngine(Player player, Environment environment) {
        this.player = player;
        this.environment = environment;
        this.playerTickInformations = new ArrayList<>();
    }

    public ArrayList<PlayerTickInformation> updatePath(ArrayList<InputTick> inputTicks) {
        playerTickInformations = new ArrayList<>();
        if (inputTicks.size() == 0) return playerTickInformations;

        player.resetPlayer();
        playerTickInformations.add(player.getPlayerTickInformation());

        for (InputTick inputTick : inputTicks) {
            calculateTick(inputTick);
            playerTickInformations.add(player.getPlayerTickInformation());
        }

        return playerTickInformations;
    }

    public void calculateTick(InputTick inputTick) {
        player.applyInput(inputTick);

        if (player.SPRINT && (player.moveForward < 0.8F || player.isCollidedHorizontally)) {
            player.SPRINT = false;
        }

        if (Math.abs(player.velocity.x) < 0.005D) player.velocity.x = 0.0D;
        if (Math.abs(player.velocity.y) < 0.005D) player.velocity.y = 0.0D;
        if (Math.abs(player.velocity.z) < 0.005D) player.velocity.z = 0.0D;

        if (player.JUMP) {
            player.velocity.y = 0.42F;
            if (player.SPRINT) {
                float f = player.YAW * 0.017453292F;
                player.velocity.x = player.velocity.x - MinecraftMathHelper.sin(f) * 0.2F;
                player.velocity.z = player.velocity.z + MinecraftMathHelper.cos(f) * 0.2F;
            }
        }

        player.moveStrafe = player.moveStrafe * 0.98F;
        player.moveForward = player.moveForward * 0.98F;

        float mult = 0.91F;
        if (player.GROUND) mult = mult * player.slipperiness.value;
        float acceleration = 0.16277136F / (mult * mult * mult);

        float movement;
        if (player.SPRINT) movement = 0.130000010133F;
        else if (player.SNEAK) movement = 0.03F;
        else movement = 0.1F;

        float movementFactor;
        if (player.GROUND) movementFactor = movement * acceleration;
        else movementFactor = player.jumpMovementFactor;

        moveFlying(player.moveStrafe, player.moveForward, movementFactor);

        // calculate ladder

        moveEntity(player.velocity.x, player.velocity.y, player.velocity.z);

        player.velocity.y -= 0.08D;
        player.velocity.y *= 0.9800000190734863D;

        player.velocity.x = player.velocity.x * mult;
        player.velocity.z = player.velocity.z * mult;

        player.jumpMovementFactor = 0.02F;

        if (player.SPRINT) {
            player.jumpMovementFactor = (float) ((double) player.jumpMovementFactor + (double) 0.02F * 0.3D);
        }
    }

    private void moveEntity(double x, double y, double z) {

        // apply player movement in web
        if (player.WEB) {
            // update x, y, z values

        }

        // save x, y, z temporarily
        double xOriginal = x;
        double yOriginal = y;
        double zOriginal = z;

        // do some things to x, y, z when player sneaks...
        boolean GROUND_SNEAK = player.GROUND && player.SNEAK;
        if (GROUND_SNEAK) {
            // do calculation when player on ground and sneaking

        }

        // get all colliding BB from extending current position BB by x, y, z
        // NOTE: it takes all blocks for collision checks
        List<AxisAlignedBB> allBlocks = environment.getAllBBs();

        // save playerBB temporarily
        AxisAlignedBB originalBB = player.playerBB;

        // check offsetY for Y from current position AND apply offsetY to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(player.playerBB, y);
        player.playerBB = player.playerBB.offset(0.0D, y, 0.0D);
        boolean onGround_or_yNegative = player.GROUND || yOriginal != y && yOriginal < 0.0D;

        // check offsetX for X from current position AND apply offsetX to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) x = axisalignedbb.calculateXOffset(player.playerBB, x);
        player.playerBB = player.playerBB.offset(x, 0.0D, 0.0D);

        // check offsetZ for Z from current position AND apply offsetZ to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) z = axisalignedbb.calculateZOffset(player.playerBB, z);
        player.playerBB = player.playerBB.offset(0.0D, 0.0D, z);

        // if the player is onGround OR falls AND x or z change:
        if (onGround_or_yNegative && (xOriginal != x || zOriginal != z)) {
            // save offsetY, offsetX, offsetZ and updatedBB
            double shiftedX = x;
            double shiftedY = y;
            double shiftedZ = z;
            AxisAlignedBB shiftedBB = player.playerBB;

            // reset playerBB to start position
            player.playerBB = originalBB;

            // overwrite y to 0.6 (step height)
            y = 0.6D;

            // get all colliding BB from extending current position BB by x, y=0.6, z
            // NOTE: it takes all blocks for collision checks
            allBlocks = environment.getAllBBs();
            AxisAlignedBB axisAlignedBBNoUpdate = player.playerBB;

            // get currentBB extend it by X and Z
            AxisAlignedBB axisAlignedBBXZUpdate = axisAlignedBBNoUpdate.addCoord(xOriginal, 0.0D, zOriginal);

            // check offsetY for Y=0.6 from xzExtendedBB AND apply offsetY to currentBB
            double yWithStep = y;
            for (AxisAlignedBB axisalignedbb : allBlocks) yWithStep = axisalignedbb.calculateYOffset(axisAlignedBBXZUpdate, yWithStep);
            axisAlignedBBNoUpdate = axisAlignedBBNoUpdate.offset(0.0D, yWithStep, 0.0D);

            // check offsetX for X from current position AND apply offsetX to currentBB
            double xWithStep = xOriginal;
            for (AxisAlignedBB axisalignedbb : allBlocks) xWithStep = axisalignedbb.calculateXOffset(axisAlignedBBNoUpdate, xWithStep);
            axisAlignedBBNoUpdate = axisAlignedBBNoUpdate.offset(xWithStep, 0.0D, 0.0D);

            // check offsetZ for Z from current position AND apply offsetZ to currentBB
            double zWithStep = zOriginal;
            for (AxisAlignedBB axisalignedbb : allBlocks) zWithStep = axisalignedbb.calculateZOffset(axisAlignedBBNoUpdate, zWithStep);
            axisAlignedBBNoUpdate = axisAlignedBBNoUpdate.offset(0.0D, 0.0D, zWithStep);

            // get current playerBB and use for further calc
            AxisAlignedBB currentPlayerBB = player.playerBB;

            // check offsetY for Y=0.6 from currentBB AND apply offsetY to currentBB
            double yWithStepNoXZShift = y;
            for (AxisAlignedBB axisalignedbb : allBlocks) yWithStepNoXZShift = axisalignedbb.calculateYOffset(currentPlayerBB, yWithStepNoXZShift);
            currentPlayerBB = currentPlayerBB.offset(0.0D, yWithStepNoXZShift, 0.0D);

            // check offsetX for X from current position AND apply offsetX to currentBB
            double xWithStepNoXZShift = x;
            for (AxisAlignedBB axisalignedbb : allBlocks) xWithStepNoXZShift = axisalignedbb.calculateXOffset(currentPlayerBB, xWithStepNoXZShift);
            currentPlayerBB = currentPlayerBB.offset(xWithStepNoXZShift, 0.0D, 0.0D);

            // check offsetZ for Z from current position AND apply offsetZ to currentBB
            double zWithStepNoXZShift = z;
            for (AxisAlignedBB axisalignedbb : allBlocks) zWithStepNoXZShift = axisalignedbb.calculateZOffset(currentPlayerBB, zWithStepNoXZShift);
            currentPlayerBB = currentPlayerBB.offset(zWithStepNoXZShift, 0.0D, 0.0D);

            // take the furthest suggestions and apply to x, -y, z, update currentBB
            double distanceWithStep = xWithStep * xWithStep + zWithStep * zWithStep;
            double distanceWithStepNoShift = xWithStepNoXZShift * xWithStepNoXZShift + zWithStepNoXZShift * zWithStepNoXZShift;

            // shift current BB down by calculated offsetY
            if (distanceWithStep > distanceWithStepNoShift) {
                x = xWithStep;
                z = zWithStep;
                y = -yWithStep;
                player.playerBB = axisAlignedBBNoUpdate;
            } else {
                x = xWithStepNoXZShift;
                z = zWithStepNoXZShift;
                y = -yWithStepNoXZShift;
                player.playerBB = currentPlayerBB;
            }

            // take the furthest of currentBB and first calculation and apply to x, y, z and update currentBB
            for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(player.playerBB, y);
            player.playerBB = player.playerBB.offset(0.0D, y, 0.0D);

            if (shiftedX * shiftedX + shiftedZ * shiftedZ >= x * x + z * z) {
                x = shiftedX;
                y = shiftedY;
                z = shiftedZ;
                player.playerBB = shiftedBB;
            }
        }

        // update player position toBB
        // updatePlayerBB();
        player.resetPositionToBB();

        // update onGround and isCollided
        player.isCollidedHorizontally = xOriginal != x || zOriginal != z;
        player.isCollidedVertically = yOriginal != y;
        player.GROUND = player.isCollidedVertically && yOriginal < 0.0D;
        player.isCollided = player.isCollidedHorizontally || player.isCollidedVertically;

        // update block below player?

        // if movedX != updatedX -> motionX = 0
        if (xOriginal != x) player.velocity.x = 0.0D;

        // if movedY != updatedY -> motionY = 0
        if (yOriginal != y) player.velocity.y = 0.0D;

        // if movedZ != updatedZ -> motionZ = 0
        if (zOriginal != z) player.velocity.z = 0.0D;
    }

    private void moveFlying(float strafe, float forward, float friction) {
        float speed = strafe * strafe + forward * forward;
        if (speed >= 1.0E-4F) {
            speed = MinecraftMathHelper.sqrt_float(speed);

            if (speed < 1.0F) {
                speed = 1.0F;
            }

            speed = friction / speed;
            strafe = strafe * speed;
            forward = forward * speed;
            float sin = MinecraftMathHelper.sin(player.YAW * (float) Math.PI / 180.0F);
            float cos = MinecraftMathHelper.cos(player.YAW * (float) Math.PI / 180.0F);
            player.velocity.x = player.velocity.x + strafe * cos - forward * sin;
            player.velocity.z = player.velocity.z + forward * cos + strafe * sin;
        }
    }
}
