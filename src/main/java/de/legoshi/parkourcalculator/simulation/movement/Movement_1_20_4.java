package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_20_4;
import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;
import de.legoshi.parkourcalculator.util.Vec3;

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

        // code from LivingEntity
        super.aiStep();
        /*if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }*/

        this.lerpSteps = 0;
        if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            --this.lerpSteps;
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        if (this.lerpHeadSteps > 0) {
            this.lerpHeadRotationStep(this.lerpHeadSteps, this.lerpYHeadRot);
            --this.lerpHeadSteps;
        }

        if (Math.abs(player.velocity.x) < 0.003D) player.velocity.x = 0.0D;
        if (Math.abs(player.velocity.y) < 0.003D) player.velocity.y = 0.0D;
        if (Math.abs(player.velocity.z) < 0.003D) player.velocity.z = 0.0D;

        if (this.jumping && this.isAffectedByFluids()) {
            double var8;
            if (this.isInLava()) {
                var8 = this.getFluidHeight(FluidTags.LAVA);
            } else {
                var8 = this.getFluidHeight(FluidTags.WATER);
            }

            boolean var10 = this.isInWater() && var8 > 0.0D;
            double var11 = this.getFluidJumpThreshold();
            if (!var10 || this.onGround() && !(var8 > var11)) {
                if (this.isInLava() && (!this.onGround() || var8 > var11)) {
                    this.jumpInLiquid(FluidTags.LAVA);
                } else if ((this.onGround() || var10 && var8 <= var11) && this.noJumpDelay == 0) {
                    this.jumpFromGround();
                    this.noJumpDelay = 10;
                }
            } else {
                this.jumpInLiquid(FluidTags.WATER);
            }
        } else {
            this.noJumpDelay = 0;
        }

        this.xxa *= 0.98F;
        this.zza *= 0.98F;
        this.updateFallFlying();
        AABB var13 = this.getBoundingBox();
        Vec3 var9 = new Vec3((double)this.xxa, (double)this.yya, (double)this.zza);
        if (this.hasEffect(MobEffects.SLOW_FALLING) || this.hasEffect(MobEffects.LEVITATION)) {
            this.resetFallDistance();
        }

        // player.java
        double var2;
        if (this.isSwimming() && !this.isPassenger()) {
            var2 = this.getLookAngle().y;
            double var4 = var2 < -0.2D ? 0.085D : 0.06D;
            if (var2 <= 0.0D || this.jumping || !this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
                Vec3 var6 = this.getDeltaMovement();
                this.setDeltaMovement(var6.add(0.0D, (var2 - var6.y) * var4, 0.0D));
            }
        }

        if (this.abilities.flying && !this.isPassenger()) {
            var2 = this.getDeltaMovement().y;
            travel(var1); // LivingEntity.java
            Vec3 var7 = this.getDeltaMovement();
            this.setDeltaMovement(var7.x, var2 * 0.6D, var7.z);
            this.resetFallDistance();
            this.setSharedFlag(7, false);
        } else {
            travel(var1); // LivingEntity.java
        }

        if (!this.level().isClientSide && !this.isDeadOrDying()) {
            int var15 = this.getTicksFrozen();
            if (this.isInPowderSnow && this.canFreeze()) {
                this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), var15 + 1));
            } else {
                this.setTicksFrozen(Math.max(0, var15 - 2));
            }
        }

        this.removeFrost();
        this.tryAddFrost();

        // belongs to player.java
        this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float var1;
        if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
            var1 = Math.min(0.1F, (float)this.getDeltaMovement().horizontalDistance());
        } else {
            var1 = 0.0F;
        }

        /*if (GROUND && this.getAbilities().flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            this.getAbilities().flying = false;
            this.onUpdateAbilities();
        }*/
    }

    private void travel(Vec3 var1) {
        if (this.isControlledByLocalInstance()) {
            double var2 = 0.08D;
            boolean var4 = this.getDeltaMovement().y <= 0.0D;
            if (var4 && this.hasEffect(MobEffects.SLOW_FALLING)) {
                var2 = 0.01D;
            }

            FluidState var5 = this.level().getFluidState(this.blockPosition());
            float var8;
            double var25;
            if (this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid(var5)) {
                var25 = this.getY();
                var8 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float var27 = 0.02F;
                float var29 = (float)EnchantmentHelper.getDepthStrider(this);
                if (var29 > 3.0F) {
                    var29 = 3.0F;
                }

                if (!this.onGround()) {
                    var29 *= 0.5F;
                }

                if (var29 > 0.0F) {
                    var8 += (0.54600006F - var8) * var29 / 3.0F;
                    var27 += (this.getSpeed() - var27) * var29 / 3.0F;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    var8 = 0.96F;
                }

                this.moveRelative(var27, var1);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 var28 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    var28 = new Vec3(var28.x, 0.2D, var28.z);
                }

                this.setDeltaMovement(var28.multiply((double)var8, 0.800000011920929D, (double)var8));
                Vec3 var12 = this.getFluidFallingAdjustedMovement(var2, var4, this.getDeltaMovement());
                this.setDeltaMovement(var12);
                if (this.horizontalCollision && this.isFree(var12.x, var12.y + 0.6000000238418579D - this.getY() + var25, var12.z)) {
                    this.setDeltaMovement(var12.x, 0.30000001192092896D, var12.z);
                }
            } else if (this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(var5)) {
                var25 = this.getY();
                this.moveRelative(0.02F, var1);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 var23;
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.800000011920929D, 0.5D));
                    var23 = this.getFluidFallingAdjustedMovement(var2, var4, this.getDeltaMovement());
                    this.setDeltaMovement(var23);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                }

                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -var2 / 4.0D, 0.0D));
                }

                var23 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(var23.x, var23.y + 0.6000000238418579D - this.getY() + var25, var23.z)) {
                    this.setDeltaMovement(var23.x, 0.30000001192092896D, var23.z);
                }
            } else if (this.isFallFlying()) {
                this.checkSlowFallDistance();
                Vec3 var6 = this.getDeltaMovement();
                Vec3 var7 = this.getLookAngle();
                var8 = this.getXRot() * 0.017453292F;
                double var9 = Math.sqrt(var7.x * var7.x + var7.z * var7.z);
                double var11 = var6.horizontalDistance();
                double var13 = var7.length();
                double var15 = Math.cos((double)var8);
                var15 = var15 * var15 * Math.min(1.0D, var13 / 0.4D);
                var6 = this.getDeltaMovement().add(0.0D, var2 * (-1.0D + var15 * 0.75D), 0.0D);
                double var17;
                if (var6.y < 0.0D && var9 > 0.0D) {
                    var17 = var6.y * -0.1D * var15;
                    var6 = var6.add(var7.x * var17 / var9, var17, var7.z * var17 / var9);
                }

                if (var8 < 0.0F && var9 > 0.0D) {
                    var17 = var11 * (double)(-Mth.sin(var8)) * 0.04D;
                    var6 = var6.add(-var7.x * var17 / var9, var17 * 3.2D, -var7.z * var17 / var9);
                }

                if (var9 > 0.0D) {
                    var6 = var6.add((var7.x / var9 * var11 - var6.x) * 0.1D, 0.0D, (var7.z / var9 * var11 - var6.z) * 0.1D);
                }

                this.setDeltaMovement(var6.multiply(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.horizontalCollision && !this.level().isClientSide) {
                    var17 = this.getDeltaMovement().horizontalDistance();
                    double var19 = var11 - var17;
                    float var21 = (float)(var19 * 10.0D - 3.0D);
                    if (var21 > 0.0F) {
                        this.playSound(this.getFallDamageSound((int)var21), 1.0F, 1.0F);
                        this.hurt(this.damageSources().flyIntoWall(), var21);
                    }
                }

                if (this.onGround() && !this.level().isClientSide) {
                    this.setSharedFlag(7, false);
                }
            } else {
                BlockPos var24 = this.getBlockPosBelowThatAffectsMyMovement();
                float var22 = this.level().getBlockState(var24).getBlock().getFriction();
                var8 = this.onGround() ? var22 * 0.91F : 0.91F;
                Vec3 var26 = this.handleRelativeFrictionAndCalculateMovement(var1, var22);
                double var10 = var26.y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    var10 += (0.05D * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - var26.y) * 0.2D;
                } else if (this.level().isClientSide && !this.level().hasChunkAt(var24)) {
                    if (this.getY() > (double)this.level().getMinBuildHeight()) {
                        var10 = -0.1D;
                    } else {
                        var10 = 0.0D;
                    }
                } else if (!this.isNoGravity()) {
                    var10 -= var2;
                }

                if (this.shouldDiscardFriction()) {
                    this.setDeltaMovement(var26.x, var10, var26.z);
                } else {
                    this.setDeltaMovement(var26.x * (double)var8, var10 * 0.9800000190734863D, var26.z * (double)var8);
                }
            }
        }

        this.calculateEntityAnimation(this instanceof FlyingAnimal);
    }

    public void move(MoverType var1, Vec3 var2) {
        if (this.noPhysics) {
            this.setPos(this.getX() + var2.x, this.getY() + var2.y, this.getZ() + var2.z);
        } else {
            this.wasOnFire = this.isOnFire();
            if (var1 == MoverType.PISTON) {
                var2 = this.limitPistonMovement(var2);
                if (var2.equals(Vec3.ZERO)) {
                    return;
                }
            }

            this.level().getProfiler().push("move");
            if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
                var2 = var2.multiply(this.stuckSpeedMultiplier);
                this.stuckSpeedMultiplier = Vec3.ZERO;
                this.setDeltaMovement(Vec3.ZERO);
            }

            var2 = this.maybeBackOffFromEdge(var2, var1);
            Vec3 var3 = this.collide(var2);
            double var4 = var3.lengthSqr();
            if (var4 > 1.0E-7D) {
                if (this.fallDistance != 0.0F && var4 >= 1.0D) {
                    BlockHitResult var6 = this.level().clip(new ClipContext(this.position(), this.position().add(var3), ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this));
                    if (var6.getType() != HitResult.Type.MISS) {
                        this.resetFallDistance();
                    }
                }

                this.setPos(this.getX() + var3.x, this.getY() + var3.y, this.getZ() + var3.z);
            }

            this.level().getProfiler().pop();
            this.level().getProfiler().push("rest");
            boolean var23 = !Mth.equal(var2.x, var3.x);
            boolean var7 = !Mth.equal(var2.z, var3.z);
            this.horizontalCollision = var23 || var7;
            this.verticalCollision = var2.y != var3.y;
            this.verticalCollisionBelow = this.verticalCollision && var2.y < 0.0D;
            if (this.horizontalCollision) {
                this.minorHorizontalCollision = this.isHorizontalCollisionMinor(var3);
            } else {
                this.minorHorizontalCollision = false;
            }

            this.setOnGroundWithKnownMovement(this.verticalCollisionBelow, var3);
            BlockPos var8 = this.getOnPosLegacy();
            BlockState var9 = this.level().getBlockState(var8);
            this.checkFallDamage(var3.y, this.onGround(), var9, var8);
            if (this.isRemoved()) {
                this.level().getProfiler().pop();
            } else {
                if (this.horizontalCollision) {
                    Vec3 var10 = this.getDeltaMovement();
                    this.setDeltaMovement(var23 ? 0.0D : var10.x, var10.y, var7 ? 0.0D : var10.z);
                }

                Block var24 = var9.getBlock();
                if (var2.y != var3.y) {
                    var24.updateEntityAfterFallOn(this.level(), this);
                }

                if (this.onGround()) {
                    var24.stepOn(this.level(), var8, var9, this);
                }

                Entity.MovementEmission var11 = this.getMovementEmission();
                if (var11.emitsAnything() && !this.isPassenger()) {
                    double var12 = var3.x;
                    double var14 = var3.y;
                    double var16 = var3.z;
                    this.flyDist += (float)(var3.length() * 0.6D);
                    BlockPos var18 = this.getOnPos();
                    BlockState var19 = this.level().getBlockState(var18);
                    boolean var20 = this.isStateClimbable(var19);
                    if (!var20) {
                        var14 = 0.0D;
                    }

                    this.walkDist += (float)var3.horizontalDistance() * 0.6F;
                    this.moveDist += (float)Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16) * 0.6F;
                    if (this.moveDist > this.nextStep && !var19.isAir()) {
                        boolean var21 = var18.equals(var8);
                        boolean var22 = this.vibrationAndSoundEffectsFromBlock(var8, var9, var11.emitsSounds(), var21, var2);
                        if (!var21) {
                            var22 |= this.vibrationAndSoundEffectsFromBlock(var18, var19, false, var11.emitsEvents(), var2);
                        }

                        if (var22) {
                            this.nextStep = this.nextStep();
                        } else if (this.isInWater()) {
                            this.nextStep = this.nextStep();
                            if (var11.emitsSounds()) {
                                this.waterSwimSound();
                            }

                            if (var11.emitsEvents()) {
                                this.gameEvent(GameEvent.SWIM);
                            }
                        }
                    } else if (var19.isAir()) {
                        this.processFlappingMovement();
                    }
                }

                this.tryCheckInsideBlocks();
                float var25 = this.getBlockSpeedFactor();
                this.setDeltaMovement(this.getDeltaMovement().multiply((double)var25, 1.0D, (double)var25));
                if (this.level().getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6D)).noneMatch((var0) -> {
                    return var0.is(BlockTags.FIRE) || var0.is(Blocks.LAVA);
                })) {
                    if (this.remainingFireTicks <= 0) {
                        this.setRemainingFireTicks(-this.getFireImmuneTicks());
                    }

                    if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
                        this.playEntityOnFireExtinguishedSound();
                    }
                }

                if (this.isOnFire() && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
                    this.setRemainingFireTicks(-this.getFireImmuneTicks());
                }

                this.level().getProfiler().pop();
            }
        }
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 var1, MoverType var2) {
        if (!this.abilities.flying && var1.y <= 0.0D && (var2 == MoverType.SELF || var2 == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
            double var3 = var1.x;
            double var5 = var1.z;
            double var7 = 0.05D;

            while(true) {
                while(var3 != 0.0D && this.level().noCollision(this, this.getBoundingBox().move(var3, (double)(-this.maxUpStep()), 0.0D))) {
                    if (var3 < 0.05D && var3 >= -0.05D) {
                        var3 = 0.0D;
                    } else if (var3 > 0.0D) {
                        var3 -= 0.05D;
                    } else {
                        var3 += 0.05D;
                    }
                }

                while(true) {
                    while(var5 != 0.0D && this.level().noCollision(this, this.getBoundingBox().move(0.0D, (double)(-this.maxUpStep()), var5))) {
                        if (var5 < 0.05D && var5 >= -0.05D) {
                            var5 = 0.0D;
                        } else if (var5 > 0.0D) {
                            var5 -= 0.05D;
                        } else {
                            var5 += 0.05D;
                        }
                    }

                    while(true) {
                        while(var3 != 0.0D && var5 != 0.0D && this.level().noCollision(this, this.getBoundingBox().move(var3, (double)(-this.maxUpStep()), var5))) {
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

    public void moveRelative(float var1, Vec3 var2) {
        Vec3 var3 = getInputVector(var2, var1, this.getYRot());
        this.setDeltaMovement(this.getDeltaMovement().add(var3));
    }

    private static Vec3 getInputVector(Vec3 var0, float var1, float var2) {
        double var3 = var0.lengthSqr();
        if (var3 < 1.0E-7D) {
            return Vec3.ZERO;
        } else {
            Vec3 var5 = (var3 > 1.0D ? var0.normalize() : var0).scale((double)var1);
            float var6 = Mth.sin(var2 * 0.017453292F);
            float var7 = Mth.cos(var2 * 0.017453292F);
            return new Vec3(var5.x * (double)var7 - var5.z * (double)var6, var5.y, var5.z * (double)var7 + var5.x * (double)var6);
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
