package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

public class Player {

    protected boolean GROUND = true;
    protected boolean WEB;

    protected boolean SPRINT;
    protected boolean SNEAK;
    protected boolean JUMP;

    protected float YAW;
    protected float moveStrafe, moveForward;

    protected float jumpMovementFactor = 0.02F;

    protected boolean isCollidedHorizontally;
    protected boolean isCollidedVertically;
    protected boolean isCollided;

    protected Movement.Slipperiness slipperiness;

    @Getter @Setter protected Vec3 velocity;
    @Getter @Setter protected Vec3 position;
    protected final Vec3 startVel;
    protected final Vec3 startPos;
    protected AxisAlignedBB playerBB;

    public Player(Vec3 position, Vec3 velocity) {
        this.startPos = position.copy();
        this.position = position.copy();

        this.startVel = velocity.copy();
        this.velocity = velocity.copy();

        slipperiness = Movement.Slipperiness.BLOCK;
        updatePlayerBB();
    }

    protected void updateTick(InputTick inputTick) {
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

    protected void resetPlayer() {
        this.position = this.startPos.copy();
        this.velocity = this.startVel.copy();
        updatePlayerBB();
        resetPositionToBB();
    }

    protected void applyInput(InputTick inputTick) {
        updateTick(inputTick);
        YAW = inputTick.YAW;
        JUMP = inputTick.JUMP && GROUND;
        SPRINT = inputTick.SPRINT || SPRINT;
        SNEAK = inputTick.SNEAK;
    }

    protected void updatePlayerBB() {
        this.playerBB = new AxisAlignedBB(
                position.x - 0.3, position.y, position.z - 0.3,
                position.x + 0.3, position.y + 1.8, position.z + 0.3
        );
    }

    protected void resetPositionToBB() {
        this.position.x = (this.playerBB.minX + this.playerBB.maxX) / 2.0D;
        this.position.y = this.playerBB.minY;
        this.position.z = (this.playerBB.minZ + this.playerBB.maxZ) / 2.0D;
    }

}
