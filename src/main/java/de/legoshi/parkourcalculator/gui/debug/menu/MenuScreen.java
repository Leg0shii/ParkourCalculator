package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MenuScreen extends VBox {

    public BlockSettings blockSettings;
    public PlayerSettings playerSettings;

    public MenuScreen(MovementEngine movementEngine, PositionVisualizer positionVisualizer, NumberBinding remainingHeight) {
        this.blockSettings = new BlockSettings();
        this.playerSettings = new PlayerSettings(movementEngine, positionVisualizer);

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(blockSettings, playerSettings);

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.maxHeightProperty().bind(remainingHeight);

        getChildren().addAll(scrollPane);
    }

}
