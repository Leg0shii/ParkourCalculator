package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_20_4;
import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.Optional;

public class Movement_1_20_4 extends Movement {

    public Movement_1_20_4(Player player, BlockManager blockManager) {
        super(player, blockManager);
    }

    @Override
    public Movement clone() {
        Player_1_20_4 player_1_20_4 = (Player_1_20_4) player.clone();
        return new Movement_1_20_4(player_1_20_4, blockManager);
    }

    @Override
    public void calculateTick(InputTick inputTick) {
        player.applyInput(inputTick);

        Player_1_20_4 player = (Player_1_20_4) this.player;

        // code from LivingEntity
        if (player.noJumpDelay > 0) {
            --player.noJumpDelay;
        }

        if (Math.abs(player.velocity.x) < 0.003D) player.velocity.x = 0.0D;
        if (Math.abs(player.velocity.y) < 0.003D) player.velocity.y = 0.0D;
        if (Math.abs(player.velocity.z) < 0.003D) player.velocity.z = 0.0D;

        float xxa = player.moveStrafe;
        float zza = player.moveForward;
        boolean jumping = inputTick.JUMP;
        /*this.yBobO = this.yBob;
        this.xBobO = this.xBob;
        this.xBob += (this.getXRot() - this.xBob) * 0.5F;
        this.yBob += (this.getYRot() - this.yBob) * 0.5F;*/

        if (jumping) {
            double fluidHeight;
            if (player.isInLava()) {
                fluidHeight = player.getFluidHeight(FluidTags.LAVA);
            } else {
                fluidHeight = player.getFluidHeight(FluidTags.WATER);
            }

            boolean var10 = player.isInWater() && fluidHeight > 0.0D;
            double fluidJumpThreshold = player.getFluidJumpThreshold();
            if (!var10 || player.GROUND && !(fluidHeight > fluidJumpThreshold)) {
                if (player.isInLava() && (!player.GROUND || fluidHeight > fluidJumpThreshold)) {
                    this.jumpInLiquid();
                } else if ((player.GROUND || var10 && fluidHeight <= fluidJumpThreshold) && player.noJumpDelay == 0) {
                    this.jumpFromGround();
                    player.noJumpDelay = 10;
                }
            } else {
                this.jumpInLiquid();
            }
        } else {
            player.noJumpDelay = 0;
        }

        xxa *= 0.98F;
        zza *= 0.98F;

        if (player.ELYTRA && !player.GROUND && !player.hasPotion(Potion.levitation)) {
            player.ELYTRA = inputTick.ELYTRA;
        } else {
            player.ELYTRA = false;
        }

        Vec3 travelVec = new Vec3((double) xxa, (double) yya, (double) zza);

        // player.java
        if (player.SWIMMING) {
            double lookAngleY = player.getLookAngle().y;
            double var4 = lookAngleY < -0.2D ? 0.085D : 0.06D;
            if (lookAngleY <= 0.0D || player.JUMP ||
                    !this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
                player.velocity.add(new Vec3(0.0D, (lookAngleY - player.velocity.y) * var4, 0.0D));
            }
        }

        travel(travelVec); // LivingEntity.java

        /*if (!this.level().isClientSide && !this.isDeadOrDying()) {
            int var15 = this.getTicksFrozen();
            if (this.isInPowderSnow && this.canFreeze()) {
                this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), var15 + 1));
            } else {
                this.setTicksFrozen(Math.max(0, var15 - 2));
            }
        }*/

        this.removeFrost();
        this.tryAddFrost();

        // belongs to player.java
        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float var1;
        if (player.GROUND && !player.SWIMMING) {
            var1 = Math.min(0.1F, (float) this.getDeltaMovement().horizontalDistance());
        } else {
            var1 = 0.0F;
        }

        /*if (GROUND && this.getAbilities().flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            this.getAbilities().flying = false;
            this.onUpdateAbilities();
        }*/

        double clampedX = MinecraftMathHelper_1_20_4.clamp(player.position.x, -2.9999999E7D, 2.9999999E7D);
        double clampedZ = MinecraftMathHelper_1_20_4.clamp(player.position.z, -2.9999999E7D, 2.9999999E7D);
        if (clampedX != player.position.x || clampedZ != player.position.z) {
            player.setPos(clampedX, player.position.y, clampedZ);
        }

        player.updatePlayerPose();
    }

    private void travel(Vec3 travelVec) {
        Player_1_20_4 player = (Player_1_20_4) this.player;

        double var2 = 0.08D;
        if (player.velocity.y <= 0.0D && player.hasPotion(Potion.slow_falling)) {
            var2 = 0.01D;
        }

        FluidState fluidState = this.level().getFluidState(this.blockPosition());
        float friction = player.SPRINT ? 0.9F : this.getWaterSlowDown();
        double yPos = player.position.y;
        if (player.isInWater() && !this.canStandOnFluid(fluidState)) {
            float var27 = 0.02F;
            float depthStrider = player.depthStrider;
            if (depthStrider > 3.0F) {
                depthStrider = 3.0F;
            }

            if (!player.GROUND) {
                depthStrider *= 0.5F;
            }

            if (depthStrider > 0.0F) {
                friction += (0.54600006F - friction) * depthStrider / 3.0F;
                var27 += (player.getSpeed() - var27) * depthStrider / 3.0F;
            }

            if (player.hasPotion(Potion.dolphins_grace)) {
                friction = 0.96F;
            }

            this.moveRelative(var27, travelVec);
            this.move(player.velocity);
            Vec3 var28 = player.velocity;
            if (player.horizontalCollision && this.onClimbable()) {
                var28 = new Vec3(var28.x, 0.2D, var28.z);
            }

            player.velocity = var28.multiply((double) friction, 0.800000011920929D, (double) friction);
            Vec3 var12 = player.getFluidFallingAdjustedMovement(var2, player.velocity.y <= 0.0D, this.player.velocity);
            player.velocity = var12;
            if (player.horizontalCollision && player.isFree(var12.x, var12.y + 0.6000000238418579D - player.position.y + yPos, var12.z)) {
                player.velocity = new Vec3(var12.x, 0.30000001192092896D, var12.z);
            }
        } else if (player.isInLava() && !player.canStandOnFluid(fluidState)) {
            yPos = player.position.y;
            this.moveRelative(0.02F, travelVec);
            this.move(player.velocity);
            Vec3 var23;
            if (player.getFluidHeight(FluidTags.LAVA) <= player.getFluidJumpThreshold()) {
                player.velocity = player.velocity.multiply(0.5D, 0.800000011920929D, 0.5D);
                var23 = player.getFluidFallingAdjustedMovement(var2, player.velocity.y <= 0.0D, this.player.velocity);
                player.velocity = var23;
            } else {
                player.velocity = player.velocity.scale(0.5D);
            }

            player.velocity.add(new Vec3(0.0D, -var2 / 4.0D, 0.0D));

            var23 = player.velocity;
            if (player.horizontalCollision && this.isFree(var23.x, var23.y + 0.6000000238418579D - player.position.y + yPos, var23.z)) {
                player.velocity = new Vec3(var23.x, 0.30000001192092896D, var23.z);
            }
        } /* ELYTRA */
        else if (player.ELYTRA) {
            this.checkSlowFallDistance();
            Vec3 playerVel = player.velocity;
            Vec3 lookAngle = this.getLookAngle();
            friction = this.getXRot() * 0.017453292F;

            double var9 = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
            double var11 = playerVel.horizontalDistance();
            double var13 = lookAngle.length();
            double var15 = Math.cos((double) friction);
            var15 = var15 * var15 * Math.min(1.0D, var13 / 0.4D);
            playerVel.add(new Vec3(0.0D, var2 * (-1.0D + var15 * 0.75D), 0.0D));

            double var17;
            if (playerVel.y < 0.0D && var9 > 0.0D) {
                var17 = playerVel.y * -0.1D * var15;
                playerVel.add(new Vec3(lookAngle.x * var17 / var9, var17, lookAngle.z * var17 / var9));
            }

            if (friction < 0.0F && var9 > 0.0D) {
                var17 = var11 * (double) (-MinecraftMathHelper_1_20_4.sin(friction)) * 0.04D;
                playerVel.add(new Vec3(-lookAngle.x * var17 / var9, var17 * 3.2D, -lookAngle.z * var17 / var9));
            }

            if (var9 > 0.0D) {
                playerVel.add(new Vec3((lookAngle.x / var9 * var11 - playerVel.x) * 0.1D, 0.0D, (lookAngle.z / var9 * var11 - playerVel.z) * 0.1D));
            }

            player.velocity = playerVel.multiply(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
            this.move(player.velocity);

            if (player.GROUND) {
                player.ELYTRA = false;
            }

        } else {
            Vec3 movementBlock = this.getBlockPosBelowThatAffectsMyMovement();
            float blockFriction = this.level().getBlockState(movementBlock).getBlock().getFriction();
            friction = player.GROUND ? blockFriction * 0.91F : 0.91F;
            Vec3 var26 = this.handleRelativeFrictionAndCalculateMovement(travelVec, blockFriction);
            double var10 = var26.y;
            if (player.hasPotion(Potion.levitation)) {
                var10 += (0.05D * (double) (player.getPotionEffects().get(Potion.levitation).getAmplifier() + 1) - var26.y) * 0.2D;
            }

            var10 -= var2;

            if (this.shouldDiscardFriction()) {
                player.velocity = new Vec3(var26.x, var10, var26.z);
            } else {
                player.velocity = new Vec3(var26.x * (double) friction, var10 * 0.9800000190734863D, var26.z * (double) friction);
            }
        }
    }

    public void move(Vec3 var2) {
        Player_1_20_4 player = (Player_1_20_4) this.player;

        if (player.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
            var2 = var2.multiply(player.stuckSpeedMultiplier);
            player.stuckSpeedMultiplier = Vec3.ZERO;
            player.velocity = Vec3.ZERO;
        }

        var2 = this.maybeBackOffFromEdge(var2);
        Vec3 var3 = player.collide(var2);
        double var4 = var3.lengthSqr();
        if (var4 > 1.0E-7D) {
            player.setPos(player.position.x + var3.x, player.position.y + var3.y, player.position.z + var3.z);
        }

        boolean var23 = !MinecraftMathHelper_1_20_4.equal(var2.x, var3.x);
        boolean var7 = !MinecraftMathHelper_1_20_4.equal(var2.z, var3.z);

        player.horizontalCollision = var23 || var7;
        player.verticalCollision = var2.y != var3.y;
        player.verticalCollisionBelow = player.verticalCollision && var2.y < 0.0D;
        if (player.horizontalCollision) {
            player.minorHorizontalCollision = player.isHorizontalCollisionMinor(var3);
        } else {
            player.minorHorizontalCollision = false;
        }

        this.setOnGroundWithKnownMovement(player.verticalCollisionBelow, var3);
        Vec3 legaceBlockPos = this.getOnPosLegacy();
        BlockState var9 = this.level().getBlockState(legaceBlockPos);
        this.checkFallDamage(var3.y, this.onGround(), var9, legaceBlockPos);
        if (this.isRemoved()) {
            this.level().getProfiler().pop();
        } else {
            if (player.horizontalCollision) {
                Vec3 var10 = player.velocity;
                player.velocity = new Vec3(var23 ? 0.0D : var10.x, var10.y, var7 ? 0.0D : var10.z);
            }

            Block var24 = var9.getBlock();
            if (var2.y != var3.y) {
                var24.updateEntityAfterFallOn(this.level(), this);
            }

            if (player.GROUND) {
                var24.stepOn(this.level(), legaceBlockPos, var9, this);
            }

            this.tryCheckInsideBlocks();
            float var25 = this.getBlockSpeedFactor();
            player.velocity = player.velocity.multiply((double) var25, 1.0D, (double) var25));
        }
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 var1) {
        if (!this.abilities.flying && var1.y <= 0.0D && this.isStayingOnGroundSurface() && this.isAboveGround()) {
            double var3 = var1.x;
            double var5 = var1.z;
            double var7 = 0.05D;

            while (true) {
                while (var3 != 0.0D && this.level().noCollision(this, this.getBoundingBox().move(var3, (double) (-this.maxUpStep()), 0.0D))) {
                    if (var3 < 0.05D && var3 >= -0.05D) {
                        var3 = 0.0D;
                    } else if (var3 > 0.0D) {
                        var3 -= 0.05D;
                    } else {
                        var3 += 0.05D;
                    }
                }

                while (true) {
                    while (var5 != 0.0D && this.level().noCollision(this, this.getBoundingBox().move(0.0D, (double) (-this.maxUpStep()), var5))) {
                        if (var5 < 0.05D && var5 >= -0.05D) {
                            var5 = 0.0D;
                        } else if (var5 > 0.0D) {
                            var5 -= 0.05D;
                        } else {
                            var5 += 0.05D;
                        }
                    }

                    while (true) {
                        while (var3 != 0.0D && var5 != 0.0D && this.level().noCollision(this, this.getBoundingBox().move(var3, (double) (-this.maxUpStep()), var5))) {
                            if (var3 < 0.05D && var3 >= -0.05D) {
                                var3 = 0.0D;
                            } else if (var3 > 0.0D) {
                                var3 -= 0.05D;
                            } else {
                                var3 += 0.05D;
                            }

                            if (var5 < 0.05D && var5 >= -0.05D) {
                                var5 = 0.0D;
                            } else if (var5 > 0.0D) {
                                var5 -= 0.05D;
                            } else {
                                var5 += 0.05D;
                            }
                        }

                        var1 = new Vec3(var3, var1.y, var5);
                        return var1;
                    }
                }
            }
        } else {
            return var1;
        }
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 var1, float var2) {
        this.moveRelative(this.getFrictionInfluencedSpeed(var2), var1);
        this.player.velocity = this.handleOnClimbable(player.velocity);
        this.move(player.velocity);
        if ((this.horizontalCollision || player.JUMP) && (this.onClimbable() || this.getFeetBlockState().is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this))) {
            this.player.velocity = new Vec3(this.player.velocity.x, 0.2D, this.player.velocity.z);
        }
        return this.player.velocity;
    }

    private Vec3 handleOnClimbable(Vec3 velocity) {
        if (this.onClimbable()) {
            double x = MinecraftMathHelper_1_20_4.clamp(velocity.x, -0.15000000596046448D, 0.15000000596046448D);
            double z = MinecraftMathHelper_1_20_4.clamp(velocity.z, -0.15000000596046448D, 0.15000000596046448D);
            double y = Math.max(velocity.y, -0.15000000596046448D);
            if (y < 0.0D && !this.getFeetBlockState().is(Blocks.SCAFFOLDING) && player.SNEAK) {
                y = 0.0D;
            }
            velocity = new Vec3(x, y, z);
        }
        return velocity;
    }

    public boolean onClimbable() {
        Vec3 blockPos = this.blockPosition();
        BlockState var2 = this.getFeetBlockState();
        if (var2.is(BlockTags.CLIMBABLE)) {
            player.lastClimbablePos = Optional.of(blockPos);
            return true;
        } else if (var2.getBlock() instanceof TrapDoorBlock && this.trapdoorUsableAsLadder(blockPos, var2)) {
            player.lastClimbablePos = Optional.of(blockPos);
            return true;
        } else {
            return false;
        }
    }

    private float getFrictionInfluencedSpeed(float var1) {
        // return player.GROUND ? this.getSpeed() * (0.21600002F / (var1 * var1 * var1)) : this.getFlyingSpeed();
        return player.getSpeed() * (0.21600002F / (var1 * var1 * var1));
    }

    public void moveRelative(float var1, Vec3 var2) {
        Vec3 var3 = getInputVector(var2, var1, this.getYRot());
        player.velocity.add(var3);
    }

    private static Vec3 getInputVector(Vec3 var0, float var1, float var2) {
        double var3 = var0.lengthSqr();
        if (var3 < 1.0E-7D) {
            return Vec3.ZERO;
        } else {
            Vec3 var5 = (var3 > 1.0D ? var0.normalize() : var0).scale((double) var1);
            float var6 = MinecraftMathHelper_1_20_4.sin(var2 * 0.017453292F);
            float var7 = MinecraftMathHelper_1_20_4.cos(var2 * 0.017453292F);
            return new Vec3(var5.x * (double) var7 - var5.z * (double) var6, var5.y, var5.z * (double) var7 + var5.x * (double) var6);
        }
    }

    protected void jumpFromGround() {
        player.velocity.y = getJumpPower();
        if (player.SPRINT) {
            float var2 = player.YAW * 0.017453292F;
            player.velocity.x += (double) -MinecraftMathHelper_1_20_4.sin(var2) * 0.2F;
            player.velocity.z += (double) MinecraftMathHelper_1_20_4.cos(var2) * 0.2F;
        }
        // player.hasImpulse = true;
    }

    protected float getJumpPower() {
        return 0.42F * getBlockJumpFactor() + getJumpBoostPower();
    }

    private float getJumpBoostPower() {
        return player.hasPotion(Potion.jump) ? 0.1F * ((float) player.potionEffects.get(Potion.jump).getAmplifier() + 1.0F) : 0.0F;
    }

    private float getBlockJumpFactor() {
        /*float var1 = this.level().getBlockState(this.blockPosition()).getBlock().getJumpFactor();
        float var2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
        return (double) var1 == 1.0D ? var2 : var1;*/
        return 0;
    }

    protected void goDownInWater() {
        player.velocity.add(new Vec3(0.0D, -0.03999999910593033D, 0.0D));
    }

    protected void jumpInLiquid() {
        player.velocity.add(new Vec3(0.0D, 0.03999999910593033D, 0.0D));
    }

    protected float getWaterSlowDown() {
        return 0.8F;
    }

    @Override
    public int tierCalc(double height) {
        double sum = 1.252;
        double n = 0.00301;
        int i;
        for (i = 4; sum >= height; i++) {
            sum = sum + n;
            n = (n - 0.08) * 0.98;
        }
        return i + 1;
    }

    @Override
    public boolean evalDistance(double distance) {
        return distance > 1.252;
    }

}
