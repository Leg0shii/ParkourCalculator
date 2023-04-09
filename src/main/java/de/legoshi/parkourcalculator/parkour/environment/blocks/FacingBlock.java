package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.gui.ConnectionGUI;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public abstract class FacingBlock extends ABlock {

    // north, east, south, west
    protected AxisVecTuple north;
    protected AxisVecTuple east;
    protected AxisVecTuple south;
    protected AxisVecTuple west;

    protected AxisVecTuple base;
    protected AxisVecTuple baseFlip;

    public FacingBlock(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        calcBase();
        calcBaseFlip();

        calcNorth();
        calcEast();
        calcSouth();
        calcWest();

        if (ConnectionGUI.isFlip() && baseFlip != null) this.axisVecTuples.add(baseFlip);
        else if (!ConnectionGUI.isFlip() && base != null) this.axisVecTuples.add(base);

        if (ConnectionGUI.isNorth()) this.axisVecTuples.add(north);
        if (ConnectionGUI.isEast()) this.axisVecTuples.add(east);
        if (ConnectionGUI.isSouth()) this.axisVecTuples.add(south);
        if (ConnectionGUI.isWest()) this.axisVecTuples.add(west);
    }

    protected abstract void  calcBase();

    protected abstract void calcBaseFlip();
    protected abstract void calcNorth();
    protected abstract void calcEast();
    protected abstract void calcSouth();
    protected abstract void  calcWest();

}
