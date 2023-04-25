package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.gui.debug.CoordinateScreen;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.util.ConfigReader;
import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MenuScreen extends VBox {

    public BlockSettings blockSettings;
    public PlayerSettings playerSettings;
    public ScreenSettings screenSeetings;
    public ExperimentalSettings experimentalSettings;

    public MenuScreen(ConfigReader configReader, CoordinateScreen coordinateScreen, MovementEngine movementEngine, PositionVisualizer positionVisualizer, NumberBinding remainingHeight) {
        this.blockSettings = new BlockSettings();
        this.playerSettings = new PlayerSettings(coordinateScreen, movementEngine, positionVisualizer);
        this.screenSeetings = new ScreenSettings(configReader, playerSettings, coordinateScreen, positionVisualizer);
        this.experimentalSettings = new ExperimentalSettings();

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(blockSettings, playerSettings, screenSeetings, experimentalSettings);

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.maxHeightProperty().bind(remainingHeight);

        getChildren().addAll(scrollPane);
    }

}
