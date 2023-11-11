package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.BlockGUI;
import de.legoshi.parkourcalculator.gui.VersionDependent;
import de.legoshi.parkourcalculator.gui.debug.menu.ScreenSettings;
import de.legoshi.parkourcalculator.gui.menu.ConfigGUI;
import de.legoshi.parkourcalculator.gui.menu.ConfigProperties;
import de.legoshi.parkourcalculator.gui.menu.MenuGUI;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.gui.InputTickGUI;
import de.legoshi.parkourcalculator.gui.MinecraftGUI;
import de.legoshi.parkourcalculator.gui.debug.DebugUI;
import de.legoshi.parkourcalculator.gui.debug.InformationScreen;
import de.legoshi.parkourcalculator.gui.debug.menu.MenuScreen;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.ParkourVersion;
import de.legoshi.parkourcalculator.simulation.Parkour_1_12;
import de.legoshi.parkourcalculator.simulation.Parkour_1_8;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import de.legoshi.parkourcalculator.simulation.tick.InputTickManager;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Application extends javafx.application.Application {

    public static String APP_NAME = "Parkour Calculator Alpha v1.1.0";

    public Scene scene;
    public BorderPane window;
    private List<VersionDependent> versionDependentList;

    public DebugUI debugUI;
    public InputTickGUI inputTickGUI;
    public BlockGUI blockGUI;
    public MenuGUI menuGUI;
    public ConfigGUI configGUI;
    public MinecraftGUI minecraftGUI;
    public SubScene minecraftSubScene;
    public CoordinateScreen coordinateScreen;
    public InformationScreen informationScreen;

    public MenuScreen menuScreen;
    public Group minecraftScreenGroup;

    public InputTickManager inputTickManager;
    public PositionVisualizer positionVisualizer;

    public Parkour currentParkour;

    public Parkour_1_8 parkour_1_8;
    public Parkour_1_12 parkour_1_12;

    public ParkourVersion parkourVersion;

    @Override
    public void start(Stage stage) {
        this.window = new BorderPane();
        this.scene = new Scene(window, 1400, 1000, true);
        this.scene.getStylesheets().add(Application.class.getResource("darkmode.css").toExternalForm());

        this.configGUI = new ConfigGUI(this);

        // different parkour versions
        this.parkourVersion = configGUI.getConfigProperties().getVersion();
        applyParkour(parkourVersion);

        // load the input manager and the UI
        this.inputTickManager = new InputTickManager();
        this.inputTickGUI = new InputTickGUI(inputTickManager);
        this.window.setLeft(inputTickGUI);

        // load path group to display player-movement and bind to pos-visualizer
        Group pathGroup = new Group();
        this.positionVisualizer = new PositionVisualizer(pathGroup, currentParkour, inputTickManager);

        // load and register block gui
        this.blockGUI = new BlockGUI(currentParkour);
        this.window.setBottom(blockGUI);

        // load the menu bar
        this.menuGUI = new MenuGUI(scene.getWindow(), this);
        this.window.setTop(menuGUI);

        // load coordinate-screen and the menu-accordion
        this.informationScreen = new InformationScreen();
        this.informationScreen.updateVersionLabel(parkourVersion.toString());
        this.coordinateScreen = new CoordinateScreen(currentParkour);
        this.menuScreen = new MenuScreen(this, getMenuOffset(scene));
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
        this.positionVisualizer.generatePlayerPath();

        fillVersionDependencyList();
        updateConfigValues(configGUI.getConfigProperties());

        stage.setTitle(APP_NAME);
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

    private void fillVersionDependencyList() {
        versionDependentList = new ArrayList<>();
        versionDependentList.add(coordinateScreen);
        versionDependentList.add(minecraftGUI);
        versionDependentList.add(blockGUI);
        versionDependentList.add(menuScreen);
        versionDependentList.add(menuGUI);
        versionDependentList.add(positionVisualizer);
    }

    public static void main(String[] args) {
        launch();
    }

    public void updateConfigValues(ConfigProperties configProperties) {
        ScreenSettings.precision = configProperties.getCoordinatePrecision();
        minecraftGUI.getController().updateConfigValues(configProperties);
        coordinateScreen.updateConfigValues();
        minecraftGUI.updateConfigValues(configProperties);

        ScreenSettings.getCoordinatePrecTF().setText(configProperties.getCoordinatePrecision() + "");
        ScreenSettings.getPreviewBlockCB().setSelected(configProperties.isPreviewBlock());
        ScreenSettings.getPathCollisionCB().setSelected(configProperties.isPathCollision());
        ScreenSettings.getRealVelCB().setSelected(configProperties.isRealVelocity());

        if (configProperties.isRealVelocity()) coordinateScreen.update();

        ParkourVersion version = configProperties.getVersion();
        menuScreen.versionSettings.getVersionComboBox().setValue(version.toString());
        applyParkour(version);
    }

    public void applyParkour(ParkourVersion parkourVersion) {
        if (parkourVersion == ParkourVersion.V_1_8) {
            currentParkour = parkour_1_8 == null ? new Parkour_1_8() : parkour_1_8;
            parkour_1_8 = (Parkour_1_8) currentParkour;
        } else {
            currentParkour = parkour_1_12 == null ? new Parkour_1_12() : parkour_1_12;
            parkour_1_12 = (Parkour_1_12) currentParkour;
        }
        if (versionDependentList != null) {
            versionDependentList.stream().filter(Objects::nonNull).forEach(versionDependent -> versionDependent.apply(currentParkour));
        }
        this.parkourVersion = parkourVersion;
    }

    private NumberBinding getMenuOffset(Scene scene) {
        return scene.heightProperty()
                .subtract(blockGUI.heightProperty())
                .subtract(menuGUI.heightProperty())
                .subtract(coordinateScreen.heightProperty());
    }

}