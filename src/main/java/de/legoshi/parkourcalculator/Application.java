package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.BlockGUI;
import de.legoshi.parkourcalculator.gui.MenuGUI;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.gui.debug.DebugUI;
import de.legoshi.parkourcalculator.gui.debug.InformationScreen;
import de.legoshi.parkourcalculator.gui.debug.menu.MenuScreen;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    public BorderPane window;

    public DebugUI debugUI;
    public InputTickGUI inputTickGUI;
    public BlockGUI blockGUI;
    public MenuGUI menuGUI;
    public MinecraftGUI minecraftGUI;
    public SubScene minecraftSubScene;
    public CoordinateScreen coordinateScreen;
    public InformationScreen informationScreen;

    public MenuScreen menuScreen;
    public Group minecraftScreenGroup;

    public InputTickManager inputTickManager;
    public PositionVisualizer positionVisualizer;

    public Environment environment;
    public MovementEngine movementEngine;

    @Override
    public void start(Stage stage) {
        this.window = new BorderPane();
        Scene scene = new Scene(window, 1400, 1000, true);
        scene.getStylesheets().add(Application.class.getResource("darkmode.css").toExternalForm());

        // load environment and bind it to the movement-engine
        this.environment = new Environment();
        this.movementEngine = new MovementEngine(environment);

        // load the input manager and the UI
        this.inputTickManager = new InputTickManager();
        this.inputTickGUI = new InputTickGUI(inputTickManager);
        this.window.setLeft(inputTickGUI);

        // load path group to display player-movement and bind to pos-visualizer
        Group pathGroup = new Group();
        this.positionVisualizer = new PositionVisualizer(pathGroup, movementEngine, inputTickManager);

        // load and register block gui
        this.blockGUI = new BlockGUI();
        this.window.setBottom(blockGUI);

        // load the menu bar
        this.menuGUI = new MenuGUI(scene.getWindow(), this);
        this.window.setTop(menuGUI);

        // load coordinate-screen and the menu-accordion
        this.informationScreen = new InformationScreen();
        this.coordinateScreen = new CoordinateScreen(movementEngine);
        this.menuScreen = new MenuScreen(coordinateScreen, movementEngine, positionVisualizer, getMenuOffset(scene));
        this.debugUI = new DebugUI(informationScreen, coordinateScreen, menuScreen);
        this.window.setRight(debugUI);

        // load groups for blocks, bind to minecraft screen, apply to borderpane
        this.minecraftScreenGroup = new Group();
        this.minecraftSubScene = new SubScene(minecraftScreenGroup, 500, 500, true, SceneAntialiasing.DISABLED);
        this.window.setCenter(this.minecraftSubScene);
        this.minecraftGUI = new MinecraftGUI(this, minecraftScreenGroup);
        this.minecraftScreenGroup.getChildren().add(pathGroup);

        this.inputTickManager.addObserver(positionVisualizer);
        this.inputTickManager.addObserver(coordinateScreen);

        this.positionVisualizer.addObserver(coordinateScreen);

        stage.setTitle("Parkour Simulator!");
        stage.setScene(scene);
        stage.show();

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        if (screenBounds.getHeight() - 100 < stage.getHeight()
                || screenBounds.getWidth() - 100 < stage.getWidth()) {
            stage.setX(20);
            stage.setY(20);
            stage.setWidth(screenBounds.getWidth() - 150);
            stage.setHeight(screenBounds.getHeight() - 150);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private NumberBinding getMenuOffset(Scene scene) {
        return scene.heightProperty()
                .subtract(blockGUI.heightProperty())
                .subtract(menuGUI.heightProperty())
                .subtract(coordinateScreen.heightProperty());
    }

}