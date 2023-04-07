package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.environment.BlockFactory;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.environment.blocks.StandardBlock;
import de.legoshi.parkourcalculator.util.Vec3;
import de.legoshi.parkourcalculator.util.fxyz.AdvancedCamera;
import de.legoshi.parkourcalculator.util.fxyz.FPSController;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MinecraftScreen extends Observable {

    public static final double BLOCK_OFFSET_X = 0.5;
    public static final double BLOCK_OFFSET_Y = 0.5;
    public static final double BLOCK_OFFSET_Z = 0.5;

    private final Group group;
    private final SubScene subScene;

    private final ArrayList<Observer> observers = new ArrayList<>();

    public MinecraftScreen(Group group, SubScene subScene) {
        this.group = group;
        this.subScene = subScene;

        registerCamera();
    }

    public void addStartingBlock() {
        ABlock aBlock = new StandardBlock(new Vec3(0, 0, 0));
        addBlock(aBlock);
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box)) return;
        mouseEvent.consume();
        switch (mouseEvent.getButton()) {
            case PRIMARY -> {
                ABlock block = getNewBlockFromPos(mouseEvent);
                if (block != null) addBlock(block);
            }
            case SECONDARY -> {
                ABlock block = getExistingBlockFromPos(mouseEvent);
                if (block != null) removeBlock(block);
            }
        }
    }

    private ABlock getNewBlockFromPos(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box clickedBox)) return null;
        ABlock clickedBlock = getExistingBlockFromPos(mouseEvent);
        if (clickedBlock == null) return null;

        double clickX = mouseEvent.getX();
        double clickY = mouseEvent.getY();
        double clickZ = mouseEvent.getZ();
        Bounds bounds = clickedBox.getBoundsInLocal();

        Vec3 vec3Float = clickedBlock.getVec3().copy();
        Vec3 vec3Rounded = new Vec3(Math.floor(vec3Float.x), Math.floor(vec3Float.y), Math.floor(vec3Float.z));
        if (bounds.getMinX() == clickX) vec3Rounded.addVector(-1, 0, 0);
        else if (bounds.getMaxX() == clickX) vec3Rounded.addVector(1, 0, 0);
        else if (bounds.getMinY() == clickY) vec3Rounded.addVector(0, 1, 0);
        else if (bounds.getMaxY() == clickY) vec3Rounded.addVector(0, -1, 0);
        else if (bounds.getMinZ() == clickZ) vec3Rounded.addVector(0, 0, -1);
        else if (bounds.getMaxZ() == clickZ) vec3Rounded.addVector(0, 0, 1);
        return BlockFactory.createBlock(vec3Rounded, Environment.currentBlock.getClass().getSimpleName());
    }

    private ABlock getExistingBlockFromPos(MouseEvent mouseEvent) {
        for (ABlock aBlock : Environment.aBlocks) {
            Box box = (Box) mouseEvent.getTarget();
            if (aBlock.getBoxesArrayList().contains(box)) {
                return aBlock;
            }
        }
        return null;
    }

    public void addBlock(ABlock aBlock) {
        if (aBlock.getBoxesArrayList().size() == 0) {
            System.out.println("No block to add... Set a connection?");
            return;
        }
        for (Box box : aBlock.getBoxesArrayList()) {
            box.setOnMouseClicked(this::handleMouseClick);
            group.getChildren().add(box);
        }
        notifyObservers(aBlock, "add");
    }

    public void removeBlock(ABlock aBlock) {
        for (Box box : aBlock.getBoxesArrayList()) {
            group.getChildren().remove(box);
        }
        notifyObservers(aBlock, "remove");
    }

    private void registerCamera() {
        AdvancedCamera camera = new AdvancedCamera();
        FPSController controller = new FPSController();

        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setFieldOfView(42);

        camera.setController(controller);
        subScene.setCamera(camera);
        subScene.setFill(Color.LIGHTBLUE);

        controller.setSubScene(subScene);
        controller.affine.setTz(-8);
        controller.affine.setTy(-2);

        // allows button presses
        subScene.setOnMousePressed(e -> {
            subScene.requestFocus();
            e.consume();
        });
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void notifyObservers(ABlock aBlock, String value) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(value);
        arrayList.add(aBlock);
        for (Observer observer : observers) {
            observer.update(this, arrayList);
        }
    }

}
