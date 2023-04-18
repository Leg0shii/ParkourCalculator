package de.legoshi.parkourcalculator.parkour.simulator;

import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.*;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.MinecraftMathHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.paint.Material;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MovementEngine {

    public Player player;
    public Environment environment;
    public ArrayList<PlayerTickInformation> playerTickInformations;

    public MovementEngine(Environment environment) {
        this.player = new Player(new Vec3(0.5, 1.0, 0.5), new Vec3(0, -0.0784000015258789, 0));
        this.environment = environment;
        this.playerTickInformations = new ArrayList<>();
    }

    public PlayerTickInformation getLastTick(List<InputTick> inputTicks) {
        player.resetPlayer();
        for (InputTick inputTick : inputTicks) calculateTick(inputTick);
        return player.getPlayerTickInformation();
    }

    public ArrayList<PlayerTickInformation> updatePath(List<InputTick> inputTicks) {
        playerTickInformations = new ArrayList<>();
        if (inputTicks.size() == 0) return playerTickInformations;

        player.resetPlayer();
        playerTickInformations.add(player.getPlayerTickInformation());

        for (InputTick inputTick : inputTicks) {
            calculateTick(inputTick);
            playerTickInformations.add(player.getPlayerTickInformation());
        }

        return playerTickInformations;
    }

    public void calculateTick(InputTick inputTick) {
        player.applyInput(inputTick);

        handleWaterMovement();
        handleLavaMovement();

        if (Math.abs(player.velocity.x) < 0.005D) player.velocity.x = 0.0D;
        if (Math.abs(player.velocity.y) < 0.005D) player.velocity.y = 0.0D;
        if (Math.abs(player.velocity.z) < 0.005D) player.velocity.z = 0.0D;

        if (player.JUMP) {
            if (player.WATER || player.LAVA) {
                player.velocity.y += 0.03999999910593033D;
            } else if (player.GROUND) {
                player.velocity.y = 0.42F;
                if (player.SPRINT) {
                    float f = player.YAW * 0.017453292F;
                    player.velocity.x = player.velocity.x - MinecraftMathHelper.sin(f) * 0.2F;
                    player.velocity.z = player.velocity.z + MinecraftMathHelper.cos(f) * 0.2F;
                }
            }
        }

        player.moveStrafe = player.moveStrafe * 0.98F;
        player.moveForward = player.moveForward * 0.98F;

        if (!player.WATER) {
            if (!player.LAVA) {
                if (player.SPRINT && (player.moveForward < 0.8F || player.isCollidedHorizontally)) {
                    player.SPRINT = false;
                }

                ABlock block = Environment.getBlock(
                        MinecraftMathHelper.floor_double(player.position.x),
                        MinecraftMathHelper.floor_double(player.position.y-1),
                        MinecraftMathHelper.floor_double(player.position.z)
                );

                float mult =  0.91F;
                if (player.GROUND) mult = mult * block.slipperiness.value;
                float acceleration = 0.16277136F / (mult * mult * mult);

                float movement;
                if (player.SPRINT) movement = 0.130000010133F;
                else if (player.SNEAK) movement = 0.03F;
                else movement = 0.1F;

                float movementFactor;
                if (player.GROUND) movementFactor = movement * acceleration;
                else movementFactor = player.jumpMovementFactor;

                moveFlying(player.moveStrafe, player.moveForward, movementFactor);
                mult = 0.91F;

                block = Environment.getBlock(
                        MinecraftMathHelper.floor_double(player.position.x),
                        MinecraftMathHelper.floor_double(player.position.y-1),
                        MinecraftMathHelper.floor_double(player.position.z)
                );

                if (player.GROUND) {
                    mult = block.slipperiness.value * 0.91F;
                }

                // calculate ladder
                if (player.isOnLadder()) {
                    float f6 = 0.15F;
                    player.velocity.x = MinecraftMathHelper.clamp_double(player.velocity.x, -f6, f6);
                    player.velocity.z = MinecraftMathHelper.clamp_double(player.velocity.z, -f6, f6);

                    if (player.velocity.y < -0.15D) {
                        player.velocity.y = -0.15D;
                    }

                    boolean flag = player.SNEAK;

                    if (flag && player.velocity.y < 0.0D) {
                        player.velocity.y = 0.0D;
                    }
                }

                moveEntity(player.velocity.x, player.velocity.y, player.velocity.z);

                if (player.isCollidedHorizontally && player.isOnLadder()) {
                    player.velocity.y = 0.2D;
                }

                player.velocity.y -= 0.08D;
                player.velocity.y *= 0.9800000190734863D;

                player.velocity.x = player.velocity.x * mult;
                player.velocity.z = player.velocity.z * mult;
            } else {
                double d1 = player.position.y;
                moveFlying(player.moveStrafe, player.moveForward, 0.02F);
                moveEntity(player.velocity.x, player.velocity.y, player.velocity.z);
                player.velocity.x *= 0.5D;
                player.velocity.y *= 0.5D;
                player.velocity.z *= 0.5D;
                player.velocity.y -= 0.02D;

                if (player.isCollidedHorizontally
                        && this.isOffsetPositionInLiquid(player.velocity.x, player.velocity.y + 0.6000000238418579D - player.position.y + d1, player.velocity.z)) {
                    player.velocity.y = 0.30000001192092896D;
                }
            }
        } else {
            double d0 = player.position.y;
            float f1 = 0.8F;
            float f2 = 0.02F;
            float f3 = (float) 0; // (float) EnchantmentHelper.getDepthStriderModifier(this);

            if (f3 > 3.0F) {
                f3 = 3.0F;
            }

            if (!player.GROUND) {
                f3 *= 0.5F;
            }

            float movement;
            if (player.SPRINT) movement = 0.130000010133F;
            else if (player.SNEAK) movement = 0.03F;
            else movement = 0.1F;

            if (f3 > 0.0F) {
                f1 += (0.54600006F - f1) * f3 / 3.0F;
                f2 += (movement * 1.0F - f2) * f3 / 3.0F;
            }

            moveFlying(player.moveStrafe, player.moveForward, f2);
            moveEntity(player.velocity.x, player.velocity.y, player.velocity.z);
            player.velocity.x *= (double) f1;
            player.velocity.y *= 0.800000011920929D;
            player.velocity.z *= (double) f1;
            player.velocity.y -= 0.02D;

            if (player.isCollidedHorizontally
                    && isOffsetPositionInLiquid(player.velocity.x, player.velocity.y + 0.6000000238418579D - player.position.y + d0, player.velocity.z)) {
                player.velocity.y = 0.30000001192092896D;
            }
        }

        player.jumpMovementFactor = 0.02F;

        if (player.SPRINT) {
            player.jumpMovementFactor = (float) ((double) player.jumpMovementFactor + (double) 0.02F * 0.3D);
        }
    }

    private void moveEntity(double x, double y, double z) {
        // probably only used for sound calculations
        double startX = player.position.x;
        double startY = player.position.y;
        double startZ = player.position.z;

        // apply player movement in web
        if (player.WEB) {
            player.WEB = false;
            x *= 0.25D;
            y *= 0.05000000074505806D;
            z *= 0.25D;
            player.velocity.x = 0.0D;
            player.velocity.y = 0.0D;
            player.velocity.z = 0.0D;
        }

        // save x, y, z temporarily
        double xOriginal = x;
        double yOriginal = y;
        double zOriginal = z;

        // do some things to x, y, z when player sneaks...
        boolean GROUND_SNEAK = player.GROUND && player.SNEAK;
        if (GROUND_SNEAK) {
            // do calculation when player on ground and sneaking

        }

        // get all colliding BB from extending current position BB by x, y, z
        // NOTE: it takes all blocks for collision checks
        List<AxisAlignedBB> allBlocks = environment.getAllBlockHitboxes();

        // save playerBB temporarily
        AxisAlignedBB originalBB = player.playerBB;

        // check offsetY for Y from current position AND apply offsetY to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(player.playerBB, y);
        player.playerBB = player.playerBB.offset(0.0D, y, 0.0D);
        boolean onGround_or_yNegative = player.GROUND || yOriginal != y && yOriginal < 0.0D;

        // check offsetX for X from current position AND apply offsetX to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) x = axisalignedbb.calculateXOffset(player.playerBB, x);
        player.playerBB = player.playerBB.offset(x, 0.0D, 0.0D);

        // check offsetZ for Z from current position AND apply offsetZ to currentBB
        for (AxisAlignedBB axisalignedbb : allBlocks) z = axisalignedbb.calculateZOffset(player.playerBB, z);
        player.playerBB = player.playerBB.offset(0.0D, 0.0D, z);

        // if the player is onGround OR falls AND x or z change:
        if (onGround_or_yNegative && (xOriginal != x || zOriginal != z)) {
            // save offsetY, offsetX, offsetZ and updatedBB
            double shiftedX = x;
            double shiftedY = y;
            double shiftedZ = z;
            AxisAlignedBB shiftedBB = player.playerBB;

            // reset playerBB to start position
            player.playerBB = originalBB;

            // overwrite y to 0.6 (step height)
            y = 0.6D;

            // get all colliding BB from extending current position BB by x, y=0.6, z
            // NOTE: it takes all blocks for collision checks
            allBlocks = environment.getAllBlockHitboxes();
            AxisAlignedBB axisAlignedBBNoUpdate = player.playerBB;

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
            AxisAlignedBB currentPlayerBB = player.playerBB;

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
                player.playerBB = axisAlignedBBNoUpdate;
            } else {
                x = xWithStepNoXZShift;
                z = zWithStepNoXZShift;
                y = -yWithStepNoXZShift;
                player.playerBB = currentPlayerBB;
            }

            // take the furthest of currentBB and first calculation and apply to x, y, z and update currentBB
            for (AxisAlignedBB axisalignedbb : allBlocks) y = axisalignedbb.calculateYOffset(player.playerBB, y);
            player.playerBB = player.playerBB.offset(0.0D, y, 0.0D);

            if (shiftedX * shiftedX + shiftedZ * shiftedZ >= x * x + z * z) {
                x = shiftedX;
                y = shiftedY;
                z = shiftedZ;
                player.playerBB = shiftedBB;
            }
        }

        // update player position toBB
        // updatePlayerBB();
        player.resetPositionToBB();

        // update onGround and isCollided
        player.isCollidedHorizontally = xOriginal != x || zOriginal != z;
        player.isCollidedVertically = yOriginal != y;
        player.GROUND = player.isCollidedVertically && yOriginal < 0.0D;
        player.isCollided = player.isCollidedHorizontally || player.isCollidedVertically;

        // update block below player
        int i = MinecraftMathHelper.floor_double(player.position.x);
        int j = MinecraftMathHelper.floor_double(player.position.y - 0.20000000298023224D);
        int k = MinecraftMathHelper.floor_double(player.position.z);
        ABlock block = Environment.getBlock(i, j, k);

        if (block instanceof Air) {
            ABlock lowerBlock = Environment.getBlock(i, j-1, k);
            if (lowerBlock instanceof Fence || lowerBlock instanceof Cobblewall) {
                block = lowerBlock;
            }
        }

        if (!player.WATER) {
            handleWaterMovement();
        }

        if (!player.LAVA) {
            handleLavaMovement();
        }

        // if movedX != updatedX -> motionX = 0
        if (xOriginal != x) player.velocity.x = 0.0D;

        // if movedZ != updatedZ -> motionZ = 0
        if (zOriginal != z) player.velocity.z = 0.0D;

        // on block landed
        if (yOriginal != y) block.onLanded(player);

        // do block collisions
        this.doBlockCollisions();
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
            float sin = MinecraftMathHelper.sin(player.YAW * (float) Math.PI / 180.0F);
            float cos = MinecraftMathHelper.cos(player.YAW * (float) Math.PI / 180.0F);
            player.velocity.x = player.velocity.x + strafe * cos - forward * sin;
            player.velocity.z = player.velocity.z + forward * cos + strafe * sin;
        }
    }

    public PlayerTickInformation getLandTick() {
        PlayerTickInformation playerTickInformation = null;
        PlayerTickInformation prevTick = null;
        for (PlayerTickInformation pti : getPlayerTickInformations()) {
            if (pti.isGround() && prevTick != null && !prevTick.isGround()) playerTickInformation = prevTick;
            prevTick = pti;
        }
        return playerTickInformation;
    }

    public PlayerTickInformation getJumpTick() {
        PlayerTickInformation playerTickInformation = null;
        for (PlayerTickInformation pti : getPlayerTickInformations()) {
            if (pti.isJump()) playerTickInformation = pti;
        }
        return playerTickInformation;
    }

    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        AxisAlignedBB axisalignedbb = player.playerBB.offset(x, y, z);
        return isLiquidPresentInAABB(axisalignedbb);
    }

    private boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
        return getCollidingBoundingBoxes(bb, "Water").isEmpty() && getCollidingBoundingBoxes(bb, "Lava").isEmpty(); // && !isAnyLiquid(bb); can be removed - check in getCollidingBoundingBoxes for water
    }

    public List<BlockLiquid> getCollidingBoundingBoxes(AxisAlignedBB bb, String type) {
        List<ABlock> placedBlocks = Environment.aBlocks;
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

    public boolean handleWaterMovement() {
        if (handleMaterialAcceleration(player.playerBB.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), "Water")) {
            player.WATER = true;
            // this.fire = 0;
        } else {
            player.WATER = false;
        }
        return player.WATER;
    }

    public boolean handleLavaMovement() {
        if (handleMaterialAcceleration(player.playerBB.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), "Lava")) {
            player.LAVA = true;
        } else {
            player.LAVA = false;
        }
        return player.LAVA;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB bb, String type) {
        List<BlockLiquid> blocks = getCollidingBoundingBoxes(bb, type);
        return !blocks.isEmpty();
    }

    private void doBlockCollisions() {
        Vec3 blockpos = new Vec3(player.playerBB.minX + 0.001D, player.playerBB.minY + 0.001D, player.playerBB.minZ + 0.001D);
        Vec3 blockpos1 = new Vec3(player.playerBB.maxX - 0.001D, player.playerBB.maxY - 0.001D, player.playerBB.maxZ - 0.001D);

        for (int i = MinecraftMathHelper.floor_double(blockpos.x); i <= MinecraftMathHelper.floor_double(blockpos1.x); ++i) {
            for (int j = MinecraftMathHelper.floor_double(blockpos.y); j <= MinecraftMathHelper.floor_double(blockpos1.y); ++j) {
                for (int k = MinecraftMathHelper.floor_double(blockpos.z); k <= MinecraftMathHelper.floor_double(blockpos1.z); ++k) {
                    Vec3 blockpos2 = new Vec3(i, j, k);
                    ABlock aBlock = Environment.getBlock(blockpos2.x, blockpos2.y, blockpos2.z);
                    aBlock.onEntityCollidedWithBlock(player);
                }
            }
        }
    }

}