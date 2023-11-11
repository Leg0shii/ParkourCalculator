package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.debug.InformationScreen;
import de.legoshi.parkourcalculator.gui.debug.menu.ScreenSettings;
import de.legoshi.parkourcalculator.gui.menu.ConfigProperties;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.BlockFactory;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.Facing;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.block.StandardBlock;
import de.legoshi.parkourcalculator.util.NumberHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import de.legoshi.parkourcalculator.util.fxyz.AdvancedCamera;
import de.legoshi.parkourcalculator.util.fxyz.FPSController;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import lombok.Getter;

import java.util.*;

public class MinecraftGUI extends Observable implements VersionDependent {

    public static final double BLOCK_OFFSET_X = 0.5;
    public static final double BLOCK_OFFSET_Y = 0.5;
    public static final double BLOCK_OFFSET_Z = 0.5;

    public static final Vec3 BLOCK_POSITION = new Vec3(1, 0, 0);

    private final ConfigProperties configProperties;
    private final Application application;
    private BlockManager blockManager;

    private final SubScene subScene;
    private final Group group;

    @Getter private FPSController controller;

    private List<Box> previewBlockBoxes = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();

    private MouseButton addBlock, destroyBlock;

    private boolean startBlockFlag = false;
    private boolean endBlockFlag = false;
    private boolean pathBlockFlag = false;

    public MinecraftGUI(Application application, Group group) {
        this.application = application;
        this.configProperties = application.configGUI.getConfigProperties();
        this.blockManager = application.currentParkour.getBlockManager();

        this.group = group;
        this.group.setDepthTest(DepthTest.ENABLE);

        this.subScene = application.minecraftSubScene;
        BorderPane window = application.window;

        this.subScene.heightProperty().bind(window.heightProperty().subtract(application.menuGUI.heightProperty()).subtract(application.blockGUI.heightProperty()));
        this.subScene.widthProperty().bind(window.widthProperty().subtract(application.inputTickGUI.widthProperty()).subtract(application.coordinateScreen.widthProperty()));

        apply(application.currentParkour);
        addObserver(application.coordinateScreen);
        addObserver(application.positionVisualizer);
        addObserver(application.informationScreen);

        application.menuGUI.setMinecraftGUI(this);
        application.minecraftSubScene.setOnMouseMoved(this::handleBackgroundMouseMove);

        registerCamera();
    }

    public void updateConfigValues(ConfigProperties configProperties) {
        addBlock = MouseButton.valueOf(configProperties.getPlaceBlock());
        destroyBlock = MouseButton.valueOf(configProperties.getDestroyBlock());
    }

    @Override
    public void apply(Parkour parkour) {
        group.getChildren().removeIf(node -> !(node instanceof Group));

        observers.remove(blockManager);
        this.observers.add(0, parkour.getBlockManager());

        this.blockManager = parkour.getBlockManager();
        this.previewBlockBoxes = new ArrayList<>();

        if (blockManager.aBlocks.isEmpty()) {
            addStartingBlock();
            return;
        }

        List<ABlock> aBlocksCopy = new ArrayList<>(blockManager.getAllBlocks());
        aBlocksCopy.forEach(this::addBlock);
        // TODO: blockManager.aBlocks = aBlocksCopy; ???
    }

    public void addStartingBlock() {
        addBlock(new StandardBlock(BLOCK_POSITION.copy()));
    }

    public void resetScreen() {
        clearScreen();
        addStartingBlock();
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box)) return;
        mouseEvent.consume();
        if (mouseEvent.getButton().equals(addBlock)) {

            ABlock clickedBlock = getExistingBlockFromPos(mouseEvent);
            if (handleBruteforceClick(clickedBlock)) return;

            Vec3 newBlockPos = getRoundedCoordinatesFromMouseEvent(mouseEvent);
            if(newBlockPos != null) {
                newBlockPos.x *= -1; // flipping the x axis
                ABlock curBlock = blockManager.getBlock((int) newBlockPos.x, (int) newBlockPos.y, (int) newBlockPos.z);
                if (!(curBlock instanceof Air)) return;
            }
            ABlock newBlock = getNewBlockFromPos(mouseEvent);
            if (newBlock != null) addBlock(newBlock);
        } else if(mouseEvent.getButton().equals(destroyBlock)) {
            ABlock block = getExistingBlockFromPos(mouseEvent);
            if (block != null) removeBlock(block);
        }
    }

    public boolean handleBruteforceClick(ABlock clickedBlock) {
        if (endBlockFlag) {
            this.endBlockFlag = false;
            application.menuScreen.bruteforceSettings.setEndBlock(clickedBlock);
            return true;
        }
        if (startBlockFlag) {
            this.startBlockFlag = false;
            application.menuScreen.bruteforceSettings.setStartBlock(clickedBlock);
            return true;
        }
        if (pathBlockFlag) {
            this.pathBlockFlag = false;
            application.menuScreen.bruteforceSettings.setPathBlock(clickedBlock);
            return true;
        }
        return false;
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
        if (endBlockFlag) return;
        if (startBlockFlag) return;

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
                Vec3 newBlockPos = getExistingBlockFromPos(mouseEvent).getVec3();
                observer.update(null, "block-info;"+getFacingAsString(mouseEvent) + ";" + newBlockPos.x * (-1) + ";" + newBlockPos.y + ";" + newBlockPos.z);
            }
        }
    }

    private Vec3 getCoordinatesFromMouseEvent(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box)) return null;
        ABlock clickedBlock = getExistingBlockFromPos(mouseEvent);
        if (clickedBlock == null) return null;

        Vec3 vec3Float = clickedBlock.getVec3().copy();
        vec3Float.x = -vec3Float.x;

        Facing facing = getFacingAsString(mouseEvent);
        if (facing == Facing.WEST) vec3Float.addVector(1, 0, 0);
        else if (facing == Facing.EAST) vec3Float.addVector(-1, 0, 0);
        else if (facing == Facing.TOP) vec3Float.addVector(0, 1, 0);
        else if (facing == Facing.BOTTOM) vec3Float.addVector(0, -1, 0);
        else if (facing == Facing.SOUTH) vec3Float.addVector(0, 0, -1);
        else if (facing == Facing.NORTH) vec3Float.addVector(0, 0, 1);
        return vec3Float;
    }

    private Facing getFacingAsString(MouseEvent mouseEvent) {
        if (!(mouseEvent.getTarget() instanceof Box clickedBox)) return null;
        ABlock clickedBlock = getExistingBlockFromPos(mouseEvent);
        if (clickedBlock == null) return null;

        int p = 7;
        double clickX = NumberHelper.roundDouble(mouseEvent.getX(), p);
        double clickY = NumberHelper.roundDouble(mouseEvent.getY(), p);
        double clickZ = NumberHelper.roundDouble(mouseEvent.getZ(), p);

        Bounds bounds = clickedBox.getBoundsInLocal();
        double maxX = NumberHelper.roundDouble(bounds.getMaxX(), p);
        double maxY = NumberHelper.roundDouble(bounds.getMaxY(), p);
        double minX = NumberHelper.roundDouble(bounds.getMinX(), p);
        double minY = NumberHelper.roundDouble(bounds.getMinY(), p);
        double minZ = NumberHelper.roundDouble(bounds.getMinZ(), p);

        if (minX == clickX) return Facing.WEST;
        else if (maxX == clickX) return Facing.EAST;
        else if (minY == clickY) return Facing.TOP;
        else if (maxY == clickY) return Facing.BOTTOM;
        else if (minZ == clickZ) return Facing.SOUTH;
        else return Facing.NORTH;
    }

    private Vec3 getRoundedCoordinatesFromMouseEvent(MouseEvent mouseEvent) {
        Vec3 vec3Float = getCoordinatesFromMouseEvent(mouseEvent);
        if (vec3Float == null) return null;
        return new Vec3(Math.floor(vec3Float.x), Math.floor(vec3Float.y), Math.floor(vec3Float.z));
    }

    private ABlock getNewBlockFromPos(MouseEvent mouseEvent) {
        Vec3 vec3Rounded = getRoundedCoordinatesFromMouseEvent(mouseEvent);
        if (vec3Rounded == null) return null;

        return BlockFactory.createBlock(vec3Rounded, blockManager.currentBlock.getClass().getSimpleName());
    }

    private ABlock getExistingBlockFromPos(MouseEvent mouseEvent) {
        Box box = (Box) mouseEvent.getTarget();
        return blockManager.getBlockFromBox(box);
    }

    public void addBlock(ABlock aBlock) {
        if (aBlock.getBoxesArrayList().size() == 0) {
            System.out.println("No block to add... Set a connection?");
            return;
        }
        for (Box box : aBlock.getBoxesArrayList()) {
            box.setOnMouseClicked(this::handleMouseClick);
            box.setOnMouseMoved(this::handleBoxMouseMove);
            // TODO: check if block is already added to the scene
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
        this.controller = new FPSController(configProperties);
        controller.setScene(application.scene);

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
        blockManager.aBlocks = new HashMap<>();
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

    public void setEndBlock() {
        this.endBlockFlag = true;
    }

    public void setStartBlock() {
        this.startBlockFlag = true;
    }

    public void setPathBlock() {
        this.pathBlockFlag = true;
    }
}
