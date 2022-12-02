package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import javafx.scene.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class MinecraftScreen {

    private final PositionVisualizer positionVisualizer;
    private static Box firstBlock;

    public MinecraftScreen(Group group, Scene scene, SubScene subScene, PositionVisualizer positionVisualizer) {
        this.positionVisualizer = positionVisualizer;
        setupModelScreen(group, scene, subScene);
    }

    public void setupModelScreen(Group root, Scene scene, SubScene subScene) {
        createStartPlatform(root);
        registerCamera(subScene);
        registerKeyInputs(scene, subScene, root);
        // prepareLightSource(root);
    }

    private void createStartPlatform(Group root) {
        Box box = new Box(100, 100, 100);
        box.setOnMouseClicked(mouseEvent -> handleBlockClick(root, mouseEvent));
        root.getChildren().add(box);

        firstBlock = box;
    }

    private void handleBlockClick(Group root, MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            addBlock(root, mouseEvent);
        } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
            removeBlock(root, mouseEvent);
        }
        positionVisualizer.generatePlayerPath();
    }

    private void removeBlock(Group root, MouseEvent mouseEvent) {
        Box bOld = (Box) mouseEvent.getTarget();
        if (bOld.equals(firstBlock)) return;
        root.getChildren().remove(bOld);
        Environment.removeBlock(bOld, firstBlock);
    }

    private void addBlock(Group root, MouseEvent mouseEvent) {
        Box bAdded = new Box(100, 100, 100);
        Box bOld = (Box) mouseEvent.getTarget();
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.GREEN);
        bAdded.setMaterial(material);
        double roundedX = Math.round(mouseEvent.getX() * 100.0) / 100.0;
        double roundedY = Math.round(mouseEvent.getY() * 100.0) / 100.0;
        double roundedZ = Math.round(mouseEvent.getZ() * 100.0) / 100.0;
        if (roundedX == 50.0) bAdded.setTranslateX(100);
        else if (roundedX == -50.0) bAdded.setTranslateX(-100);
        else if (roundedY == 50.0) bAdded.setTranslateY(100);
        else if (roundedY == -50.0) bAdded.setTranslateY(-100);
        else if (roundedZ == 50.0) bAdded.setTranslateZ(100);
        else if (roundedZ == -50.0) bAdded.setTranslateZ(-100);
        bAdded.setTranslateX(bAdded.getTranslateX() + bOld.getTranslateX());
        bAdded.setTranslateY(bAdded.getTranslateY() + bOld.getTranslateY());
        bAdded.setTranslateZ(bAdded.getTranslateZ() + bOld.getTranslateZ());
        bAdded.setOnMouseClicked(mouseEvent1 -> handleBlockClick(root, mouseEvent1));
        root.getChildren().add(bAdded);

        Environment.addBlock(bAdded, firstBlock);
    }

    private void registerCamera(SubScene subScene) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.translateXProperty().set(0);
        camera.translateYProperty().set(0);
        camera.translateZProperty().set(-1000);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        subScene.setCamera(camera);
    }

    private void registerKeyInputs(Scene scene, SubScene subScene, Group group) {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W -> scaleGroup(group, 1.2);
                case S -> scaleGroup(group, 0.8);
                case A -> subScene.getCamera().setTranslateX(subScene.getCamera().getTranslateX()-10);
                case D -> subScene.getCamera().setTranslateX(subScene.getCamera().getTranslateX()+10);
                case CAPS -> subScene.getCamera().setTranslateY(subScene.getCamera().getTranslateY()-10);
                case SHIFT -> subScene.getCamera().setTranslateY(subScene.getCamera().getTranslateY()+10);
                case PLUS -> rotateGroup(group, -10);
                case MINUS -> rotateGroup(group, 10);
            }
        });
    }

    private void rotateGroup(Group group, int value) {
        group.setRotationAxis(Rotate.Y_AXIS);
        group.setRotate(group.getRotate()+value);
    }

    private void scaleGroup(Group group, double factor) {
        group.setScaleX(group.getScaleX()*factor);
        group.setScaleY(group.getScaleY()*factor);
        group.setScaleZ(group.getScaleZ()*factor);
    }

}
