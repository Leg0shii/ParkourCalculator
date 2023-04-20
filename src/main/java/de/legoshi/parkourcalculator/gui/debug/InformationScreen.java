package de.legoshi.parkourcalculator.gui.debug;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Observable;
import java.util.Observer;

public class InformationScreen extends VBox implements Observer {

    private final Label screenInfoLabel = new Label("General Screen Information");
    private final Label blockFacingLabel = new Label("Block Facing: -");

    public InformationScreen() {
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setMinWidth(200);

        this.getStyleClass().add("coordinate-field");
        this.screenInfoLabel.getStyleClass().add("coords-title");
        this.blockFacingLabel.getStyleClass().add("coords-text");

        getChildren().addAll(screenInfoLabel, blockFacingLabel);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg.toString().length() <= 7)) return;
        blockFacingLabel.setText("Block Facing: " + arg);
    }
}
