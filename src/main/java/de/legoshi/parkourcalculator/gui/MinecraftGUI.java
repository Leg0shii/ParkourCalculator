package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.debug.InformationScreen;
import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.gui.debug.menu.ScreenSettings;
import de.legoshi.parkourcalculator.parkour.environment.BlockFactory;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Air;
import de.legoshi.parkourcalculator.parkour.environment.blocks.StandardBlock;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.util.Vec3;
import de.legoshi.parkourcalculator.util.fxyz.AdvancedCamera;
import de.legoshi.parkourcalculator.util.fxyz.FPSController;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

public class MinecraftGUI extends Observable {

    public static final double BLOCK_OFFSET_X = 0.5;
    public static final double BLOCK_OFFSET_Y = 0.5;
    public static final double BLOCK_OFFSET_Z = 0.5;

    private final BorderPane window;
    private final SubScene subScene;
    private final Group group;

    public final Comparator<Node> depthComparator;

    private ArrayList<Box> previewBlockBoxes = new ArrayList<>();
    private final ArrayList<Observer> observers = new ArrayList<>();

    public MinecraftGUI(Application application, Group group) {
        this.group = group;
        this.group.setDepthTest(DepthTest.ENABLE);

        this.subScene = application.minecraftSubScene;
        this.window = application.window;

        this.subScene.heightProperty().bind(window.heightProperty().subtract(application.menuGUI.heightProperty()).subtract(application.blockGUI.heightProperty()));
        this.subScene.widthProperty().bind(window.widthProperty().subtract(application.inputTickGUI.widthProperty()).subtract(application.coordinateScreen.widthProperty()));

        addObserver(application.environment);
        addObserver(application.coordinateScreen);
        addObserver(application.positionVisualizer);
        addObserver(application.informationScreen);

        application.menuGUI.setMinecraftGUI(this);

        application.minecraftSubScene.setOnMouseMoved(this::handleBackgroundMouseMove);

        addStartingBlock();
        registerCamera();

        this.depthComparator = (node1, node2) -> {
            double z1 = node1.localToSceneTransformProperty().get().getTz();
            double z2 = node2.localToSceneTransformProperty().get().getTz();
            return Double.compare(z2, z1);
        };
    }

    public void addStartingBlock() {
        ABlock aBlock = new StandardBlock(new Vec3(0, 0, 0));
        addBlock(aBlock);
    }

    public void resetScreen() {
        clearScreen();
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box)) return;
        mouseEvent.consume();
        switch (mouseEvent.getButton()) {
            case PRIMARY -> {
                Vec3 newBlockPos = getRoundedCoordinatesFromMouseEvent(mouseEvent);
                if(newBlockPos != null) {
                    newBlockPos.x *= -1; // flipping the x axis ??
                    ABlock curBlock = Environment.getBlock(newBlockPos.x, newBlockPos.y, newBlockPos.z);
                    if (!(curBlock instanceof Air)) return;
                }
                ABlock newBlock = getNewBlockFromPos(mouseEvent);
                if (newBlock != null) addBlock(newBlock);
            }
            case SECONDARY -> {
                ABlock block = getExistingBlockFromPos(mouseEvent);
                if (block != null) removeBlock(block);
            }
        }
    }

    public void handleBackgroundMouseMove(MouseEvent mouseEvent) {
        for(Box box : previewBlockBoxes) {
            group.getChildren().remove(box);
        }
        previewBlockBoxes = new ArrayList<>();
    }

    public void handleBoxMouseMove(MouseEvent mouseEvent) {
        if (!ScreenSettings.isPreviewMode()) return;
        if (!(mouseEvent.getTarget() instanceof Box)) return;
        mouseEvent.consume();
        ABlock newBlock = getNewBlockFromPos(mouseEvent);

        if (newBlock == null) return;
        Color newBlockColor = newBlock.getMaterialColor();
        double PREVIEW_BLOCK_OPACITY = 0.2;
        Color transparentColor = new Color(newBlockColor.getRed(), newBlockColor.getGreen(), newBlockColor.getBlue(), PREVIEW_BLOCK_OPACITY);
        newBlock.setMaterialColor(transparentColor);
        previewBlock(newBlock);
        for (Observer observer : observers) {
            if (observer instanceof InformationScreen) {
                observer.update(null, getFacingAsString(mouseEvent));
            }
        }
    }

    private Vec3 getCoordinatesFromMouseEvent(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box clickedBox)) return null;
        ABlock clickedBlock = getExistingBlockFromPos(mouseEvent);
        if (clickedBlock == null) return null;

        DecimalFormat df = new DecimalFormat("#.#########");
        double clickX = Double.parseDouble(df.format(mouseEvent.getX()).replace(",", "."));
        double clickY = Double.parseDouble(df.format(mouseEvent.getY()).replace(",", "."));
        double clickZ = Double.parseDouble(df.format(mouseEvent.getZ()).replace(",", "."));
        Bounds bounds = clickedBox.getBoundsInLocal();

        Vec3 vec3Float = clickedBlock.getVec3().copy();
        vec3Float.x = -vec3Float.x; // flipping the x axis

        if (bounds.getMinX() == clickX) vec3Float.addVector(1, 0, 0);
        else if (bounds.getMaxX() == clickX) vec3Float.addVector(-1, 0, 0);
        else if (bounds.getMinY() == clickY) vec3Float.addVector(0, 1, 0);
        else if (bounds.getMaxY() == clickY) vec3Float.addVector(0, -1, 0);
        else if (bounds.getMinZ() == clickZ) vec3Float.addVector(0, 0, -1);
        else if (bounds.getMaxZ() == clickZ) vec3Float.addVector(0, 0, 1);
        return vec3Float;
    }

    private String getFacingAsString(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box clickedBox)) return null;
        ABlock clickedBlock = getExistingBlockFromPos(mouseEvent);
        if (clickedBlock == null) return null;

        DecimalFormat df = new DecimalFormat("#.#########");
        double clickX = Double.parseDouble(df.format(mouseEvent.getX()).replace(",", "."));
        double clickY = Double.parseDouble(df.format(mouseEvent.getY()).replace(",", "."));
        double clickZ = Double.parseDouble(df.format(mouseEvent.getZ()).replace(",", "."));
        Bounds bounds = clickedBox.getBoundsInLocal();

        if (bounds.getMinX() == clickX) return "west";
        else if (bounds.getMaxX() == clickX) return "east";
        else if (bounds.getMinY() == clickY) return "top";
        else if (bounds.getMaxY() == clickY) return "bottom";
        else if (bounds.getMinZ() == clickZ) return "south";
        else return "north";
    }

    private Vec3 getRoundedCoordinatesFromMouseEvent(MouseEvent mouseEvent) {
        Vec3 vec3Float = getCoordinatesFromMouseEvent(mouseEvent);
        if (vec3Float == null) return null;
        return new Vec3(Math.floor(vec3Float.x), Math.floor(vec3Float.y), Math.floor(vec3Float.z));
    }

    private ABlock getNewBlockFromPos(MouseEvent mouseEvent) {
        Vec3 vec3Rounded = getRoundedCoordinatesFromMouseEvent(mouseEvent);
        if (vec3Rounded == null) return null;

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
            box.setOnMouseMoved(this::handleBoxMouseMove);
            group.getChildren().add(box);
        }
        notifyObservers(aBlock, "add");
    }

    private void previewBlock(ABlock aBlock) {
        for(Box box : previewBlockBoxes) {
            group.getChildren().remove(box);
        }
        previewBlockBoxes = new ArrayList<>();
        if (aBlock.getBoxesArrayList().size() == 0) return;
        for (Box box : aBlock.getBoxesArrayList()) {
            box.setOnMouseMoved(this::handleBoxMouseMove);
            box.setMouseTransparent(true);
            group.getChildren().add(box);
            previewBlockBoxes.add(box);
        }
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

        camera.setController(controller);
        subScene.setCamera(camera);
        subScene.setFill(Color.LIGHTGRAY);

        controller.setSubScene(subScene);
        controller.affine.setTz(-8);
        controller.affine.setTy(-2);

        // allows button presses
        subScene.setOnMousePressed(e -> {
            subScene.requestFocus();
            e.consume();
        });
    }

    public void clearScreen() {
        group.getChildren().removeIf(node -> !(node instanceof Group)); // remove all blocks, keep path group
        Environment.aBlocks = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void notifyObservers(ABlock aBlock, String value) {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(value);
        arrayList.add(aBlock);
        for (Observer observer : observers) {
            if (observer != null) observer.update(this, arrayList);
        }
    }

}
