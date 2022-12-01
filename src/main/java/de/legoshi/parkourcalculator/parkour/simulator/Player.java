package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.List;

public class Player {

    public boolean GROUND = true;
    public boolean WEB;

    public boolean SPRINT;
    public boolean SNEAK;
    public boolean JUMP;

    public float YAW;
    public float moveStrafe, moveForward;

    public float jumpMovementFactor = 0.02F;

    private boolean isCollidedHorizontally;
    private boolean isCollidedVertically;
    private boolean isCollided;

    public Movement.Slipperiness slipperiness;

    public Vec3 velocity;
    public Vec3 position;
    public AxisAlignedBB playerBB;

    public Player(Vec3 position, Vec3 velocity) {
        this.position = position;
        this.velocity = velocity;

        slipperiness = Movement.Slipperiness.BLOCK;
        updatePlayerBB();
    }

    private void updateTick(InputTick inputTick) {
        moveStrafe = 0F;
        moveForward = 0F;

        if (inputTick.W) moveForward++;
        if (inputTick.S) moveForward--;
        if (inputTick.A) moveStrafe++;
        if (inputTick.D) moveStrafe--;

        if (SNEAK) {
            moveStrafe = (float) ((double) moveStrafe * 0.3D);
            moveForward = (float) ((double) moveForward * 0.3D);
        }

        if (moveForward < 0.8F) this.SPRINT = false;
    }

    private void applyInput(InputTick inputTick) {
        updateTick(inputTick);
        YAW = inputTick.YAW;
        JUMP = inputTick.JUMP && GROUND;
        SPRINT = inputTick.SPRINT || SPRINT;
        SNEAK = inputTick.SNEAK;
    }


    public void calculateTick(InputTick inputTick) {
        applyInput(inputTick);

        if (SPRINT && (this.moveForward < 0.8F || this.isCollidedHorizontally)) {
            SPRINT = false;
        }

        if (Math.abs(velocity.x) < 0.005D) velocity.x = 0.0D;
        if (Math.abs(velocity.y) < 0.005D) velocity.y = 0.0D;
        if (Math.abs(velocity.z) < 0.005D) velocity.z = 0.0D;

        if (JUMP) {
            velocity.y = 0.42F;
            if (SPRINT) {
                float f = YAW * 0.017453292F;
                velocity.x = velocity.x - MinecraftMathHelper.sin(f) * 0.2F;
                velocity.z = velocity.z + MinecraftMathHelper.cos(f) * 0.2F;
            }
        }

        moveStrafe = moveStrafe * 0.98F;
        moveForward = moveForward * 0.98F;

        float mult = 0.91F;
        if (GROUND) mult = mult * slipperiness.value;
        float acceleration = 0.16277136F / (mult * mult * mult);

        float movement;
        if (SPRINT) movement = 0.130000010133F;
        else if (SNEAK) movement = 0.03F;
        else movement = 0.1F;

        float movementFactor;
        if (GROUND) movementFactor = movement * acceleration;
        else movementFactor = this.jumpMovementFactor;

        moveFlying(moveStrafe, moveForward, movementFactor);

        // calculate ladder

        moveEntity(velocity.x, velocity.y, velocity.z);

        velocity.y -= 0.08D;
        velocity.y *= 0.9800000190734863D;

        velocity.x = velocity.x * mult;
        velocity.z = velocity.z * mult;

        this.jumpMovementFactor = 0.02F;

        if (SPRINT) {
            this.jumpMovementFactor = (float) ((double) this.jumpMovementFactor + (double) 0.02F * 0.3D);
        }
    }

    private void moveEntity(double x, double y, double z) {

        // apply player movement in web
        if (WEB) {
            // update x, y, z values

        }

        // save x, y, z temporarily
        double xOriginal = x;
        double yOriginal = y;
        double zOriginal = z;

        // do some things to x, y, z when player sneaks...
        boolean GROUND_SNEAK = GROUND && SNEAK;
        if (GROUND_SNEAK) {
            // do calculation when player on ground and sneaking

        }

        // get all colliding BB from extending current position BB by x, y, z
        // NOTE: it takes all blocks for collision checks
        List<AxisAlignedBB> allBlocks = Environment.getAllBBs();

        // save playerBB temporarily
        AxisAlignedBB originalBB = playerBB;

        // check offsetY for Y from current position AND apply offsetY to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(playerBB, y);
        playerBB = playerBB.offset(0.0D, y, 0.0D);
        boolean onGround_or_yNegative = GROUND || yOriginal != y && yOriginal < 0.0D;

        // check offsetX for X from current position AND apply offsetX to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) x = axisalignedbb.calculateXOffset(playerBB, x);
        playerBB = playerBB.offset(x, 0.0D, 0.0D);

        // check offsetZ for Z from current position AND apply offsetZ to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) z = axisalignedbb.calculateZOffset(playerBB, z);
        playerBB = playerBB.offset(0.0D, 0.0D, z);

        // if the player is onGround OR falls AND x or z change:
        if (onGround_or_yNegative && (xOriginal != x || zOriginal != z)) {
            // save offsetY, offsetX, offsetZ and updatedBB
            double shiftedX = x;
            double shiftedY = y;
            double shiftedZ = z;
            AxisAlignedBB shiftedBB = playerBB;

            // reset playerBB to start position
            playerBB = originalBB;

            // overwrite y to 0.6 (step height)
             y = 0.6D;

            // get all colliding BB from extending current position BB by x, y=0.6, z
            // NOTE: it takes all blocks for collision checks
            allBlocks = Environment.getAllBBs();
            AxisAlignedBB axisAlignedBBNoUpdate = playerBB;

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
            AxisAlignedBB currentPlayerBB = playerBB;

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
                playerBB = axisAlignedBBNoUpdate;
            } else {
                x = xWithStepNoXZShift;
                z = zWithStepNoXZShift;
                y = -yWithStepNoXZShift;
                playerBB = currentPlayerBB;
            }

            // take the furthest of currentBB and first calculation and apply to x, y, z and update currentBB
            for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(playerBB, y);
            playerBB = playerBB.offset(0.0D, y, 0.0D);

            if (shiftedX * shiftedX + shiftedZ * shiftedZ >= x * x + z * z) {
                x = shiftedX;
                y = shiftedY;
                z = shiftedZ;
                playerBB = shiftedBB;
            }
        }

        // update player position toBB
        // updatePlayerBB();
        resetPositionToBB();

        // update onGround and isCollided
        isCollidedHorizontally = xOriginal != x || zOriginal != z;
        isCollidedVertically = yOriginal != y;
        GROUND = isCollidedVertically && yOriginal < 0.0D;
        isCollided = isCollidedHorizontally || isCollidedVertically;

        // update block below player?

        // if movedX != updatedX -> motionX = 0
        if (xOriginal != x) velocity.x = 0.0D;

        // if movedY != updatedY -> motionY = 0
        if (yOriginal != y) velocity.y = 0.0D;

        // if movedZ != updatedZ -> motionZ = 0
        if (zOriginal != z) velocity.z = 0.0D;
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
            float sin = MinecraftMathHelper.sin(YAW * (float) Math.PI / 180.0F);
            float cos = MinecraftMathHelper.cos(YAW * (float) Math.PI / 180.0F);
            velocity.x = velocity.x + strafe * cos - forward * sin;
            velocity.z = velocity.z + forward * cos + strafe * sin;
        }
    }

    private void updatePlayerBB() {
        this.playerBB = new AxisAlignedBB(
                position.x - 0.3, position.y, position.z - 0.3,
                position.x + 0.3, position.y + 1.8, position.z + 0.3
        );
    }

    private void resetPositionToBB() {
        this.position.x = (this.playerBB.minX + this.playerBB.maxX) / 2.0D;
        this.position.y = this.playerBB.minY;
        this.position.z = (this.playerBB.minZ + this.playerBB.maxZ) / 2.0D;
    }

}
