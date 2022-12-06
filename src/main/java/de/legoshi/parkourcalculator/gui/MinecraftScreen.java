package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.environment.blocks.StandardBlock;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MinecraftScreen extends Observable {

    public static final double BLOCK_OFFSET_X = 0.5;
    public static final double BLOCK_OFFSET_Y = 0.5;
    public static final double BLOCK_OFFSET_Z = 0.5;

    private final Group group;
    private final Scene scene;
    private final SubScene subScene;
    private final Environment environment;

    private ArrayList<Observer> observers = new ArrayList<>();

    public MinecraftScreen(Group group, Scene scene, SubScene subScene, Environment environment) {
        this.group = group;
        this.scene = scene;
        // this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));

        this.subScene = subScene;
        this.environment = environment;

        setupModelScreen();
    }

    public void setupModelScreen() {
        registerCamera();
        registerKeyInputs();
    }

    public void addStartingBlock() {
        ABlock aBlock = new StandardBlock(new Vec3(0, 0, 0));
        addBlock(aBlock);
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box)) return;
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

        Vec3 vec3 = clickedBlock.getVec3().copy();
        if (bounds.getMinX() == clickX) vec3.addVector(-1, 0, 0);
        else if (bounds.getMaxX() == clickX) vec3.addVector(1, 0, 0);
        else if (bounds.getMinY() == clickY) vec3.addVector(0, 1, 0);
        else if (bounds.getMaxY() == clickY) vec3.addVector(0, -1, 0);
        else if (bounds.getMinZ() == clickZ) vec3.addVector(0, 0, -1);
        else if (bounds.getMaxZ() == clickZ) vec3.addVector(0, 0, 1);
        return new StandardBlock(vec3);
    }

    private ABlock getExistingBlockFromPos(MouseEvent mouseEvent) {
        for (ABlock aBlock : environment.aBlocks) {
            Box box = (Box) mouseEvent.getTarget();
            if (aBlock.getBoxesArrayList().contains(box)) {
                return aBlock;
            }
        }
        return null;
    }

    public void addBlock(ABlock aBlock) {
        for(Box box : aBlock.getBoxesArrayList()) {
            box.setOnMouseClicked(this::handleMouseClick);
            group.getChildren().add(box);
        }
        notifyObservers(aBlock, "add");
    }

    public void removeBlock(ABlock aBlock) {
        for(Box box : aBlock.getBoxesArrayList()) {
            group.getChildren().remove(box);
        }
        notifyObservers(aBlock, "remove");
    }

    private void registerCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.translateXProperty().set(10);
        camera.translateYProperty().set(-2);
        camera.translateZProperty().set(0);
        camera.setRotationAxis(Rotate.Y_AXIS);
        camera.setRotate(270);
        camera.setNearClip(0.01);
        camera.setFarClip(100);
        subScene.setCamera(camera);
    }

    private void registerKeyInputs() {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W -> scaleGroup(1.02);
                case S -> scaleGroup(0.98);
                case A -> subScene.getCamera().setTranslateZ(subScene.getCamera().getTranslateZ()+0.1);
                case D -> subScene.getCamera().setTranslateZ(subScene.getCamera().getTranslateZ()-0.1);
                case CAPS -> subScene.getCamera().setTranslateY(subScene.getCamera().getTranslateY()-0.1);
                case SHIFT -> subScene.getCamera().setTranslateY(subScene.getCamera().getTranslateY()+0.1);
                case PLUS -> rotateGroup(-1);
                case MINUS -> rotateGroup(1);
            }
        });
    }

    private void rotateGroup(int value) {
        group.setRotationAxis(Rotate.Y_AXIS);
        group.setRotate(group.getRotate()+value);
    }

    private void scaleGroup(double factor) {
        group.setScaleX(group.getScaleX()*factor);
        group.setScaleY(group.getScaleY()*factor);
        group.setScaleZ(group.getScaleZ()*factor);
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
