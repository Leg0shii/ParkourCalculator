package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Ladder;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Vine;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper;
import de.legoshi.parkourcalculator.util.Movement;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Player {

    protected float width = 0.3F;
    protected float height = 1.8F;

    protected boolean GROUND = true;
    @Setter protected boolean WEB;
    protected boolean WATER;
    protected boolean LAVA;

    protected boolean SPRINT;
    @Getter protected boolean SNEAK;
    protected boolean JUMP;

    @Setter @Getter protected float YAW;
    protected float moveStrafe, moveForward;

    protected float jumpMovementFactor = 0.02F;

    protected boolean isCollidedHorizontally;
    protected boolean isCollidedVertically;
    protected boolean isCollided;

    protected Movement.Slipperiness slipperiness;

    @Getter @Setter protected Vec3 velocity;
    @Getter @Setter protected Vec3 position;
    @Getter @Setter protected Vec3 startVel;
    @Getter @Setter protected Vec3 startPos;
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
        boolean flag = inputTick.JUMP;
        boolean isSNEAK = inputTick.SNEAK;
        boolean flag2 = moveForward >= 0.8F;

        moveStrafe = 0F;
        moveForward = 0F;

        if (inputTick.W) moveForward++;
        if (inputTick.S) moveForward--;
        if (inputTick.A) moveStrafe++;
        if (inputTick.D) moveStrafe--;

        JUMP = inputTick.JUMP;
        SNEAK = inputTick.SNEAK;

        if (SNEAK) {
            moveStrafe = (float) ((double) moveStrafe * 0.3D);
            moveForward = (float) ((double) moveForward * 0.3D);
        }

        if (GROUND && !isSNEAK && !flag2 && moveForward >= 0.8F && !SPRINT) {
            if (inputTick.SPRINT) {
                SPRINT = true;
            }
        }

        if (!SPRINT && moveForward >= 0.8F && inputTick.SPRINT) {
            SPRINT = true;
        }

        if (SPRINT && (moveForward < 0.8F || this.isCollidedHorizontally)) {
            SPRINT = false;
        }
    }

    protected void resetPlayer() {
        this.position = this.startPos.copy();
        this.velocity = this.startVel.copy();
        YAW = 0;
        GROUND = true;
        WEB = false;
        SPRINT = false;
        SNEAK = false;
        JUMP = false;
        moveStrafe = 0;
        moveForward = 0;
        jumpMovementFactor = 0.02F;
        isCollidedHorizontally = false;
        isCollidedVertically = false;
        isCollided = false;
        slipperiness = Movement.Slipperiness.BLOCK;
        updatePlayerBB();
        resetPositionToBB();
    }

    protected void applyInput(InputTick inputTick) {
        updateTick(inputTick);
        YAW = YAW + inputTick.YAW;
        SPRINT = inputTick.SPRINT || SPRINT;
    }

    protected void updatePlayerBB() {
        this.playerBB = new AxisAlignedBB(
                position.x - this.width, position.y, position.z - this.width,
                position.x + this.width, position.y + this.height, position.z + this.width
        );
    }

    protected void resetPositionToBB() {
        this.position.x = (this.playerBB.minX + this.playerBB.maxX) / 2.0D;
        this.position.y = this.playerBB.minY;
        this.position.z = (this.playerBB.minZ + this.playerBB.maxZ) / 2.0D;
    }

    protected PlayerTickInformation getPlayerTickInformation() {
        return new PlayerTickInformation(
                YAW,
                position.copy(),
                velocity.copy(),
                isCollided,
                GROUND,
                JUMP
        );
    }

    public boolean isOnLadder() {
        int x = MinecraftMathHelper.floor_double(this.position.x);
        int minY = MinecraftMathHelper.floor_double(this.playerBB.minY);
        int z = MinecraftMathHelper.floor_double(this.position.z);

        ABlock block = Environment.getBlock(x, minY, z);
        return (block instanceof Ladder || block instanceof Vine);
    }

}
