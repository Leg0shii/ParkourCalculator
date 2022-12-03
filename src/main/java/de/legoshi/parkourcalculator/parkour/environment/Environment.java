package de.legoshi.parkourcalculator.parkour.environment;

import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Environment implements Observer {

    public ArrayList<ABlock> aBlocks = new ArrayList<>();

    public Environment() {

    }

    public void addBlock(ABlock block) {
        aBlocks.add(block);
    }

    public void removeBlock(ABlock block) {
        aBlocks.remove(block);
    }

    public ArrayList<AxisAlignedBB> getAllBBs() {
        ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>();
        for (ABlock aBlock : aBlocks) {
            boundingBoxes.addAll(aBlock.getAxisAlignedBBS());
        }
        return boundingBoxes;
    }

    @Override
    public void update(Observable o, Object arg) {
        ArrayList<Object> objects = (ArrayList<Object>) arg;
        if (objects.get(0).equals("add")) addBlock((ABlock) objects.get(1));
        else removeBlock((ABlock) objects.get(1));
    }
}
