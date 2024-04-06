package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.potion.PotionEffect;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player_1_20_4 extends Player {

    public int noJumpDelay;
    public float eyeHeight;
    public double fluidHeight;
    public boolean wasTouchingWater;
    public boolean SWIMMING;
    public boolean ELYTRA;
    public boolean wasELYTRA;
    public float depthStrider;
    public boolean horizontalCollision;
    public boolean verticalCollision;

    public Optional<Vec3> lastClimbablePos;
    public boolean verticalCollisionBelow;
    public boolean minorHorizontalCollision;
    public Vec3 stuckSpeedMultiplier;

    public Player_1_20_4(Vec3 position, Vec3 velocity, float startYAW, List<PotionEffect> eFs) {
        super(position, velocity, startYAW, eFs);
    }

    @Override
    public Player clone() {
        return new Player_1_20_4(this.startPos.copy(), this.startVel.copy(), this.startYAW, new ArrayList<>(this.potionEffects.values()));
    }

    @Override
    public void updateTick(InputTick inputTick) {
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        boolean jumpFlag = inputTick.JUMP;
        boolean isSneakAttempt = inputTick.SNEAK;
        boolean moveFlag = hasEnoughImpulseToStartSprinting();
        SNEAK = !this.isSwimming() && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)
                && (isSneakAttempt && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));

        float sneakEnchantBonus = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(this), 0.0F, 1.0F);

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

        this.moveTowardsClosestSpace(position.x - (double) width * 0.35D, position.z + (double) width * 0.35D);
        this.moveTowardsClosestSpace(position.x - (double) width * 0.35D, position.z - (double) width * 0.35D);
        this.moveTowardsClosestSpace(position.x + (double) width * 0.35D, position.z - (double) width * 0.35D);
        this.moveTowardsClosestSpace(position.x + (double) width * 0.35D, position.z + (double) width * 0.35D);

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

        /*if (this.input.jumping && !var9 && !var1 && !this.getAbilities().flying && !this.isPassenger() && !this.onClimbable()) {
            ItemStack var11 = this.getItemBySlot(EquipmentSlot.CHEST);
            if (var11.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(var11) && this.tryToStartFallFlying()) {
                this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
        }*/

        this.wasELYTRA = ELYTRA;
        if (this.isInWater() && inputTick.SNEAK && this.isAffectedByFluids()) {
            this.goDownInWater();
        }

        // rideables

        // code from player.java
        super.aiStep();
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
            Pose var1;
            if (this.isFallFlying()) {
                var1 = Pose.FALL_FLYING;
            } else if (this.isSleeping()) {
                var1 = Pose.SLEEPING;
            } else if (this.isSwimming()) {
                var1 = Pose.SWIMMING;
            } else if (this.isAutoSpinAttack()) {
                var1 = Pose.SPIN_ATTACK;
            } else if (this.isShiftKeyDown() && !this.abilities.flying) {
                var1 = Pose.CROUCHING;
            } else {
                var1 = Pose.STANDING;
            }

            Pose var2;
            if (!this.isSpectator() && !this.isPassenger() && !this.canPlayerFitWithinBlocksAndEntitiesWhen(var1)) {
                if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
                    var2 = Pose.CROUCHING;
                } else {
                    var2 = Pose.SWIMMING;
                }
            } else {
                var2 = var1;
            }

            this.setPose(var2);
        }
    }

    public void setPos(double var1, double var3, double var5) {
        this.setPosRaw(var1, var3, var5);
        playerBB = this.makeBoundingBox(position);
    }

    public AxisAlignedBB makeBoundingBox(Vec3 var1) {
        return makeBoundingBox(var1.x, var1.y, var1.z);
    }

    public AxisAlignedBB makeBoundingBox(double var1, double var3, double var5) {
        float var7 = this.width / 2.0F;
        float var8 = this.height;
        return new AxisAlignedBB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7);
    }

    public final void setPosRaw(double var1, double var3, double var5) {
        if (this.position.x != var1 || this.position.y != var3 || this.position.z != var5) {
            this.position = new Vec3(var1, var3, var5);
            int var7 = MinecraftMathHelper_1_20_4.floor(var1);
            int var8 = MinecraftMathHelper_1_20_4.floor(var3);
            int var9 = MinecraftMathHelper_1_20_4.floor(var5);
            if (var7 != this.blockPosition.getX() || var8 != this.blockPosition.getY() || var9 != this.blockPosition.getZ()) {
                this.blockPosition = new BlockPos(var7, var8, var9);
                this.feetBlockState = null;
                if (SectionPos.blockToSectionCoord(var7) != this.chunkPosition.x || SectionPos.blockToSectionCoord(var9) != this.chunkPosition.z) {
                    this.chunkPosition = new ChunkPos(this.blockPosition);
                }
            }
            this.levelCallback.onMove();
        }
    }

    private void moveTowardsClosestSpace(double var1, double var3) {
        BlockPos var5 = BlockPos.containing(var1, this.getY(), var3);
        if (this.suffocatesAt(var5)) {
            double var6 = var1 - (double)var5.getX();
            double var8 = var3 - (double)var5.getZ();
            Direction var10 = null;
            double var11 = Double.MAX_VALUE;
            Direction[] var13 = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
            Direction[] var14 = var13;
            int var15 = var13.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                Direction var17 = var14[var16];
                double var18 = var17.getAxis().choose(var6, 0.0D, var8);
                double var20 = var17.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - var18 : var18;
                if (var20 < var11 && !this.suffocatesAt(var5.relative(var17))) {
                    var11 = var20;
                    var10 = var17;
                }
            }

            if (var10 != null) {
                Vec3 var22 = this.getDeltaMovement();
                if (var10.getAxis() == Direction.Axis.X) {
                    this.setDeltaMovement(0.1D * (double)var10.getStepX(), var22.y, var22.z);
                } else {
                    this.setDeltaMovement(var22.x, var22.y, 0.1D * (double)var10.getStepZ());
                }
            }

        }
    }

    protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose pose) {
        return this.level().noCollision(this, this.getDimensions(pose).makeBoundingBox(this.position()).deflate(1.0E-7D));
    }

    private boolean hasEnoughImpulseToStartSprinting() {
        double var1 = 0.8D;
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8D;
    }

    public boolean isMovingSlowly() {
        return SNEAK || this.isVisuallyCrawling();
    }

    public boolean isVisuallyCrawling() {
        return this.isVisuallySwimming() && !this.isInWater();
    }

    public boolean isVisuallySwimming() {
        return this.hasPose(Pose.SWIMMING);
    }

    public double getFluidHeight(TagKey<Fluid> var1) {
        return this.fluidHeight.getDouble(var1);
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

}
