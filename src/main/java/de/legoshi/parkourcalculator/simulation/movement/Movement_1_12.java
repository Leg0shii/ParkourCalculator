package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.Parkour_1_8;
import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_12;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_12;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper;
import de.legoshi.parkourcalculator.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Movement_1_12 extends Movement {

    private final Player_1_12 player_1_12;
    private final BlockManager_1_12 blockManager_1_12;

    private int tick;

    public Movement_1_12(Player player, BlockManager blockManager) {
        this.player_1_12 = (Player_1_12) player;
        this.blockManager_1_12 = (BlockManager_1_12) blockManager;
        this.playerTickInformations = new ArrayList<>();
    }

    // check if player is on ground.... (STUPID)
    private boolean preparePlayer() {
        boolean onGround = false;
        tick = -1;
        player_1_12.resetPlayer();

        // check if the player is without true velocity on the ground
        calculateTick(new InputTick());
        if (player_1_12.getPlayerTickInformation().isGround()) onGround = true;
        player_1_12.resetPlayer();

        return onGround;
    }

    @Override
    public PlayerTickInformation getLandOnBlock(List<InputTick> inputTicks, ABlock aBlock) {
        player_1_12.resetPlayer();
        for (InputTick inputTick : inputTicks) calculateTick(inputTick);
        return getLandTick(aBlock);
    }

    public void resetPlayer() {
        player_1_12.startYAW = Parkour_1_8.START_YAW;
        player_1_12.setStartPos(Parkour_1_8.DEFAULT_START);
        player_1_12.setStartVel(Parkour_1_8.DEFAULT_VELOCITY);
        player_1_12.resetPlayer();
    }

    public List<PlayerTickInformation> updatePath(List<InputTick> inputTicks) {
        playerTickInformations = new ArrayList<>();
        if (inputTicks.size() == 0) return playerTickInformations;

        boolean onGround = preparePlayer();
        playerTickInformations.add(player_1_12.getPlayerTickInformation()); // add starting position

        player_1_12.GROUND = onGround;
        for (InputTick inputTick : inputTicks) {
            calculateTick(inputTick);
            playerTickInformations.add(player_1_12.getPlayerTickInformation());
        }

        return playerTickInformations;
    }

    public void calculateTick(InputTick inputTick) {
        player_1_12.applyInput(inputTick);

        handleWaterMovement();
        handleLavaMovement();

        if (Math.abs(player_1_12.velocity.x) < 0.003D) player_1_12.velocity.x = 0.0D;
        if (Math.abs(player_1_12.velocity.y) < 0.003D) player_1_12.velocity.y = 0.0D;
        if (Math.abs(player_1_12.velocity.z) < 0.003D) player_1_12.velocity.z = 0.0D;

        if (player_1_12.JUMP) {
            if (player_1_12.WATER || player_1_12.LAVA) {
                player_1_12.velocity.y += 0.03999999910593033D;
            } else if (player_1_12.GROUND) {
                player_1_12.velocity.y = 0.42F;
                if (player_1_12.SPRINT) {
                    float f = player_1_12.YAW * 0.017453292F;
                    player_1_12.velocity.x = player_1_12.velocity.x - MinecraftMathHelper.sin(f) * 0.2F;
                    player_1_12.velocity.z = player_1_12.velocity.z + MinecraftMathHelper.cos(f) * 0.2F;
                }
            }
        }

        player_1_12.moveStrafe = player_1_12.moveStrafe * 0.98F;
        player_1_12.moveForward = player_1_12.moveForward * 0.98F;

        if (!player_1_12.WATER) {
            if (!player_1_12.LAVA) {
                if (player_1_12.SPRINT && (player_1_12.moveForward < 0.8F || player_1_12.isCollidedHorizontally)) {
                    player_1_12.SPRINT = false;
                }

                ABlock block = blockManager_1_12.getBlock(
                        MinecraftMathHelper.floor_double(player_1_12.position.x),
                        MinecraftMathHelper.floor_double(player_1_12.position.y-1),
                        MinecraftMathHelper.floor_double(player_1_12.position.z)
                );

                float mult =  0.91F;
                if (player_1_12.GROUND) mult = mult * block.slipperiness.value;
                float acceleration = 0.16277136F / (mult * mult * mult);

                float movement;
                if (player_1_12.SPRINT) movement = 0.130000010133F;
                else movement = 0.1F;

                float movementFactor;
                if (player_1_12.GROUND) movementFactor = movement * acceleration;
                else movementFactor = player_1_12.jumpMovementFactor;

                moveFlying(player_1_12.moveStrafe, player_1_12.moveForward, movementFactor);
                mult = 0.91F;

                block = blockManager_1_12.getBlock(
                        MinecraftMathHelper.floor_double(player_1_12.position.x),
                        MinecraftMathHelper.floor_double(player_1_12.position.y-1),
                        MinecraftMathHelper.floor_double(player_1_12.position.z)
                );

                if (player_1_12.GROUND) {
                    mult = block.slipperiness.value * 0.91F;
                }

                // calculate ladder
                if (player_1_12.isOnLadder(blockManager_1_12)) {
                    float f6 = 0.15F;
                    player_1_12.velocity.x = MinecraftMathHelper.clamp_double(player_1_12.velocity.x, -f6, f6);
                    player_1_12.velocity.z = MinecraftMathHelper.clamp_double(player_1_12.velocity.z, -f6, f6);

                    if (player_1_12.velocity.y < -0.15D) {
                        player_1_12.velocity.y = -0.15D;
                    }

                    boolean flag = player_1_12.SNEAK;

                    if (flag && player_1_12.velocity.y < 0.0D) {
                        player_1_12.velocity.y = 0.0D;
                    }
                }

                moveEntity(player_1_12.velocity.x, player_1_12.velocity.y, player_1_12.velocity.z);

                if (player_1_12.isCollidedHorizontally && player_1_12.isOnLadder(blockManager_1_12)) {
                    player_1_12.velocity.y = 0.2D;
                }

                player_1_12.velocity.y -= 0.08D;
                player_1_12.velocity.y *= 0.9800000190734863D;

                player_1_12.velocity.x = player_1_12.velocity.x * mult;
                player_1_12.velocity.z = player_1_12.velocity.z * mult;
            } else {
                double d1 = player_1_12.position.y;
                moveFlying(player_1_12.moveStrafe, player_1_12.moveForward, 0.02F);
                moveEntity(player_1_12.velocity.x, player_1_12.velocity.y, player_1_12.velocity.z);
                player_1_12.velocity.x *= 0.5D;
                player_1_12.velocity.y *= 0.5D;
                player_1_12.velocity.z *= 0.5D;
                player_1_12.velocity.y -= 0.02D;

                if (player_1_12.isCollidedHorizontally
                        && this.isOffsetPositionInLiquid(player_1_12.velocity.x, player_1_12.velocity.y + 0.6000000238418579D - player_1_12.position.y + d1, player_1_12.velocity.z)) {
                    player_1_12.velocity.y = 0.30000001192092896D;
                }
            }
        } else {
            double d0 = player_1_12.position.y;
            float f1 = 0.8F;
            float f2 = 0.02F;
            float f3 = (float) 0; // (float) EnchantmentHelper.getDepthStriderModifier(this);

            if (f3 > 3.0F) {
                f3 = 3.0F;
            }

            if (!player_1_12.GROUND) {
                f3 *= 0.5F;
            }

            float movement;
            if (player_1_12.SPRINT) movement = 0.130000010133F;
            else movement = 0.1F;

            if (f3 > 0.0F) {
                f1 += (0.54600006F - f1) * f3 / 3.0F;
                f2 += (movement * 1.0F - f2) * f3 / 3.0F;
            }

            moveFlying(player_1_12.moveStrafe, player_1_12.moveForward, f2);
            moveEntity(player_1_12.velocity.x, player_1_12.velocity.y, player_1_12.velocity.z);
            player_1_12.velocity.x *= (double) f1;
            player_1_12.velocity.y *= 0.800000011920929D;
            player_1_12.velocity.z *= (double) f1;
            player_1_12.velocity.y -= 0.02D;

            if (player_1_12.isCollidedHorizontally
                    && isOffsetPositionInLiquid(player_1_12.velocity.x, player_1_12.velocity.y + 0.6000000238418579D - player_1_12.position.y + d0, player_1_12.velocity.z)) {
                player_1_12.velocity.y = 0.30000001192092896D;
            }
        }

        player_1_12.jumpMovementFactor = 0.02F;

        if (player_1_12.SPRINT) {
            player_1_12.jumpMovementFactor = (float) ((double) player_1_12.jumpMovementFactor + (double) 0.02F * 0.3D);
        }
        updatePlayerSize();
        tick++;
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    public void moveEntity(double x, double y, double z) {
        // probably only used for sound calculations
        double startX = player_1_12.position.x;
        double startY = player_1_12.position.y;
        double startZ = player_1_12.position.z;

        // apply player movement in web
        if (player_1_12.WEB) {
            player_1_12.WEB = false;
            x *= 0.25D;
            y *= 0.05000000074505806D;
            z *= 0.25D;
            player_1_12.velocity.x = 0.0D;
            player_1_12.velocity.y = 0.0D;
            player_1_12.velocity.z = 0.0D;
        }

        // save x, y, z temporarily
        double xOriginal = x;
        double yOriginal = y;
        double zOriginal = z;

        // do sneak to x, y, z when player sneaks
        boolean GROUND_SNEAK = player_1_12.GROUND && player_1_12.SNEAK;
        if (GROUND_SNEAK) {
            double d6 = 0.05D;

            // if player collided with x: go back by 0.05 until no collision...
            while (x != 0.0D && getCollidingBoundingBoxes(player_1_12.playerBB.offset(x, -1.0D, 0.0D)).isEmpty()) {
                if (x < d6 && x >= -d6) x = 0.0D;
                else if (x > 0.0D)  x -= d6;
                else x += d6;
                xOriginal = x;
            }

            while (z != 0.0D && getCollidingBoundingBoxes(player_1_12.playerBB.offset(0.0D, -1.0D, z)).isEmpty()) {
                if (z < d6 && z >= -d6) z = 0.0D;
                else if (z > 0.0D) z -= d6;
                else z += d6;
                zOriginal = z;
            }

            while (x != 0.0D && z != 0.0D && getCollidingBoundingBoxes(player_1_12.playerBB.offset(x, -1.0D, z)).isEmpty()) {
                if (x < d6 && x >= -d6) x = 0.0D;
                else if (x > 0.0D) x -= d6;
                else x += d6;
                xOriginal = x;

                if (z < d6 && z >= -d6) z = 0.0D;
                else if (z > 0.0D) z -= d6;
                else z += d6;
                zOriginal = z;
            }
        }

        // get all colliding BB from extending current position BB by x, y, z
        // NOTE: it takes all blocks for collision checks
        List<AxisAlignedBB> allBlocks = blockManager_1_12.getAllBlockHitboxes();

        // save playerBB temporarily
        AxisAlignedBB originalBB = player_1_12.playerBB;

        // check offsetY for Y from current position AND apply offsetY to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(player_1_12.playerBB, y);
        player_1_12.playerBB = player_1_12.playerBB.offset(0.0D, y, 0.0D);
        boolean onGround_or_yNegative = player_1_12.GROUND || yOriginal != y && yOriginal < 0.0D;

        // check offsetX for X from current position AND apply offsetX to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) x = axisalignedbb.calculateXOffset(player_1_12.playerBB, x);
        player_1_12.playerBB = player_1_12.playerBB.offset(x, 0.0D, 0.0D);

        // check offsetZ for Z from current position AND apply offsetZ to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) z = axisalignedbb.calculateZOffset(player_1_12.playerBB, z);
        player_1_12.playerBB = player_1_12.playerBB.offset(0.0D, 0.0D, z);

        // if the player is onGround OR falls AND x or z change:
        if (onGround_or_yNegative && (xOriginal != x || zOriginal != z)) {
            // save offsetY, offsetX, offsetZ and updatedBB
            double shiftedX = x;
            double shiftedY = y;
            double shiftedZ = z;
            AxisAlignedBB shiftedBB = player_1_12.playerBB;

            // reset playerBB to start position
            player_1_12.playerBB = originalBB;

            // overwrite y to 0.6 (step height)
            y = 0.6D;

            // get all colliding BB from extending current position BB by x, y=0.6, z
            // NOTE: it takes all blocks for collision checks
            allBlocks = blockManager_1_12.getAllBlockHitboxes();
            AxisAlignedBB axisAlignedBBNoUpdate = player_1_12.playerBB;

            // get currentBB extend it by X and Z
            AxisAlignedBB axisAlignedBBXZUpdate = axisAlignedBBNoUpdate.addCoord(xOriginal, 0.0D, zOriginal);

            // check offsetY for Y=0.6 from xzExtendedBB AND apply offsetY to currentBB
            double yWithStep = y;
            for (AxisAlignedBB axisalignedbb : allBlocks)
                yWithStep = axisalignedbb.calculateYOffset(axisAlignedBBXZUpdate, yWithStep);
            axisAlignedBBNoUpdate = axisAlignedBBNoUpdate.offset(0.0D, yWithStep, 0.0D);

            // check offsetX for X from current position AND apply offsetX to currentBB
            double xWithStep = xOriginal;
            for (AxisAlignedBB axisalignedbb : allBlocks)
                xWithStep = axisalignedbb.calculateXOffset(axisAlignedBBNoUpdate, xWithStep);
            axisAlignedBBNoUpdate = axisAlignedBBNoUpdate.offset(xWithStep, 0.0D, 0.0D);

            // check offsetZ for Z from current position AND apply offsetZ to currentBB
            double zWithStep = zOriginal;
            for (AxisAlignedBB axisalignedbb : allBlocks)
                zWithStep = axisalignedbb.calculateZOffset(axisAlignedBBNoUpdate, zWithStep);
            axisAlignedBBNoUpdate = axisAlignedBBNoUpdate.offset(0.0D, 0.0D, zWithStep);

            // get current playerBB and use for further calc
            AxisAlignedBB currentPlayerBB = player_1_12.playerBB;

            // check offsetY for Y=0.6 from currentBB AND apply offsetY to currentBB
            double yWithStepNoXZShift = y;
            for (AxisAlignedBB axisalignedbb : allBlocks)
                yWithStepNoXZShift = axisalignedbb.calculateYOffset(currentPlayerBB, yWithStepNoXZShift);
            currentPlayerBB = currentPlayerBB.offset(0.0D, yWithStepNoXZShift, 0.0D);

            // check offsetX for X from current position AND apply offsetX to currentBB
            double xWithStepNoXZShift = x;
            for (AxisAlignedBB axisalignedbb : allBlocks)
                xWithStepNoXZShift = axisalignedbb.calculateXOffset(currentPlayerBB, xWithStepNoXZShift);
            currentPlayerBB = currentPlayerBB.offset(xWithStepNoXZShift, 0.0D, 0.0D);

            // check offsetZ for Z from current position AND apply offsetZ to currentBB
            double zWithStepNoXZShift = z;
            for (AxisAlignedBB axisalignedbb : allBlocks)
                zWithStepNoXZShift = axisalignedbb.calculateZOffset(currentPlayerBB, zWithStepNoXZShift);
            currentPlayerBB = currentPlayerBB.offset(zWithStepNoXZShift, 0.0D, 0.0D);

            // take the furthest suggestions and apply to x, -y, z, update currentBB
            double distanceWithStep = xWithStep * xWithStep + zWithStep * zWithStep;
            double distanceWithStepNoShift = xWithStepNoXZShift * xWithStepNoXZShift + zWithStepNoXZShift * zWithStepNoXZShift;

            // shift current BB down by calculated offsetY
            if (distanceWithStep > distanceWithStepNoShift) {
                x = xWithStep;
                z = zWithStep;
                y = -yWithStep;
                player_1_12.playerBB = axisAlignedBBNoUpdate;
            } else {
                x = xWithStepNoXZShift;
                z = zWithStepNoXZShift;
                y = -yWithStepNoXZShift;
                player_1_12.playerBB = currentPlayerBB;
            }

            // take the furthest of currentBB and first calculation and apply to x, y, z and update currentBB
            for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(player_1_12.playerBB, y);
            player_1_12.playerBB = player_1_12.playerBB.offset(0.0D, y, 0.0D);

            if (shiftedX * shiftedX + shiftedZ * shiftedZ >= x * x + z * z) {
                x = shiftedX;
                y = shiftedY;
                z = shiftedZ;
                player_1_12.playerBB = shiftedBB;
            }
        }

        // update player position toBB
        // updatePlayerBB();
        player_1_12.resetPositionToBB();

        // update onGround and isCollided
        player_1_12.isCollidedHorizontally = xOriginal != x || zOriginal != z;
        player_1_12.isCollidedVertically = yOriginal != y;
        player_1_12.GROUND = player_1_12.isCollidedVertically && yOriginal < 0.0D;
        player_1_12.isCollided = player_1_12.isCollidedHorizontally || player_1_12.isCollidedVertically;

        // update block below player
        int i = MinecraftMathHelper.floor_double(player_1_12.position.x);
        int j = MinecraftMathHelper.floor_double(player_1_12.position.y - 0.20000000298023224D);
        int k = MinecraftMathHelper.floor_double(player_1_12.position.z);
        ABlock block = blockManager_1_12.getBlock(i, j, k);

        if (block instanceof Air) {
            ABlock lowerBlock = blockManager_1_12.getBlock(i, j-1, k);
            if (lowerBlock instanceof Fence || lowerBlock instanceof Cobblewall) {
                block = lowerBlock;
            }
        }

        if (!player_1_12.WATER) {
            handleWaterMovement();
        }

        if (!player_1_12.LAVA) {
            handleLavaMovement();
        }

        // if movedX != updatedX -> motionX = 0
        if (xOriginal != x) player_1_12.velocity.x = 0.0D;

        // if movedZ != updatedZ -> motionZ = 0
        if (zOriginal != z) player_1_12.velocity.z = 0.0D;

        // on block landed
        if (yOriginal != y) block.onLanded(player_1_12);

        // do block collisions
        /*if (!GROUND_SNEAK && player.GROUND) {
            block.onEntityCollidedWithBlock(player);
        }*/

        doBlockCollisions();
        player_1_12.setRealVel(new Vec3(x, y, z));
    }

    private void moveFlying(float strafe, float forward, float friction) {
        float speed = strafe * strafe + forward * forward;
        if (speed >= 1.0E-4F) {
            speed = MinecraftMathHelper.sqrt_float(speed);

            if (speed < 1.0F) {
                speed = 1.0F;
            }

            speed = friction / speed;
            strafe = strafe * speed;
            forward = forward * speed;
            float sin = MinecraftMathHelper.sin(player_1_12.YAW * (float) Math.PI / 180.0F);
            float cos = MinecraftMathHelper.cos(player_1_12.YAW * (float) Math.PI / 180.0F);
            player_1_12.velocity.x = player_1_12.velocity.x + strafe * cos - forward * sin;
            player_1_12.velocity.z = player_1_12.velocity.z + forward * cos + strafe * sin;
        }
    }

    private boolean isOffsetPositionInLiquid(double x, double y, double z) {
        AxisAlignedBB axisalignedbb = player_1_12.playerBB.offset(x, y, z);
        return isLiquidPresentInAABB(axisalignedbb);
    }

    public boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
        return getCollidingBoundingBoxes(bb, "Water").isEmpty() && getCollidingBoundingBoxes(bb, "Lava").isEmpty(); // && !isAnyLiquid(bb); can be removed - check in getCollidingBoundingBoxes for water
    }

    private List<BlockLiquid> getCollidingBoundingBoxes(AxisAlignedBB bb, String type) {
        List<ABlock> placedBlocks = getCollidingBoundingBoxes(bb);
        List<BlockLiquid> list = new ArrayList<>();

        for (ABlock aBlock : placedBlocks) {
            if (!(aBlock.getClass().getSimpleName().equals(type))) continue;
            for (AxisVecTuple axisVecTuple : aBlock.axisVecTuples) {
                if (bb.intersectsWith(axisVecTuple.getBb())) {
                    list.add((BlockLiquid) aBlock);
                }
            }
        }

        return list;
    }

    public List<ABlock> getCollidingBoundingBoxes(AxisAlignedBB bb) {
        List<ABlock> placedBlocks = blockManager_1_12.aBlocks;
        List<ABlock> list = new ArrayList<>();
        for (ABlock aBlock : placedBlocks) {
            for (AxisVecTuple axisVecTuple : aBlock.axisVecTuples) {
                if (bb.intersectsWith(axisVecTuple.getBb())) {
                    list.add(aBlock);
                }
            }
        }
        return list;
    }

    private void handleWaterMovement() {
        // this.fire = 0;
        player_1_12.WATER = handleMaterialAcceleration(player_1_12.playerBB.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), "Water");
    }

    private void handleLavaMovement() {
        player_1_12.LAVA = handleMaterialAcceleration(player_1_12.playerBB.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), "Lava");
    }

    private boolean handleMaterialAcceleration(AxisAlignedBB bb, String type) {
        List<BlockLiquid> blocks = getCollidingBoundingBoxes(bb, type);
        return !blocks.isEmpty();
    }

    private void doBlockCollisions() {
        Vec3 blockpos = new Vec3(player_1_12.playerBB.minX + 0.001D, player_1_12.playerBB.minY + 0.001D, player_1_12.playerBB.minZ + 0.001D);
        Vec3 blockpos1 = new Vec3(player_1_12.playerBB.maxX - 0.001D, player_1_12.playerBB.maxY - 0.001D, player_1_12.playerBB.maxZ - 0.001D);

        for (int i = MinecraftMathHelper.floor_double(blockpos.x); i <= MinecraftMathHelper.floor_double(blockpos1.x); ++i) {
            for (int j = MinecraftMathHelper.floor_double(blockpos.y); j <= MinecraftMathHelper.floor_double(blockpos1.y); ++j) {
                for (int k = MinecraftMathHelper.floor_double(blockpos.z); k <= MinecraftMathHelper.floor_double(blockpos1.z); ++k) {
                    Vec3 blockpos2 = new Vec3(i, j, k);
                    ABlock aBlock = blockManager_1_12.getBlock(blockpos2.x, blockpos2.y, blockpos2.z);
                    aBlock.onEntityCollidedWithBlock(player_1_12);
                }
            }
        }
    }

    protected void updatePlayerSize() {
        float width;
        float height;

        if (player_1_12.SNEAK) {
            width = 0.3F;
            height = 1.65F;
        } else {
            width = 0.3F;
            height = 1.8F;
        }

        if (width != player_1_12.width || height != player_1_12.height) {
            AxisAlignedBB axisalignedbb = player_1_12.playerBB;
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);

            if (getCollidingBoundingBoxes(axisalignedbb).isEmpty()) {
                this.setPlayerSize(width, height);
            }
        }
    }

    protected void setPlayerSize(float width, float height) {
        if (width*2.0D != player_1_12.width*2.0D || height != player_1_12.height) {
            float f = player_1_12.width*2.0F;
            player_1_12.width = width;
            player_1_12.height = height;

            if (player_1_12.width*2.0D < f) {
                double d0 = (double) width;
                player_1_12.playerBB = new AxisAlignedBB(player_1_12.position.x - d0, player_1_12.position.y, player_1_12.position.z - d0, player_1_12.position.x + d0, player_1_12.position.y + (double) player_1_12.height, player_1_12.position.z + d0);
                return;
            }

            AxisAlignedBB axisalignedbb = player_1_12.playerBB;
            player_1_12.playerBB = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) player_1_12.width*2.0D, axisalignedbb.minY + (double) player_1_12.height, axisalignedbb.minZ + (double) player_1_12.width*2.0D);

            if (player_1_12.width*2.0D > f) {
                moveEntity((double) (f - player_1_12.width*2.0D), 0.0D, (double) (f - player_1_12.width*2.0D));
            }
        }
    }

}
