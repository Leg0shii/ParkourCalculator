package de.legoshi.parkourcalculator.simulation.environment.block;

public class Air extends ABlock {

    private static final Air instance = new Air();

    private Air() {

    }

    public static Air getInstance() {
        return instance;
    }

    @Override
    public void updateBoundingBox() {

    }

    @Override
    public void updateImage() {

    }

}
