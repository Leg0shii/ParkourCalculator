package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CobbleWall extends Block {

    public CobbleWall(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    @Override
    public void addBB() {
        this.axisAlignedBBS.add(new AxisAlignedBB(gridX+0.25, gridY, gridZ+0.25, gridX+0.75, gridY+2.5, gridZ+0.75));
    }

    public static class DrawableCobbleWall extends DrawableBlock {

        public DrawableCobbleWall() {
            super();
            Rectangle rectangle = new Rectangle(30, 30);
            rectangle.setStroke(Color.DARKGRAY);
            rectangle.setFill(Color.LIGHTGRAY);
            getChildren().add(rectangle);

            name = "cobblewall";
            xSize = 0.50;
            ySize = 2.50;
            zSize = 0.50;
        }

    }

}
