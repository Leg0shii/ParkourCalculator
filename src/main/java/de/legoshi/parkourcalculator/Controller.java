package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.DebugScreen;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Enderchest;
import de.legoshi.parkourcalculator.parkour.environment.blocks.StandardBlock;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public BorderPane borderPane;
    public Button addButton;
    public VBox vBox;
    public HBox itemBox;
    public SubScene subScene;
    public Group group;
    public AnchorPane debugHolder;
    public ScrollPane tickList;
    public MenuBar menuBar;
    private Group pathGroup;

    public MinecraftScreen minecraftScreen;
    private InputTickGUI inputTickGUI;
    private InputTickManager inputTickManager;
    private PositionVisualizer positionVisualizer;

    private DebugScreen debugScreen;

    public Environment environment;
    private MovementEngine movementEngine;
    private Player player;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.player = new Player(new Vec3(0.5, 1.0, 0.5), new Vec3(0, -0.0784000015258789, 0));
        this.environment = new Environment();
        this.movementEngine = new MovementEngine(player, environment);
        this.inputTickManager = new InputTickManager();

        this.inputTickGUI = new InputTickGUI(inputTickManager, addButton);
        this.inputTickGUI.setButtonAction(vBox);

        this.debugScreen = new DebugScreen(movementEngine);
        debugHolder.getChildren().add(debugScreen);

        registerBlocks();
    }

    private void registerBlocks() {
        StandardBlock standardBlock = new StandardBlock();
        Enderchest enderchest = new Enderchest();

        ImageView standardBlockIV = new ImageView(standardBlock.image);
        ImageView enderchestIV = new ImageView(enderchest.image);

        itemBox.getChildren().add(standardBlockIV);
        itemBox.getChildren().add(enderchestIV);

        Rectangle rectangle = new Rectangle(50, 50);
        rectangle.setFill(Color.RED);
        itemBox.getChildren().add(rectangle);
        // itemBox
    }

    // this is called after the initialize method was called
    public void setUpModelScreen(Scene scene) {
        this.group = new Group();

        // bad
        subScene.setRoot(group);
        subScene.heightProperty().bind(borderPane.heightProperty().subtract(menuBar.heightProperty()).subtract(itemBox.heightProperty()));
        subScene.widthProperty().bind(borderPane.widthProperty().subtract(tickList.widthProperty()).subtract(debugScreen.widthProperty()));

        this.minecraftScreen = new MinecraftScreen(group, scene, subScene, environment);
        this.minecraftScreen.setupModelScreen();

        this.pathGroup = new Group();
        this.positionVisualizer = new PositionVisualizer(pathGroup, movementEngine, inputTickManager);
        this.group.getChildren().add(pathGroup);
    }

    public void registerObservers() {
        inputTickManager.addObserver(positionVisualizer);
        inputTickManager.addObserver(debugScreen);
        minecraftScreen.addObserver(positionVisualizer);
        minecraftScreen.addObserver(environment);
        minecraftScreen.addObserver(debugScreen);
    }

}