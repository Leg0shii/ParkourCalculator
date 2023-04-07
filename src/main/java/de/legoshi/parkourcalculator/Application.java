package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.DebugScreen;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    private Controller controller;

    private Environment environment;
    private MovementEngine movementEngine;

    private MinecraftScreen minecraftScreen;
    private InputTickManager inputTickManager;
    private PositionVisualizer positionVisualizer;
    private DebugScreen debugScreen;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 1000, true);
        this.controller = fxmlLoader.getController();

        this.environment = new Environment();
        this.movementEngine = new MovementEngine(environment);
        this.inputTickManager = new InputTickManager();

        InputTickGUI inputTickGUI = new InputTickGUI(inputTickManager, controller.addButton);
        inputTickGUI.setButtonAction(controller.vBox);

        this.debugScreen = new DebugScreen(movementEngine);
        this.controller.debugHolder.getChildren().add(debugScreen);

        registerBlocks();

        setUpModelScreen();
        registerObservers();

        this.minecraftScreen.addStartingBlock();
        this.controller.setObjects(inputTickGUI, positionVisualizer, movementEngine);

        stage.setTitle("Parkour Simulator!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void registerBlocks() {
        Environment.registeredBlocks.forEach(block -> {
            ImageView imageView = new ImageView(block.image);
            controller.itemBox.getChildren().add(imageView);
            imageView.setOnMouseClicked(mouseEvent -> Environment.updateCurrentBlock(block));
        });
    }

    private void setUpModelScreen() {
        Group group = new Group();

        controller.subScene.setRoot(group);
        controller.subScene.heightProperty().bind(controller.borderPane.heightProperty().subtract(controller.menuBar.heightProperty()).subtract(controller.itemBox.heightProperty()));
        controller.subScene.widthProperty().bind(controller.borderPane.widthProperty().subtract(controller.tickList.widthProperty()).subtract(debugScreen.widthProperty()));
        controller.tickList.setContent(controller.vBox);

        this.minecraftScreen = new MinecraftScreen(group, controller.subScene);
        // this.minecraftScreen.setupModelScreen();

        Group pathGroup = new Group();
        this.positionVisualizer = new PositionVisualizer(pathGroup, movementEngine, inputTickManager);
        group.getChildren().add(pathGroup);
    }

    private void registerObservers() {
        inputTickManager.addObserver(positionVisualizer);
        inputTickManager.addObserver(debugScreen);
        minecraftScreen.addObserver(positionVisualizer);
        minecraftScreen.addObserver(environment);
        minecraftScreen.addObserver(debugScreen);
    }

}