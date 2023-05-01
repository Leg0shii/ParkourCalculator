package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.gui.menu.ConfigProperties;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MenuScreen extends VBox {

    private final Application application;

    public VersionSettings versionSettings;
    public BlockSettings blockSettings;
    public PlayerSettings playerSettings;
    public ScreenSettings screenSeetings;
    public BruteforceSettings bruteforceSettings;
    public ExperimentalSettings experimentalSettings;

    private final CoordinateScreen coordinateScreen;
    private final PositionVisualizer positionVisualizer;
    private final ConfigProperties configProperties;
    private final NumberBinding remainingHeight;

    public MenuScreen(Application application, NumberBinding remainingHeight) {
        this.application = application;
        this.configProperties = application.configGUI.getConfigProperties();
        this.coordinateScreen = application.coordinateScreen;
        this.positionVisualizer = application.positionVisualizer;
        this.remainingHeight = remainingHeight;
        this.experimentalSettings = new ExperimentalSettings();
        this.blockSettings = new BlockSettings();
        this.versionSettings = new VersionSettings(application);

        apply(application.currentParkour);
    }

    public void apply(Parkour parkour) {
        this.getChildren().clear();

        this.playerSettings = new PlayerSettings(coordinateScreen, parkour, positionVisualizer);
        this.screenSeetings = new ScreenSettings(configProperties, playerSettings, coordinateScreen);

        if (this.bruteforceSettings != null) this.bruteforceSettings.reset();
        this.bruteforceSettings = new BruteforceSettings(application);

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(versionSettings, blockSettings, playerSettings, screenSeetings,
                bruteforceSettings, experimentalSettings);

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.maxHeightProperty().bind(remainingHeight);

        getChildren().addAll(scrollPane);
        System.out.println("MenuScreen applied");
    }

}
