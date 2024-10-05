package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.config.Configurable;
import de.legoshi.parkourcalculator.gui.VersionDependent;
import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.config.ConfigProperties;
import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.ParkourVersion;
import de.legoshi.parkourcalculator.util.PositionVisualizer;
import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuScreen extends VBox implements VersionDependent, Configurable {

    private static final Logger logger = LogManager.getLogger(MenuScreen.class.getName());
    @Setter private Application application;

    public VersionSettings versionSettings;
    public BlockSettings blockSettings;
    public PlayerSettings playerSettings;
    public ScreenSettings screenSeetings;
    public BruteforceSettings bruteforceSettings;
    public ExperimentalSettings experimentalSettings;

    private final CoordinateScreen coordinateScreen;
    private final PositionVisualizer positionVisualizer;
    private final NumberBinding remainingHeight;

    public MenuScreen(Application application, NumberBinding remainingHeight) {
        this.application = application;
        this.coordinateScreen = application.coordinateScreen;
        this.positionVisualizer = application.positionVisualizer;
        this.remainingHeight = remainingHeight;
        this.experimentalSettings = new ExperimentalSettings(application);
        this.blockSettings = new BlockSettings();
        this.versionSettings = new VersionSettings(application);

        maxHeightProperty().bind(application.window.heightProperty()
                .subtract(application.menuGUI.heightProperty())
                .subtract(application.coordinateScreen.heightProperty())
                .subtract(application.informationScreen.heightProperty())
                .subtract(application.blockGUI.heightProperty())
        );

        apply(application.currentParkour);
    }

    @Override
    public void apply(Parkour parkour) {
        this.getChildren().clear();

        this.playerSettings = new PlayerSettings(coordinateScreen, parkour, positionVisualizer);
        this.screenSeetings = new ScreenSettings(playerSettings, coordinateScreen);

        if (this.bruteforceSettings != null) this.bruteforceSettings.reset();
        this.bruteforceSettings = new BruteforceSettings(application);
        this.bruteforceSettings.apply(parkour);

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(versionSettings, blockSettings, playerSettings, screenSeetings,
                bruteforceSettings, experimentalSettings);

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.maxHeightProperty().bind(remainingHeight);

        getChildren().addAll(scrollPane);
        logger.info("MenuScreen applied");
    }

    @Override
    public void applyConfigValues(ConfigProperties configProperties) {
        ParkourVersion version = configProperties.getVersion();
        versionSettings.getVersionComboBox().setValue(version.toString());
    }
}
