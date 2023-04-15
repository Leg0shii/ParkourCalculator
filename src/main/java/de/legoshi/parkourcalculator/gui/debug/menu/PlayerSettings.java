package de.legoshi.parkourcalculator.gui.debug.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PlayerSettings extends TitledPane {

    private TextField xPosField;
    private TextField yPosField;
    private TextField zPosField;
    private TextField xVelField;
    private TextField yVelField;
    private TextField zVelField;
    private TextField facingField;

    public PlayerSettings() {
        Text titleText = new Text("Player Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        // Create the text fields
        xPosField = new TextField();
        xPosField.setPromptText("X Position");

        yPosField = new TextField();
        yPosField.setPromptText("Y Position");

        zPosField = new TextField();
        zPosField.setPromptText("Z Position");

        xVelField = new TextField();
        xVelField.setPromptText("X Velocity");

        yVelField = new TextField();
        yVelField.setPromptText("Y Velocity");

        zVelField = new TextField();
        zVelField.setPromptText("Z Velocity");

        facingField = new TextField();
        facingField.setPromptText("Facing");

        // Create a GridPane to hold the text fields
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label("Position:"), 0, 0);
        gridPane.add(xPosField, 0, 1);
        gridPane.add(yPosField, 0, 2);
        gridPane.add(zPosField, 0, 3);

        gridPane.add(new Label("Velocity:"), 1, 0);
        gridPane.add(xVelField, 1, 1);
        gridPane.add(yVelField, 1, 2);
        gridPane.add(zVelField, 1, 3);

        gridPane.add(new Label("Facing:"), 0, 4);
        gridPane.add(facingField, 0, 5);

        // Create the button to apply values
        Button getButton = new Button("Get values");
        getButton.setOnAction(event -> {
            // Get values from somewhere (e.g. a game engine) and set them in the text fields
            xPosField.setText(Double.toString(10.5));
            yPosField.setText(Double.toString(20.2));
            zPosField.setText(Double.toString(30.9));
            xVelField.setText(Double.toString(0.3));
            yVelField.setText(Double.toString(1.2));
            zVelField.setText(Double.toString(-0.5));
            facingField.setText(Double.toString(1.3));
        });

        Button applyButton = new Button("Apply values");
        applyButton.setOnAction(event -> {

        });

        // Add the GridPane and button to a VBox
        HBox hBox = new HBox(getButton, applyButton);
        hBox.setSpacing(30);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(gridPane, hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        // Set the content of the titledPane to the VBox
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
        setContent(scrollPane);
    }

}
