package de.legoshi.parkourcalculator.gui.debug.menu;

import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MenuScreen extends VBox {

    public BlockSettings blockSettings;
    public PlayerSettings playerSettings;

    public MenuScreen(NumberBinding remainingHeight) {
        this.blockSettings = new BlockSettings();
        this.playerSettings = new PlayerSettings();

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(blockSettings, playerSettings);

        ScrollPane scrollPane = new ScrollPane(accordion);
        scrollPane.setFitToWidth(true);
        scrollPane.maxHeightProperty().bind(remainingHeight);

        getChildren().addAll(scrollPane);
    }

}
