package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Block;
import de.legoshi.parkourcalculator.parkour.environment.blocks.FullBlock;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.simulator.Parkour;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class Controller implements Initializable, Observer {

    public BorderPane borderPane;
    public Button addButton;
    public VBox vBox;
    public HBox itemBox;
    public SubScene subScene;
    public Group group;
    private Group pathGroup;

    public MinecraftScreen minecraftScreen;
    private InputTickGUI inputTickGUI;
    private PositionVisualizer positionVisualizer;
    public static Block.DrawableBlock selectedBlock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Environment environment = new Environment();
        Parkour parkour = new Parkour();
        InputTickManager inputTickManager = new InputTickManager();

        this.positionVisualizer = new PositionVisualizer(parkour, inputTickManager);
        this.inputTickGUI = new InputTickGUI();

        this.inputTickGUI.addObserver(inputTickManager, addButton);
        inputTickManager.addObserver(this);

        this.pathGroup = new Group();

        registerAddButton();
        registerBlocks();
    }

    private void registerAddButton() {
        inputTickGUI.setButtonAction(vBox);
    }

    private void registerBlocks() {
        FullBlock.DrawableFullBlock fullBlock = new FullBlock.DrawableFullBlock();
        itemBox.getChildren().addAll(fullBlock);
    }

    @Override
    public void update(Observable o, Object arg) {
        pathGroup.getChildren().clear();
        pathGroup = positionVisualizer.generatePlayerPath();
        group.getChildren().removeAll(pathGroup);
        group.getChildren().add(pathGroup);
    }

    public void setUpModelScreen(Scene scene) {
        this.group = new Group();
        subScene.setRoot(group);
        subScene.heightProperty().bind(borderPane.heightProperty().subtract(110));
        subScene.widthProperty().bind(borderPane.widthProperty().subtract(558));
        MinecraftScreen minecraftScreen = new MinecraftScreen(group, scene, subScene, positionVisualizer);
        minecraftScreen.setupModelScreen(group, scene, subScene);
    }

}