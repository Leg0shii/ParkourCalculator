package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.DebugScreen;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.gui.menu.MenuScreen;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private MenuScreen menuScreen;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 1000, true);
        scene.getStylesheets().add(Application.class.getResource("darkmode.css").toExternalForm());
        this.controller = fxmlLoader.getController();

        this.environment = new Environment();
        this.movementEngine = new MovementEngine(environment);
        this.inputTickManager = new InputTickManager();

        InputTickGUI inputTickGUI = new InputTickGUI(inputTickManager, controller.addButton);
        inputTickGUI.setButtonAction(controller.vBox);

        this.menuScreen = new MenuScreen();
        this.debugScreen = new DebugScreen(movementEngine);

        VBox vBox = new VBox(debugScreen, menuScreen);
        this.controller.debugHolder.getChildren().add(vBox);

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
        Insets insets2 = new Insets(3, 3, 3, 3);
        Insets insets0 = new Insets(0, 0, 0, 0);
        Border blackBorder = new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3, 3, 3, 3)));
        Border grayBorder = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3, 3, 3, 3)));

        Environment.registeredBlocks.forEach(block -> {
            StackPane stackPane = new StackPane();
            stackPane.setPadding(insets2);

            ImageView imageView = new ImageView(block.image);
            stackPane.getChildren().add(imageView);
            controller.itemBox.getChildren().add(stackPane);

            stackPane.setOnMouseClicked(mouseEvent -> {
                for (Node node : controller.itemBox.getChildren()) {
                    if (node instanceof StackPane sP) {
                        sP.setBorder(null);
                        sP.setPadding(insets2);
                    }
                }
                stackPane.setPadding(insets0);
                stackPane.setBorder(blackBorder);
                Environment.updateCurrentBlock(block);
            });

            stackPane.setOnMouseEntered(mouseEvent -> {
                if (stackPane.getBorder() != null && stackPane.getBorder().equals(blackBorder)) return;
                stackPane.setPadding(insets0);
                stackPane.setBorder(grayBorder);
            });

            stackPane.setOnMouseExited(mouseEvent -> {
                if (stackPane.getBorder() != null && stackPane.getBorder().equals(blackBorder)) return;
                stackPane.setPadding(insets2);
                stackPane.setBorder(null);
            });
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