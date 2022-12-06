package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.simulator.Player;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DebugScreen extends StackPane {

    private Label xPosition;
    private Label yPosition;
    private Label zPosition;

    public DebugScreen(Player player) {
        this.xPosition = new Label("X: " + player.getPosition().x);
        this.yPosition = new Label("Y: " + player.getPosition().y);
        this.zPosition = new Label("Z: " + player.getPosition().z);

        registerLabels();
    }

    private void registerLabels() {
        this.getChildren().addAll(xPosition, yPosition, zPosition);
    }

}
