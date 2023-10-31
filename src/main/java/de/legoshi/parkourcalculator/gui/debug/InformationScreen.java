package de.legoshi.parkourcalculator.gui.debug;

import de.legoshi.parkourcalculator.simulation.environment.Facing;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Observable;
import java.util.Observer;

public class InformationScreen extends VBox implements Observer {

    private final Label screenInfoLabel = new Label("General Screen Information");
    private final Label currentVersion = new Label("Current Version: -");
    private final Label blockFacingLabel = new Label("Block Facing: -");
    private final Label blockPosLabel = new Label("Block Position: -");

    public InformationScreen() {
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setMinWidth(200);

        this.getStyleClass().add("coordinate-field");
        this.screenInfoLabel.getStyleClass().add("coords-title");
        this.currentVersion.getStyleClass().add("coords-text");
        this.blockFacingLabel.getStyleClass().add("coords-text");
        this.blockPosLabel.getStyleClass().add("coords-text");

        getChildren().addAll(screenInfoLabel, currentVersion, blockFacingLabel, blockPosLabel);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg.toString().contains("block-info"))) return;
        String objectString = arg.toString();
        String[] args = objectString.split(";");

        blockFacingLabel.setText("Block Facing: " + args[1]);
        blockPosLabel.setText("Block at: (" + args[2] + "," + args[3] + "," + args[4] + ")");
    }

    public void updateVersionLabel(String val) {
        currentVersion.setText("Current Version: " + val);
    }

}
