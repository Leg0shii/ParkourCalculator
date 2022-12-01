package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.Controller;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.Movement;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public abstract class Block extends StackPane {

    public ArrayList<AxisAlignedBB> axisAlignedBBS;
    public Movement.Slipperiness slipperiness;
    public int gridX;
    public int gridY;
    public int gridZ;

    public Block(int gridX, int gridY, int gridZ) {
        this.axisAlignedBBS = new ArrayList<>();
        this.slipperiness = Movement.Slipperiness.BLOCK;
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
        addBB();
    }

    public abstract void addBB();

    public abstract static class DrawableBlock extends StackPane {

        public String name;

        public double xSize = 1.0;
        public double ySize = 1.0;
        public double zSize = 1.0;

        public DrawableBlock() {
            setOnMouseClicked(mouseEvent -> Controller.selectedBlock = this);
        }

    }

}
