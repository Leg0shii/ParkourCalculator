package de.legoshi.parkourcalculator.simulation.player;

import de.legoshi.parkourcalculator.simulation.potion.PotionEffect;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Player_1_20_4 extends Player {

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

        /*
        if (this.isUsingItem() && !this.isPassenger()) {
            Input var10000 = this.input;
            var10000.leftImpulse *= 0.2F;
            var10000 = this.input;
            var10000.forwardImpulse *= 0.2F;
            this.sprintTriggerTime = 0;
        }
        */

        /*boolean autoJump = false;
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            autoJump = true;
            this.input.jumping = true;
        }*/

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
            if (this.isSwimming()) {
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

        this.wasFallFlying = this.isFallFlying();
        if (this.isInWater() && this.input.shiftKeyDown && this.isAffectedByFluids()) {
            this.goDownInWater();
        }

        // rideables

        // code from player.java
        super.aiStep();
        /*if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }*/
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

    public boolean isInWater() {
        return this.wasTouchingWater;
    }

    public boolean hasForwardImpulse() {
        return this.moveForward > 1.0E-5F;
    }

}
