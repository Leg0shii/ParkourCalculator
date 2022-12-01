package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FullBlock extends Block {

    public FullBlock(int gridX, int gridY, int gridZ) {
        super(gridX, gridY, gridZ);
    }

    @Override
    public void addBB() {
        this.axisAlignedBBS.add(new AxisAlignedBB(gridX, gridY, gridZ, gridX+1, gridY+1, gridZ+1));
    }

    public static class DrawableFullBlock extends DrawableBlock {

        public DrawableFullBlock() {
            super();
            this.xSize = 100;
            this.ySize = 100;
            this.zSize = 100;
            Rectangle rectangle = new Rectangle(50, 50);
            rectangle.setStroke(Color.DARKGREEN);
            rectangle.setFill(Color.LIGHTGREEN);
            getChildren().add(rectangle);

            name = "fullblock";
        }

    }

}
