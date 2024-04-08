package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.FluidTags;
import de.legoshi.parkourcalculator.simulation.Pose;
import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.block_1_20_4.BubbleWater;
import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.simulation.potion.PotionEffect;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

public class Player_1_20_4 extends Player {

    public int noJumpDelay;
    public float eyeHeight;
    public HashMap<FluidTags, Double> fluidHeight = new HashMap<>();
    public boolean wasTouchingWater;
    public boolean SWIMMING;
    public boolean ELYTRA;
    public boolean wasELYTRA;
    public float depthStrider;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public Pose pose;

    public Optional<ABlock> lastClimbablePos;
    public Optional<ABlock> mainSupportingBlockPos;
    public boolean verticalCollisionBelow;
    public boolean minorHorizontalCollision;
    public Vec3 stuckSpeedMultiplier;
    public boolean onGroundNoBlocks;
    private boolean discardFriction;
    private boolean wasUnderwater;
    public ABlock blockPosition;
    public ABlock feetBlockState;
    public float maxUpStep = 0.6F;

    @Getter
    @Setter
    public float PITCH;
    @Getter
    @Setter
    public float startPITCH;
    public float fallDistance;
    private boolean wasInPowderSnow;
    private boolean isInPowderSnow;
    public Set<FluidTags> fluidOnEyes = new HashSet<>();
    public boolean wasEyeInWater;

    public static final EntityDimensions STANDING_DIMENSIONS;
    private static final Map<Pose, EntityDimensions> POSES = new HashMap<>();

    static {
        STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F);
        POSES.put(Pose.STANDING, STANDING_DIMENSIONS);
        POSES.put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F));
        POSES.put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F));
        POSES.put(Pose.CROUCHING, EntityDimensions.scalable(0.6F, 1.5F));
    }

    public Player_1_20_4(Vec3 position, Vec3 velocity, float startYAW, List<PotionEffect> eFs) {
        super(position, velocity, startYAW, eFs);
    }

    @Override
    public Player clone() {
        return new Player_1_20_4(this.startPos.copy(), this.startVel.copy(), this.startYAW, new ArrayList<>(this.potionEffects.values()));
    }

    @Override
    public void updateTick(InputTick inputTick) {
        this.updateIsUnderwater();

        // this.wasInPowderSnow = this.isInPowderSnow;
        // this.isInPowderSnow = false;
        // this.updateInWaterStateAndDoFluidPushing();
        // this.updateFluidOnEyes();
        // this.updateSwimming();

        if (this.isInLava()) {
            this.fallDistance *= 0.5F;
        }

        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        boolean jumpFlag = inputTick.JUMP;
        boolean isSneakAttempt = inputTick.SNEAK;
        boolean moveFlag = hasEnoughImpulseToStartSprinting();
        //SNEAK = !SWIMMING && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)
        //        && (isSneakAttempt && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));

        float sneakEnchantBonus = MinecraftMathHelper_1_20_4.clamp(0.3F + getSneakingSpeedBonus(), 0.0F, 1.0F);

        moveStrafe = 0F;
        moveForward = 0F;

        if (inputTick.W) moveForward++;
        if (inputTick.S) moveForward--;
        if (inputTick.A) moveStrafe--; // switch sites (related to x-axis difference in minecraft/javafx)
        if (inputTick.D) moveStrafe++; // switch sites (related to x-axis difference in minecraft/javafx)

        JUMP = inputTick.JUMP;
        boolean isSneakKeyDown = inputTick.SNEAK;

        if (isMovingSlowly()) {
            this.moveStrafe *= sneakEnchantBonus;
            this.moveForward *= sneakEnchantBonus;
        }

        if (isSneakAttempt) {
            this.sprintToggleTimer = 0;
        }

        boolean canStartSprinting = this.canStartSprinting();

        if ((GROUND || this.isUnderWater()) && !isSneakAttempt && !moveFlag && canStartSprinting) {
            if (this.sprintToggleTimer <= 0 && !inputTick.SPRINT) {
                this.sprintToggleTimer = 7;
            } else {
                SPRINT = true;
            }
        }

        if ((!this.isInWater() || this.isUnderWater()) && canStartSprinting && inputTick.SPRINT) {
            SPRINT = true;
        }

        boolean movesForward;
        if (SPRINT) {
            movesForward = !hasForwardImpulse();
            boolean var10 = movesForward || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater();
            if (SWIMMING) {
                if (!GROUND && !inputTick.SNEAK && movesForward || !this.isInWater()) {
                    SPRINT = false;
                }
            } else if (var10) {
                SPRINT = false;
            }
        }

        this.wasELYTRA = ELYTRA;
        if (this.isInWater() && inputTick.SNEAK) {
            this.goDownInWater();
        }

        // code from player.java
        /*if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }*/
    }

    public void resetPlayer() {
        super.resetPlayer();
        noJumpDelay = 0;
    }

    private boolean hasEnoughImpulseToStartSprinting() {
        double var1 = 0.8D;
        return this.isUnderWater() ? hasForwardImpulse() : (double) moveForward >= 0.8D;
    }

    public boolean isMovingSlowly() {
        return SNEAK || this.isVisuallyCrawling();
    }

    public boolean isVisuallyCrawling() {
        return this.isVisuallySwimming() && !this.isInWater();
    }

    public boolean isVisuallySwimming() {
        return pose == Pose.SWIMMING;
    }

    public double getFluidHeight(FluidTags tags) {
        return this.fluidHeight.get(tags);
    }

    public boolean hasForwardImpulse() {
        return this.moveForward > 1.0E-5F;
    }

    public double getFluidJumpThreshold() {
        return (double) eyeHeight < 0.4D ? 0.0D : 0.4D;
    }

    public boolean isInLava() {
        return fluidHeight.get(FluidTags.LAVA) > 0.0D;
    }

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    public boolean isUnderWater() {
        return this.wasUnderwater;
    }

    public boolean shouldDiscardFriction() {
        return this.discardFriction;
    }

    public float getSneakingSpeedBonus() {
        return (float) potionEffects.get(Potion.swift_sneak).getAmplifier() * 0.15F;
    }

    public boolean isHorizontalCollisionMinor(Vec3 var1) {
        float var2 = YAW * 0.017453292F;
        double var3 = (double) MinecraftMathHelper_1_20_4.sin(var2);
        double var5 = (double) MinecraftMathHelper_1_20_4.cos(var2);
        double var7 = (double) this.moveStrafe * var5 - (double) this.moveForward * var3;
        double var9 = (double) this.moveForward * var5 + (double) this.moveStrafe * var3;
        double var11 = MinecraftMathHelper_1_20_4.square(var7) + MinecraftMathHelper_1_20_4.square(var9);
        double var13 = MinecraftMathHelper_1_20_4.square(var1.x) + MinecraftMathHelper_1_20_4.square(var1.z);
        if (!(var11 < 9.999999747378752E-6D) && !(var13 < 9.999999747378752E-6D)) {
            double var15 = var7 * var1.x + var9 * var1.z;
            double var17 = Math.acos(var15 / Math.sqrt(var11 * var13));
            return var17 < 0.13962633907794952D;
        } else {
            return false;
        }
    }

    private boolean canStartSprinting() {
        return !SPRINT && this.hasEnoughImpulseToStartSprinting() && !ELYTRA;
    }

    protected void goDownInWater() {
        velocity.add(new Vec3(0.0D, -0.03999999910593033D, 0.0D));
    }


    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    public boolean isEyeInFluid(FluidTags var1) {
        return this.fluidOnEyes.contains(var1);
    }

    public double getEyeY() {
        return this.position.y + (double) this.eyeHeight;
    }

    public EntityDimensions getDimensions(Pose var1) {
        return (EntityDimensions) POSES.getOrDefault(var1, STANDING_DIMENSIONS);
    }

}
