package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.simulation.potion.PotionEffect;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Ladder;
import de.legoshi.parkourcalculator.simulation.environment.block.Vine;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ToString
public abstract class Player {

    private static final Logger logger = LogManager.getLogger(Player.class.getName());

    @Getter @Setter public Vec3 startPos;
    @Getter @Setter public Vec3 position;
    @Getter @Setter public Vec3 startVel;
    @Getter @Setter public Vec3 velocity;
    @Getter @Setter public Vec3 realVel;

    @Getter @Setter public float YAW;
    @Getter @Setter public float startYAW;

    public float width = 0.3F;
    public float height = 1.8F;
    public double speed = 0.699999988079071D;

    public boolean GROUND;
    public boolean WEB;
    public boolean WATER;
    public boolean LAVA;

    public boolean SPRINT;
    public int sprintToggleTimer;
    public boolean SNEAK;
    public boolean JUMP;

    public float moveStrafe, moveForward;

    public float jumpMovementFactor = 0.02F;
    public int jumpTicks;

    public boolean isCollidedHorizontally;
    public boolean isCollidedVertically;
    public boolean isCollided;

    public Movement.Slipperiness slipperiness;
    public AxisAlignedBB playerBB;

    @Getter public Map<Potion, PotionEffect> potionEffects;

    public Player(Vec3 position, Vec3 velocity, float startYAW, List<PotionEffect> eFs) {
        this.startPos = position.copy();
        this.position = position.copy();

        this.startVel = velocity.copy();
        this.velocity = velocity.copy();

        this.startYAW = startYAW;

        slipperiness = Movement.Slipperiness.BLOCK;
        initPotion(eFs);
        updatePlayerBB();
    }
    
    public abstract Player clone();

    public void updateTick(InputTick inputTick) {
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        boolean jumpFlag = inputTick.JUMP;
        boolean isSNEAK = inputTick.SNEAK;
        boolean moveFlag = moveForward >= 0.8F;

        moveStrafe = 0F;
        moveForward = 0F;

        if (inputTick.W) moveForward++;
        if (inputTick.S) moveForward--;
        if (inputTick.A) moveStrafe--; // switch sites (related to x-axis difference in minecraft/javafx)
        if (inputTick.D) moveStrafe++; // switch sites (related to x-axis difference in minecraft/javafx)

        JUMP = inputTick.JUMP;
        SNEAK = inputTick.SNEAK;

        if (SNEAK) {
            moveStrafe = (float) ((double) moveStrafe * 0.3D);
            moveForward = (float) ((double) moveForward * 0.3D);
        }

        if (GROUND && !isSNEAK && !moveFlag && moveForward >= 0.8F && !SPRINT) {
            if (this.sprintToggleTimer <= 0) {
                this.sprintToggleTimer = 7;
            } else {
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

    // run one idle tick to apply these values
    public void resetPlayer() {
        this.position = this.startPos.copy();
        this.velocity = this.startVel.copy();
        this.realVel = this.startVel.copy();
        this.sprintToggleTimer = 0;
        YAW = startYAW;
        GROUND = false;
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
        jumpTicks = 0;
        updatePlayerBB();
        resetPositionToBB();
    }

    public boolean isOnLadder(BlockManager blockManager) {
        int x = MinecraftMathHelper.floor_double(this.position.x);
        int minY = MinecraftMathHelper.floor_double(this.playerBB.minY);
        int z = MinecraftMathHelper.floor_double(this.position.z);

        ABlock block = blockManager.getBlock(x, minY, z);
        return (block instanceof Ladder || block instanceof Vine);
    }

    public void applyInput(InputTick inputTick) {
        updateTick(inputTick);
        YAW = YAW + inputTick.YAW; // flips facing on x-axis
    }

    public void updatePlayerBB() {
        this.playerBB = new AxisAlignedBB(
                position.x - this.width, position.y, position.z - this.width,
                position.x + this.width, position.y + this.height, position.z + this.width
        );
    }

    public AxisAlignedBB getStartBB() {
        return new AxisAlignedBB(
                startPos.x - this.width, startPos.y, startPos.z - this.width,
                startPos.x + this.width, startPos.y + this.height, startPos.z + this.width
        );
    }

    public void setWEB(boolean b) {
        this.WEB = b;
    }

    public boolean isSNEAK() {
        return SNEAK;
    }

    public void resetPositionToBB() {
        this.position.x = (this.playerBB.minX + this.playerBB.maxX) / 2.0D;
        this.position.y = this.playerBB.minY;
        this.position.z = (this.playerBB.minZ + this.playerBB.maxZ) / 2.0D;
    }

    public PlayerTickInformation getPlayerTickInformation() {
        return new PlayerTickInformation(
                YAW,
                position.copy(),
                velocity.copy(),
                realVel.copy(),
                isCollided,
                GROUND,
                JUMP
        );
    }

    private void initPotion(List<PotionEffect> pEs) {
        this.potionEffects = new HashMap<>();
        potionEffects.put(Potion.moveSpeed, pEs.get(0));
        potionEffects.put(Potion.moveSlowdown, pEs.get(1));
        potionEffects.put(Potion.jump, pEs.get(2));
        potionEffects.put(Potion.swift_sneak, pEs.get(3));
        potionEffects.put(Potion.levitation, pEs.get(4));
        potionEffects.put(Potion.soul_speed, pEs.get(5));
        potionEffects.put(Potion.dolphins_grace, pEs.get(6));
        potionEffects.put(Potion.slow_falling, pEs.get(7));
    }

    public boolean hasPotion(Potion potion) {
        return potionEffects.get(potion).getAmplifier() >= 1;
    }

    public void resetPotion() {
        logger.debug("reset potion effects");
        this.potionEffects.forEach((potion, potionEffect) -> {
            potionEffect.setDuration(-1);
            potionEffect.setAmplifier(0);
        });
    }

    public void setSpeed(float attributeValue) {
        this.speed = attributeValue;
    }

    public float getSpeed() {
        return (float) speed;
    }

}
