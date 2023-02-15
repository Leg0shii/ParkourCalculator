package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.*;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.blocks.*;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Pane;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.event.ActionEvent;
import javafx.event.EventType;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public BorderPane borderPane;
    public Button addButton;
    public VBox vBox;
    public HBox itemBox;
    public SubScene subScene;
    public ScrollPane tickList;
    public MenuBar menuBar;

    public MinecraftScreen minecraftScreen;
    private InputTickGUI inputTickGUI;
    private InputTickManager inputTickManager;
    private PositionVisualizer positionVisualizer;

    public AnchorPane debugHolder;
    private DebugScreen debugScreen;

    public Environment environment;
    private MovementEngine movementEngine;

    private Group group;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.environment = new Environment();
        this.movementEngine = new MovementEngine(environment);
        this.inputTickManager = new InputTickManager();

        this.inputTickGUI = new InputTickGUI(inputTickManager, addButton);
        this.inputTickGUI.setButtonAction(vBox);

        this.debugScreen = new DebugScreen(movementEngine);
        this.debugHolder.getChildren().add(debugScreen);

        registerBlocks();
    }

    private void registerBlocks() {
        Environment.registeredBlocks.forEach(block -> {
            ImageView imageView = new ImageView(block.image);
            this.itemBox.getChildren().add(imageView);
            imageView.setOnMouseClicked(mouseEvent -> Environment.updateCurrentBlock(block));
        });
    }

    // this is called after the initialize method was called
    public void setUpModelScreen(Scene scene) {
        this.group = new Group();

        // bad
        subScene.setRoot(group);
        subScene.heightProperty().bind(borderPane.heightProperty().subtract(menuBar.heightProperty()).subtract(itemBox.heightProperty()));
        subScene.widthProperty().bind(borderPane.widthProperty().subtract(tickList.widthProperty()).subtract(debugScreen.widthProperty()));
        tickList.setContent(vBox);

        this.minecraftScreen = new MinecraftScreen(group, scene, subScene);
        this.minecraftScreen.setupModelScreen();
        // this.minecraftScreen.addStartingBlock();

        Group pathGroup = new Group();
        this.positionVisualizer = new PositionVisualizer(pathGroup, movementEngine, inputTickManager);
        group.getChildren().add(pathGroup);
    }

    public void registerObservers() {
        inputTickManager.addObserver(positionVisualizer);
        inputTickManager.addObserver(debugScreen);
        minecraftScreen.addObserver(positionVisualizer);
        minecraftScreen.addObserver(environment);
        minecraftScreen.addObserver(debugScreen);
    }

    public void loadConnectionUI() {
        new ConnectionGUI().show();
    }

    public void loadEditPlayerUI() {
        new EditPlayerGUI(movementEngine, positionVisualizer, inputTickGUI).show();
    }
}