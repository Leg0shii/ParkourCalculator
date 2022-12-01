package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Fence extends Block {

    public Fence(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    @Override
    public void addBB() {
        this.axisAlignedBBS.add(new AxisAlignedBB(gridX, gridY, gridZ, gridX+0.25, gridY+1.5, gridZ+0.25));
    }

    public static class DrawableFence extends DrawableBlock {

        public DrawableFence() {
            super();
            Rectangle rectangle = new Rectangle(18, 18);
            rectangle.setStroke(Color.BROWN);
            rectangle.setFill(Color.YELLOW);
            getChildren().add(rectangle);

            name = "fence";
            xSize = 0.25;
            ySize = 1.50;
            zSize = 0.25;
        }

    }

}
