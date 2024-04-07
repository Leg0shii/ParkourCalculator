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
    private Set<FluidTags> fluidOnEyes = new HashSet<>();
    private boolean wasEyeInWater;

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

        this.wasInPowderSnow = this.isInPowderSnow;
        this.isInPowderSnow = false;
        this.updateInWaterStateAndDoFluidPushing();
        this.updateFluidOnEyes();
        this.updateSwimming();

        if (this.isInLava()) {
            this.fallDistance *= 0.5F;
        }

        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        boolean jumpFlag = inputTick.JUMP;
        boolean isSneakAttempt = inputTick.SNEAK;
        boolean moveFlag = hasEnoughImpulseToStartSprinting();
        SNEAK = !SWIMMING && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)
                && (isSneakAttempt && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));

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

    public void updatePlayerPose() {
        if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
            Pose poseBefore;
            if (ELYTRA) {
                poseBefore = Pose.FALL_FLYING;
            } else if (SWIMMING) {
                poseBefore = Pose.SWIMMING;
            } else if (SNEAK) {
                poseBefore = Pose.CROUCHING;
            } else {
                poseBefore = Pose.STANDING;
            }

            Pose poseAfter;
            if (!this.canPlayerFitWithinBlocksAndEntitiesWhen(poseBefore)) {
                if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
                    poseAfter = Pose.CROUCHING;
                } else {
                    poseAfter = Pose.SWIMMING;
                }
            } else {
                poseAfter = poseBefore;
            }
            this.pose = poseAfter;
        }
    }

    public boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose pose) {
        return this.level().noCollision(this, this.getDimensions(pose).makeBoundingBox(this.position()).deflate(1.0E-7D));
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
        return this.fluidHeight.getDouble(tags);
    }

    public boolean hasForwardImpulse() {
        return this.moveForward > 1.0E-5F;
    }

    public double getFluidJumpThreshold() {
        return (double) eyeHeight < 0.4D ? 0.0D : 0.4D;
    }

    public boolean isInLava() {
        return fluidHeight.getDouble(FluidTags.LAVA) > 0.0D;
    }

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    public boolean isUnderWater() {
        return this.wasUnderwater;
    }

    public ABlock getOnPosLegacy() {
        return this.getOnPos(0.2F);
    }

    public ABlock getBlockPosBelowThatAffectsMyMovement() {
        return this.getOnPos(0.500001F);
    }

    protected ABlock getOnPos(float var1) {
        if (this.mainSupportingBlockPos.isPresent()) {
            ABlock block = this.mainSupportingBlockPos.get();
            if (!(var1 > 1.0E-5F)) {
                return block;
            } else {
                return (!((double) var1 <= 0.5D) || !(block instanceof Fence) && !(block instanceof Cobblewall)
                        ? block.atY(MinecraftMathHelper_1_20_4.floor(this.position.y - (double) var1)) : block);
            }
        } else {
            int var2 = MinecraftMathHelper_1_20_4.floor(this.position.x);
            int var3 = MinecraftMathHelper_1_20_4.floor(this.position.y - (double) var1);
            int var4 = MinecraftMathHelper_1_20_4.floor(this.position.z);
            return blockManager.getBlock(new Vec3(var2, var3, var4));
        }
    }

    public boolean shouldDiscardFriction() {
        return this.discardFriction;
    }

    public float getBlockSpeedFactor() {
        float speedFactor = this.onSoulSpeedBlock() && hasPotion(Potion.soul_speed) ? 1.0F : getBlockSpeedFactor2();
        return !ELYTRA ? speedFactor : 1.0F;
    }

    public float getBlockSpeedFactor2() {
        float var2 = blockPosition.getSpeedFactor();
        if (!(blockPosition instanceof Water) && !(blockPosition instanceof BubbleWater)) {
            return (double) var2 == 1.0D ? getBlockPosBelowThatAffectsMyMovement().getSpeedFactor() : var2;
        } else {
            return var2;
        }
    }

    protected boolean onSoulSpeedBlock() {
        return this.getBlockPosBelowThatAffectsMyMovement() instanceof Soulsand;
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

    public boolean isAboveGround() {
        return GROUND || this.fallDistance < maxUpStep && !this.level().noCollision(this, playerBB.offset(0.0D, (double) (this.fallDistance - maxUpStep), 0.0D));
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    protected boolean updateInWaterStateAndDoFluidPushing() {
        this.fluidHeight.clear();
        this.updateInWaterStateAndDoWaterCurrentPushing();
        double var1 = this.level().dimensionType().ultraWarm() ? 0.007D : 0.0023333333333333335D;
        boolean var3 = this.updateFluidHeightAndDoFluidPushing(FluidTags.LAVA, var1);
        return this.isInWater() || var3;
    }

    public void updateInWaterStateAndDoWaterCurrentPushing() {
        if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0.014D)) {
            this.fallDistance = 0.0F;
            this.wasTouchingWater = true;
        } else {
            this.wasTouchingWater = false;
        }

    }

    public boolean updateFluidHeightAndDoFluidPushing(FluidTags var1, double var2) {
        AxisAlignedBB playerBB = this.playerBB.deflate(0.001D);
        int minX = MinecraftMathHelper_1_20_4.floor(playerBB.minX);
        int maxX = MinecraftMathHelper_1_20_4.ceil(playerBB.maxX);
        int minY = MinecraftMathHelper_1_20_4.floor(playerBB.minY);
        int maxY = MinecraftMathHelper_1_20_4.ceil(playerBB.maxY);
        int minZ = MinecraftMathHelper_1_20_4.floor(playerBB.minZ);
        int maxZ = MinecraftMathHelper_1_20_4.ceil(playerBB.maxZ);
        double var11 = 0.0D;
        boolean var14 = false;
        Vec3 var15 = Vec3.ZERO;
        int var16 = 0;
        BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    var17.set(x, y, z);
                    FluidState var21 = this.level().getFluidState(var17);
                    if (var21.is(var1)) {
                        double var22 = (double) ((float) y + var21.getHeight(this.level(), var17));
                        if (var22 >= playerBB.minY) {
                            var14 = true;
                            var11 = Math.max(var22 - playerBB.minY, var11);
                            Vec3 var24 = var21.getFlow(this.level(), var17);
                            if (var11 < 0.4D) {
                                var24 = var24.scale(var11);
                            }

                            var15.add(var24);
                            ++var16;

                        }
                    }
                }
            }
        }

        if (var15.length() > 0.0D) {
            if (var16 > 0) {
                var15 = var15.scale(1.0D / (double) var16);
            }

            Vec3 var25 = velocity;
            var15 = var15.scale(var2 * 1.0D);
            double var26 = 0.003D;
            if (Math.abs(var25.x) < 0.003D && Math.abs(var25.z) < 0.003D && var15.length() < 0.0045000000000000005D) {
                var15 = var15.normalize().scale(0.0045000000000000005D);
            }

            velocity.add(var15);
        }

        this.fluidHeight.put(var1, var11);
        return var14;
    }

    private void updateFluidOnEyes() {
        this.wasEyeInWater = this.isEyeInFluid(FluidTags.WATER);
        this.fluidOnEyes.clear();
        double var1 = this.getEyeY() - 0.1111111119389534D;

        BlockPos var8 = BlockPos.containing(this.getX(), var1, this.getZ());
        FluidState var5 = this.level().getFluidState(var8);
        double var6 = (double) ((float) var8.getY() + var5.getHeight(this.level(), var8));
        if (var6 > var1) {
            Stream var10000 = var5.getTags();
            Set var10001 = this.fluidOnEyes;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
        }
    }

    public void updateSwimming() {
        if (SWIMMING) {
            SWIMMING = SPRINT && this.isInWater();
        } else {
            SWIMMING = SPRINT && this.isUnderWater() && this.level().getFluidState(this.blockPosition).is(FluidTags.WATER);
        }
    }

    public boolean isEyeInFluid(FluidTags var1) {
        return this.fluidOnEyes.contains(var1);
    }

}
