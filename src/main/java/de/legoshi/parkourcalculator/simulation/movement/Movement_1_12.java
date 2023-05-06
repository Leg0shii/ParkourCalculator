package de.legoshi.parkourcalculator.simulation.movement;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;

public class Movement_1_12 extends Movement {

    public Movement_1_12(Player player, BlockManager blockManager) {
        super(player, blockManager);
    }

    public void calculateTick(InputTick inputTick) {
        player.applyInput(inputTick);

        handleWaterMovement();
        handleLavaMovement();

        if (Math.abs(player.velocity.x) < 0.003D) player.velocity.x = 0.0D;
        if (Math.abs(player.velocity.y) < 0.003D) player.velocity.y = 0.0D;
        if (Math.abs(player.velocity.z) < 0.003D) player.velocity.z = 0.0D;

        super.calculateTick(inputTick);

        updatePlayerSize();
    }

    protected void updatePlayerSize() {
        float width;
        float height;

        if (player.SNEAK) {
            width = 0.3F;
            height = 1.65F;
        } else {
            width = 0.3F;
            height = 1.8F;
        }

        if (width != player.width || height != player.height) {
            AxisAlignedBB axisalignedbb = player.playerBB;
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);

            if (getCollidingBoundingBoxes(axisalignedbb).isEmpty()) {
                this.setPlayerSize(width, height);
            }
        }
    }

    protected void setPlayerSize(float width, float height) {
        if (width*2.0D != player.width*2.0D || height != player.height) {
            float f = player.width*2.0F;
            player.width = width;
            player.height = height;

            if (player.width*2.0D < f) {
                double d0 = (double) width;
                player.playerBB = new AxisAlignedBB(player.position.x - d0, player.position.y, player.position.z - d0, player.position.x + d0, player.position.y + (double) player.height, player.position.z + d0);
                return;
            }

            AxisAlignedBB axisalignedbb = player.playerBB;
            player.playerBB = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) player.width*2.0D, axisalignedbb.minY + (double) player.height, axisalignedbb.minZ + (double) player.width*2.0D);

            if (player.width*2.0D > f) {
                moveEntity((double) (f - player.width*2.0D), 0.0D, (double) (f - player.width*2.0D));
            }
        }
    }

}
