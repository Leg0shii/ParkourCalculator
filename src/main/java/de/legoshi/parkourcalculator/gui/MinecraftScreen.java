package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.environment.blocks.StandardBlock;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MinecraftScreen extends Observable {

    private final Group group;
    private final Scene scene;
    private final SubScene subScene;

    private ArrayList<Observer> observers = new ArrayList<>();

    public MinecraftScreen(Group group, Scene scene, SubScene subScene) {
        this.group = group;
        this.scene = scene;
        this.subScene = subScene;

        setupModelScreen();
    }

    public void setupModelScreen() {
        registerCamera();
        registerKeyInputs();
    }

    public void addStartingBlock() {
        ABlock aBlock = new StandardBlock(new Vec3(0.0, 0.0, 0.0));
        addBlock(aBlock);
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        System.out.println("CLICKED!");
        switch (mouseEvent.getButton()) {
            case PRIMARY -> {
                ABlock block = getNewBlockFromPos(mouseEvent);
                // addBlock(block);
            }
            case SECONDARY -> {
                ABlock block = getExistingBlockFromPos(mouseEvent);
                // removeBlock(block);
            }
        }
    }

    private ABlock getNewBlockFromPos(MouseEvent mouseEvent) {
        return null;
    }

    private ABlock getExistingBlockFromPos(MouseEvent mouseEvent) {
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
        camera.translateXProperty().set(0);
        camera.translateYProperty().set(-2);
        camera.translateZProperty().set(-10);
        camera.setNearClip(0.01);
        camera.setFarClip(100);
        subScene.setCamera(camera);
    }

    private void registerKeyInputs() {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W -> scaleGroup(1.02);
                case S -> scaleGroup(0.98);
                case A -> subScene.getCamera().setTranslateX(subScene.getCamera().getTranslateX()-0.1);
                case D -> subScene.getCamera().setTranslateX(subScene.getCamera().getTranslateX()+0.1);
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
