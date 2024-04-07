package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.AxisCycle;
import de.legoshi.parkourcalculator.simulation.Direction;
import de.legoshi.parkourcalculator.simulation.FluidTags;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Trapdoor;
import de.legoshi.parkourcalculator.simulation.environment.block_1_20_4.PowderSnowBlock;
import de.legoshi.parkourcalculator.simulation.environment.block_1_20_4.ScaffoldingBlock;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.voxel.Shapes;
import de.legoshi.parkourcalculator.simulation.environment.voxel.VoxelShape;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_20_4;
import de.legoshi.parkourcalculator.simulation.potion.Potion;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper_1_20_4;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        travel(new Vec3((double) xxa, (double) yya, (double) zza)); // LivingEntity.java

        /*if (!this.level().isClientSide && !this.isDeadOrDying()) {
            int var15 = this.getTicksFrozen();
            if (this.isInPowderSnow && this.canFreeze()) {
                this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), var15 + 1));
            } else {
                this.setTicksFrozen(Math.max(0, var15 - 2));
            }
        }*/

        // this.removeFrost();
        // this.tryAddFrost();

        // belongs to player.java
        // player.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        player.setSpeed((float) 0.699999988079071D);

        /*if (GROUND && this.getAbilities().flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            this.getAbilities().flying = false;
            this.onUpdateAbilities();
        }*/

        double clampedX = MinecraftMathHelper_1_20_4.clamp(player.position.x, -2.9999999E7D, 2.9999999E7D);
        double clampedZ = MinecraftMathHelper_1_20_4.clamp(player.position.z, -2.9999999E7D, 2.9999999E7D);
        if (clampedX != player.position.x || clampedZ != player.position.z) {
            setPos(clampedX, player.position.y, clampedZ);
        }

        player.updatePlayerPose();
    }

    private void travel(Vec3 travelVec) {
        Player_1_20_4 player = (Player_1_20_4) this.player;

        if (player.SWIMMING) {
            double lookAngleY = getLookAngle().y;
            double var4 = lookAngleY < -0.2D ? 0.085D : 0.06D;

            ABlock fluidBlock = blockManager.getBlock(Vec3.containing(player.position.x, player.position.y + 1.0D - 0.1D, player.position.z));
            if (lookAngleY <= 0.0D || player.JUMP || !fluidBlock.getFluidState().isEmpty()) {
                player.velocity.add(new Vec3(0.0D, (lookAngleY - player.velocity.y) * var4, 0.0D));
            }
        }

        double var2 = 0.08D;
        if (player.velocity.y <= 0.0D && player.hasPotion(Potion.slow_falling)) {
            var2 = 0.01D;
        }

        FluidState fluidState = player.blockPosition.getFluidState();
        float friction = player.SPRINT ? 0.9F : this.getWaterSlowDown();
        double yPos = player.position.y;
        if (player.isInWater()) {
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
            Vec3 var12 = getFluidFallingAdjustedMovement(var2, player.velocity.y <= 0.0D, this.player.velocity);
            player.velocity = var12;
            if (player.horizontalCollision && isFree(var12.x, var12.y + 0.6000000238418579D - player.position.y + yPos, var12.z)) {
                player.velocity = new Vec3(var12.x, 0.30000001192092896D, var12.z);
            }
        } else if (player.isInLava()) {
            yPos = player.position.y;
            this.moveRelative(0.02F, travelVec);
            this.move(player.velocity);
            Vec3 var23;
            if (player.getFluidHeight(FluidTags.LAVA) <= player.getFluidJumpThreshold()) {
                player.velocity = player.velocity.multiply(0.5D, 0.800000011920929D, 0.5D);
                var23 = getFluidFallingAdjustedMovement(var2, player.velocity.y <= 0.0D, this.player.velocity);
                player.velocity = var23;
            } else {
                player.velocity = player.velocity.scale(0.5D);
            }

            player.velocity.add(new Vec3(0.0D, -var2 / 4.0D, 0.0D));

            var23 = player.velocity;
            if (player.horizontalCollision && isFree(var23.x, var23.y + 0.6000000238418579D - player.position.y + yPos, var23.z)) {
                player.velocity = new Vec3(var23.x, 0.30000001192092896D, var23.z);
            }
        } /* ELYTRA */ else if (player.ELYTRA) {
            this.checkSlowFallDistance();
            Vec3 playerVel = player.velocity;
            Vec3 lookAngle = this.getLookAngle();
            friction = player.PITCH * 0.017453292F;

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

            player.velocity = playerVel.multiply(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D);
            this.move(player.velocity);

            if (player.GROUND) {
                player.ELYTRA = false;
            }

        } else {
            ABlock movementBlock = player.getBlockPosBelowThatAffectsMyMovement();
            float blockFriction = movementBlock.getFriction();
            friction = player.GROUND ? blockFriction * 0.91F : 0.91F;
            Vec3 var26 = this.handleRelativeFrictionAndCalculateMovement(travelVec, blockFriction);
            double var10 = var26.y;
            if (player.hasPotion(Potion.levitation)) {
                var10 += (0.05D * (double) (player.getPotionEffects().get(Potion.levitation).getAmplifier() + 1) - var26.y) * 0.2D;
            }

            var10 -= var2;

            if (player.shouldDiscardFriction()) {
                player.velocity = new Vec3(var26.x, var10, var26.z);
            } else {
                player.velocity = new Vec3(var26.x * (double) friction, var10 * 0.9800000190734863D, var26.z * (double) friction);
            }
        }
    }

    public void checkSlowFallDistance() {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        if (player.velocity.y > -0.5D && player.fallDistance > 1.0F) {
            player.fallDistance = 1.0F;
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
        Vec3 var3 = collide(var2);
        double var4 = var3.lengthSqr();
        if (var4 > 1.0E-7D) {
            /*if (player.fallDistance != 0.0F && var4 >= 1.0D) {
                BlockHitResult var6 = this.level().clip(new ClipContext(this.position(), this.position().add(var3), ClipContext.Block.FALLDAMAGE_RESETTING, ClipContext.Fluid.WATER, this));
                if (var6.getType() != HitResult.Type.MISS) {
                    player.fallDistance = 0.0F;
                }
            }*/
            setPos(player.position.x + var3.x, player.position.y + var3.y, player.position.z + var3.z);
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
        this.checkFallDamage(var3.y, player.GROUND);

        if (player.horizontalCollision) {
            Vec3 var10 = player.velocity;
            player.velocity = new Vec3(var23 ? 0.0D : var10.x, var10.y, var7 ? 0.0D : var10.z);
        }

        ABlock block = player.getOnPosLegacy();
        if (var2.y != var3.y) {
            block.updateEntityAfterFallOn(player);
        }

        if (player.GROUND) {
            block.onEntityCollidedWithBlock(player);
        }

        this.tryCheckInsideBlocks();
        float blockSpeedFactor = player.getBlockSpeedFactor();
        player.velocity = player.velocity.multiply((double) blockSpeedFactor, 1.0D, (double) blockSpeedFactor);
    }

    private Vec3 collide(Vec3 velocity) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        AxisAlignedBB playerBB = player.playerBB;
        List<AxisAlignedBB> collidingBBs = getCollidingBoundingBoxes(playerBB.expandTowards(velocity));
        Vec3 var4 = velocity.lengthSqr() == 0.0D ? velocity : collideBoundingBox(velocity, playerBB, collidingBBs);
        boolean xHasMoved = velocity.x != var4.x;
        boolean yHasMoved = velocity.y != var4.y;
        boolean zHasMoved = velocity.z != var4.z;
        boolean var8 = player.GROUND || yHasMoved && velocity.y < 0.0D;
        if (player.maxUpStep > 0.0F && var8 && (xHasMoved || zHasMoved)) {
            Vec3 velStepUp = collideBoundingBox(new Vec3(velocity.x, (double) player.maxUpStep, velocity.z), playerBB, collidingBBs);
            Vec3 noVelStepUp = collideBoundingBox(new Vec3(0.0D, (double) player.maxUpStep, 0.0D), playerBB.expandTowards(velocity.x, 0.0D, velocity.z), collidingBBs);
            if (noVelStepUp.y < (double) player.maxUpStep) {
                collideBoundingBox(new Vec3(velocity.x, 0.0D, velocity.z), playerBB.offset(noVelStepUp), collidingBBs).add(noVelStepUp);
                if (noVelStepUp.horizontalDistanceSqr() > velStepUp.horizontalDistanceSqr()) {
                    velStepUp = noVelStepUp;
                }
            }

            if (velStepUp.horizontalDistanceSqr() > var4.horizontalDistanceSqr()) {
                velStepUp.add(collideBoundingBox(new Vec3(0.0D, -velStepUp.y + velocity.y, 0.0D), playerBB.offset(velStepUp), collidingBBs));
                return velStepUp;
            }
        }

        return var4;
    }

    public Vec3 collideBoundingBox(Vec3 velocity, AxisAlignedBB playerBB, List<AxisAlignedBB> collidingBBs) {
        List<AxisAlignedBB> bbs = new ArrayList<>();
        if (!collidingBBs.isEmpty()) {
            bbs.addAll(collidingBBs);
        }

        bbs.addAll(getCollidingBoundingBoxes(playerBB.expandTowards(velocity)));

        List<VoxelShape> voxelShapes = collidingBBs.stream().map(Shapes::create).toList();
        return collideWithShapes(velocity, playerBB, voxelShapes);
    }

    private Vec3 collideWithShapes(Vec3 velocity, AxisAlignedBB playerBB, List<VoxelShape> collidingVoxels) {
        if (collidingVoxels.isEmpty()) {
            return velocity;
        } else {
            double x = velocity.x;
            double y = velocity.y;
            double z = velocity.z;
            if (y != 0.0D) {
                y = Shapes.collide(Direction.Axis.Y, playerBB, collidingVoxels, y);
                if (y != 0.0D) {
                    playerBB = playerBB.offset(0.0D, y, 0.0D);
                }
            }

            boolean var9 = Math.abs(x) < Math.abs(z);
            if (var9 && z != 0.0D) {
                z = Shapes.collide(Direction.Axis.Z, playerBB, collidingVoxels, z);
                if (z != 0.0D) {
                    playerBB = playerBB.offset(0.0D, 0.0D, z);
                }
            }

            if (x != 0.0D) {
                x = Shapes.collide(Direction.Axis.X, playerBB, collidingVoxels, x);
                if (!var9 && x != 0.0D) {
                    playerBB = playerBB.offset(x, 0.0D, 0.0D);
                }
            }

            if (!var9 && z != 0.0D) {
                z = Shapes.collide(Direction.Axis.Z, playerBB, collidingVoxels, z);
            }

            return new Vec3(x, y, z);
        }
    }

    protected Vec3 maybeBackOffFromEdge(Vec3 var1) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        if (var1.y <= 0.0D && player.SNEAK && player.isAboveGround()) {
            double velX = var1.x;
            double velZ = var1.z;
            double var7 = 0.05D;

            while (true) {
                while (velX != 0.0D && getCollidingBoundingBoxes(player.playerBB.offset(velX, (double) (-player.maxUpStep), 0.0D)).isEmpty()) {
                    if (velX < 0.05D && velX >= -0.05D) {
                        velX = 0.0D;
                    } else if (velX > 0.0D) {
                        velX -= 0.05D;
                    } else {
                        velX += 0.05D;
                    }
                }

                while (true) {
                    while (velZ != 0.0D && getCollidingBoundingBoxes(player.playerBB.offset(0.0D, (double) (-player.maxUpStep), velZ)).isEmpty()) {
                        if (velZ < 0.05D && velZ >= -0.05D) {
                            velZ = 0.0D;
                        } else if (velZ > 0.0D) {
                            velZ -= 0.05D;
                        } else {
                            velZ += 0.05D;
                        }
                    }

                    while (true) {
                        while (velX != 0.0D && velZ != 0.0D && getCollidingBoundingBoxes(player.playerBB.offset(velX, (double) (-player.maxUpStep), velZ)).isEmpty()) {
                            if (velX < 0.05D && velX >= -0.05D) {
                                velX = 0.0D;
                            } else if (velX > 0.0D) {
                                velX -= 0.05D;
                            } else {
                                velX += 0.05D;
                            }

                            if (velZ < 0.05D && velZ >= -0.05D) {
                                velZ = 0.0D;
                            } else if (velZ > 0.0D) {
                                velZ -= 0.05D;
                            } else {
                                velZ += 0.05D;
                            }
                        }

                        var1 = new Vec3(velX, var1.y, velZ);
                        return var1;
                    }
                }
            }
        } else {
            return var1;
        }
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 var1, float var2) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        this.moveRelative(this.getFrictionInfluencedSpeed(var2), var1);
        this.player.velocity = this.handleOnClimbable(player.velocity);
        this.move(player.velocity);
        if ((player.horizontalCollision || player.JUMP) && (this.onClimbable()
                || (player.feetBlockState instanceof PowderSnowBlock) /*&& PowderSnowBlock.canEntityWalkOnPowderSnow(this))*/)) {
            this.player.velocity = new Vec3(this.player.velocity.x, 0.2D, this.player.velocity.z);
        }
        return this.player.velocity;
    }

    private Vec3 handleOnClimbable(Vec3 velocity) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        if (this.onClimbable()) {
            double x = MinecraftMathHelper_1_20_4.clamp(velocity.x, -0.15000000596046448D, 0.15000000596046448D);
            double z = MinecraftMathHelper_1_20_4.clamp(velocity.z, -0.15000000596046448D, 0.15000000596046448D);
            double y = Math.max(velocity.y, -0.15000000596046448D);
            if (y < 0.0D && !(player.feetBlockState instanceof ScaffoldingBlock) && player.SNEAK) {
                y = 0.0D;
            }
            velocity = new Vec3(x, y, z);
        }
        return velocity;
    }

    public boolean onClimbable() {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        ABlock climbBlock = player.blockPosition;
        if (climbBlock.canClimb()) {
            player.lastClimbablePos = Optional.of(climbBlock);
            return true;
        } else if (climbBlock instanceof Trapdoor && this.trapdoorUsableAsLadder(climbBlock)) {
            player.lastClimbablePos = Optional.of(climbBlock);
            return true;
        } else {
            return false;
        }
    }

    public Vec3 getFluidFallingAdjustedMovement(double var1, boolean var3, Vec3 var4) {
        if (!player.SPRINT) {
            double var5;
            if (var3 && Math.abs(var4.y - 0.005D) >= 0.003D && Math.abs(var4.y - var1 / 16.0D) < 0.003D) {
                var5 = -0.003D;
            } else {
                var5 = var4.y - var1 / 16.0D;
            }

            return new Vec3(var4.x, var5, var4.z);
        } else {
            return var4;
        }
    }

    private float getFrictionInfluencedSpeed(float var1) {
        // return player.GROUND ? this.getSpeed() * (0.21600002F / (var1 * var1 * var1)) : this.getFlyingSpeed();
        return player.getSpeed() * (0.21600002F / (var1 * var1 * var1));
    }

    public void moveRelative(float var1, Vec3 var2) {
        Vec3 var3 = getInputVector(var2, var1, player.YAW);
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

    protected void tryCheckInsideBlocks() {
        this.checkInsideBlocks();
    }

    protected void checkInsideBlocks() {
        AxisAlignedBB var1 = player.playerBB;
        ABlock aBlock1 = blockManager.getBlock(Vec3.containing(var1.minX + 1.0E-7D, var1.minY + 1.0E-7D, var1.minZ + 1.0E-7D));
        ABlock aBlock2 = blockManager.getBlock(Vec3.containing(var1.maxX - 1.0E-7D, var1.maxY - 1.0E-7D, var1.maxZ - 1.0E-7D));
        ABlock aBlockResult;

        for (int var5 = aBlock1.getX(); var5 <= aBlock2.getX(); ++var5) {
            for (int var6 = aBlock1.getY(); var6 <= aBlock2.getY(); ++var6) {
                for (int var7 = aBlock1.getZ(); var7 <= aBlock2.getZ(); ++var7) {
                    aBlockResult = blockManager.getBlock(new Vec3(var5, var6, var7));
                    aBlockResult.entityInside(player);
                }
            }
        }
    }

    public void setOnGroundWithKnownMovement(boolean var1, Vec3 var2) {
        player.GROUND = var1;
        this.checkSupportingBlock(var1, var2);
    }

    protected void checkSupportingBlock(boolean var1, Vec3 var2) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        if (var1) {
            AxisAlignedBB var3 = player.playerBB;
            AxisAlignedBB var4 = new AxisAlignedBB(var3.minX, var3.minY - 1.0E-6D, var3.minZ, var3.maxX, var3.minY, var3.maxZ);
            Optional<ABlock> var5 = findSupportingBlock(var4);
            if (var5.isEmpty() && !player.onGroundNoBlocks) {
                if (var2 != null) {
                    AxisAlignedBB var6 = var4.offset(-var2.x, 0.0D, -var2.z);
                    var5 = findSupportingBlock(var6);
                    player.mainSupportingBlockPos = var5;
                }
            } else {
                player.mainSupportingBlockPos = var5;
            }

            player.onGroundNoBlocks = var5.isEmpty();
        } else {
            player.onGroundNoBlocks = false;
            if (player.mainSupportingBlockPos.isPresent()) {
                player.mainSupportingBlockPos = Optional.empty();
            }
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
        Player_1_20_4 player = (Player_1_20_4) this.player;
        float var1 = player.blockPosition.getJumpFactor();
        float var2 = blockManager.getBlock(player.getBlockPosBelowThatAffectsMyMovement()).getJumpFactor();
        return (double) var1 == 1.0D ? var2 : var1;
    }

    protected void jumpInLiquid() {
        player.velocity.add(new Vec3(0.0D, 0.03999999910593033D, 0.0D));
    }

    protected float getWaterSlowDown() {
        return 0.8F;
    }

    public boolean isFree(double var1, double var3, double var5) {
        return this.isFree(player.playerBB.offset(var1, var3, var5));
    }

    private boolean isFree(AxisAlignedBB var1) {
        return getCollidingBoundingBoxes(var1).isEmpty() && containsAnyLiquid(var1);
    }

    private boolean containsAnyLiquid(AxisAlignedBB var1) {
        int var2 = MinecraftMathHelper_1_20_4.floor(var1.minX);
        int var3 = MinecraftMathHelper_1_20_4.ceil(var1.maxX);
        int var4 = MinecraftMathHelper_1_20_4.floor(var1.minY);
        int var5 = MinecraftMathHelper_1_20_4.ceil(var1.maxY);
        int var6 = MinecraftMathHelper_1_20_4.floor(var1.minZ);
        int var7 = MinecraftMathHelper_1_20_4.ceil(var1.maxZ);

        for(int var9 = var2; var9 < var3; ++var9) {
            for(int var10 = var4; var10 < var5; ++var10) {
                for(int var11 = var6; var11 < var7; ++var11) {
                    ABlock block = blockManager.getBlock(new Vec3(var9, var10, var11));
                    if (!block.getFluidState().isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Vec3 getLookAngle() {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        return this.calculateViewVector(player.PITCH, player.YAW);
    }

    protected final Vec3 calculateViewVector(float var1, float var2) {
        float var3 = var1 * 0.017453292F;
        float var4 = -var2 * 0.017453292F;
        float var5 = MinecraftMathHelper_1_20_4.cos(var4);
        float var6 = MinecraftMathHelper_1_20_4.sin(var4);
        float var7 = MinecraftMathHelper_1_20_4.cos(var3);
        float var8 = MinecraftMathHelper_1_20_4.sin(var3);
        return new Vec3((double) (var6 * var7), (double) (-var8), (double) (var5 * var7));
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

    public void setPos(double var1, double var3, double var5) {
        this.setPosRaw(var1, var3, var5);
        player.playerBB = this.makeBoundingBox(player.position);
    }

    public AxisAlignedBB makeBoundingBox(Vec3 var1) {
        return makeBoundingBox(var1.x, var1.y, var1.z);
    }

    public AxisAlignedBB makeBoundingBox(double var1, double var3, double var5) {
        float var7 = player.width / 2.0F;
        float var8 = player.height;
        return new AxisAlignedBB(var1 - (double) var7, var3, var5 - (double) var7, var1 + (double) var7, var3 + (double) var8, var5 + (double) var7);
    }

    public final void setPosRaw(double var1, double var3, double var5) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        if (player.position.x != var1 || player.position.y != var3 || player.position.z != var5) {
            player.position = new Vec3(var1, var3, var5);
            int var7 = MinecraftMathHelper_1_20_4.floor(var1);
            int var8 = MinecraftMathHelper_1_20_4.floor(var3);
            int var9 = MinecraftMathHelper_1_20_4.floor(var5);
            if (var7 != player.blockPosition.getX() || var8 != player.blockPosition.getY() || var9 != player.blockPosition.getZ()) {
                player.blockPosition = blockManager.getBlock(new Vec3(var7, var8, var9));
                player.feetBlockState = null;
            }
        }
    }

    private boolean trapdoorUsableAsLadder(ABlock block) {
        /*if ((Boolean)var2.getValue(TrapDoorBlock.OPEN)) {
            BlockState var3 = this.level().getBlockState(var1.below());
            if (var3.is(Blocks.LADDER) && var3.getValue(LadderBlock.FACING) == var2.getValue(TrapDoorBlock.FACING)) {
                return true;
            }
        }*/
        return false;
    }

    protected void checkFallDamage(double var1, boolean isOnGround) {
        Player_1_20_4 player = (Player_1_20_4) this.player;
        if (!player.isInWater()) {
            player.updateInWaterStateAndDoWaterCurrentPushing();
        }

        if (isOnGround && player.fallDistance > 0.0F) {
            // player.removeSoulSpeed();
            // player.tryAddSoulSpeed();
        }

        if (isOnGround) {
            player.fallDistance = 0.0F;
        } else if (var1 < 0.0D) {
            player.fallDistance -= (float) var1;
        }

        if (isOnGround) {
            player.lastClimbablePos = Optional.empty();
        }

    }

    public Optional<ABlock> findSupportingBlock(AxisAlignedBB var2) {
        ABlock var3 = null;
        double var4 = Double.MAX_VALUE;
        BlockCollisions var6 = new BlockCollisions(this, var2, false, (var0, var1x) -> {
            return var0;
        });

        while(true) {
            ABlock var7;
            double var8;
            do {
                if (!var6.hasNext()) {
                    return Optional.ofNullable(var3);
                }

                var7 = (BlockPos)var6.next();
                var8 = var7.distToCenterSqr(var1.position());
            } while(!(var8 < var4) && (var8 != var4 || var3 != null && var3.compareTo(var7) >= 0));

            var3 = var7.immutable();
            var4 = var8;
        }
    }

}
