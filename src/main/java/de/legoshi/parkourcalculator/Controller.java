package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftScreen;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.parkour.environment.blocks.StandardBlock;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.simulator.Parkour;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
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

public class Controller implements Initializable {

    public BorderPane borderPane;
    public Button addButton;
    public VBox vBox;
    public HBox itemBox;
    public SubScene subScene;
    public Group group;
    private Group pathGroup;

    public Environment environment;
    private Parkour parkour;
    public MinecraftScreen minecraftScreen;
    private InputTickManager inputTickManager;
    private InputTickGUI inputTickGUI;
    private PositionVisualizer positionVisualizer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Player player = new Player(new Vec3(0, 1.0, 0), new Vec3(0, -0.0784000015258789, 0));
        this.environment = new Environment();
        this.parkour = new Parkour(player, environment);
        this.inputTickManager = new InputTickManager();

        this.inputTickGUI = new InputTickGUI(inputTickManager, addButton);
        this.inputTickGUI.setButtonAction(vBox);
        registerBlocks();
    }

    private void registerBlocks() {

    }

    // this is called after the initialize method was called
    public void setUpModelScreen(Scene scene) {
        this.group = new Group();

        // bad
        subScene.setRoot(group);
        subScene.heightProperty().bind(borderPane.heightProperty().subtract(110));
        subScene.widthProperty().bind(borderPane.widthProperty().subtract(558));

        this.minecraftScreen = new MinecraftScreen(group, scene, subScene);
        this.minecraftScreen.setupModelScreen();

        this.pathGroup = new Group();
        this.positionVisualizer = new PositionVisualizer(pathGroup, parkour, inputTickManager);
        this.group.getChildren().add(pathGroup);
    }

    public void registerObservers() {
        inputTickManager.addObserver(positionVisualizer);
        minecraftScreen.addObserver(positionVisualizer);
        minecraftScreen.addObserver(environment);
    }

}