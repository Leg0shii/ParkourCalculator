package de.legoshi.parkourcalculator.gui.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.config.ConfigManager;
import de.legoshi.parkourcalculator.config.ConfigProperties;
import de.legoshi.parkourcalculator.simulation.ParkourVersion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class ConfigGUI extends Stage {

    private final Application application;
    private final ConfigManager configManager;

    private ChoiceBox<String> parkourVersion;
    private TextField forward, backward, left, right, up, down, sprint, placeBlock, destroyBlock;
    private TextField cameraSpeed, maxSpeedMultiplier, maxMouseMultiplier, mouseSpeed, coordinatePrecision;
    private CheckBox previewBlock, pathCollision, realVelocity;

    public ConfigGUI(Application application) {
        this.application = application;
        this.configManager = application.configManager;
    }

    // TODO: merge field creation with bruteforce settings
    public void showConfigScreen() {
        setTitle("Config Settings");

        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(10, 10, 10, 10));
        Scene configScene = new Scene(mainVBox);
        setScene(configScene);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        ConfigProperties configProperties = configManager.getConfigProperties();

        parkourVersion = createChoiceBox("PK-Version", configProperties.getVersion(), gridPane, 0);
        forward = createKeyTextField("Forward", configProperties.getForward(), gridPane, 1);
        backward = createKeyTextField("Backward", configProperties.getBackward(), gridPane, 2);
        left = createKeyTextField("Left", configProperties.getLeft(), gridPane, 3);
        right = createKeyTextField("Right", configProperties.getRight(), gridPane, 4);
        up = createKeyTextField("Up", configProperties.getUp(), gridPane, 5);
        down = createKeyTextField("Down", configProperties.getDown(), gridPane, 6);
        sprint = createKeyTextField("Sprint", configProperties.getSprint(), gridPane, 7);

        placeBlock = createMouseTextField("Place Block", configProperties.getPlaceBlock(), gridPane, 8);
        destroyBlock = createMouseTextField("Destroy Block", configProperties.getDestroyBlock(), gridPane, 9);

        cameraSpeed = createTextField("Camera Speed", configProperties.getCameraSpeed(), gridPane, 10);
        maxSpeedMultiplier = createTextField("Max Speed Multiplier", configProperties.getMaxSpeedMultiplier(), gridPane, 11);
        maxMouseMultiplier = createTextField("Max Mouse Multiplier", configProperties.getMaxMouseMultiplier(), gridPane, 12);
        mouseSpeed = createTextField("Mouse Speed", configProperties.getMouseSpeed(), gridPane, 13);
        coordinatePrecision = createTextField("Coordinate Precision", configProperties.getCoordinatePrecision(), gridPane, 14);

        previewBlock = createCheckBox("Preview Block", configProperties.isPreviewBlock(), gridPane, 15);
        pathCollision = createCheckBox("Path Collision", configProperties.isPathCollision(), gridPane, 16);
        realVelocity = createCheckBox("Real Velocity", configProperties.isRealVelocity(), gridPane, 17);

        mainVBox.getChildren().add(gridPane);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveConfig());
        buttonsBox.getChildren().add(saveButton);

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> resetConfig());
        buttonsBox.getChildren().add(resetButton);

        mainVBox.getChildren().add(buttonsBox);

        configScene.getStylesheets().add(Application.class.getResource("darkmode.css").toExternalForm());
        sizeToScene();
        show();
    }

    private TextField createKeyTextField(String labelText, String keyValue, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        TextField textField = new TextField(keyValue);
        textField.getStyleClass().add("key-text-field");
        textField.setEditable(false);
        textField.setOnMouseClicked(event -> textField.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyChange));
        gridPane.add(textField, 1, rowIndex);

        return textField;
    }

    private TextField createMouseTextField(String labelText, String keyValue, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        TextField textField = new TextField(keyValue);
        textField.getStyleClass().add("key-text-field");
        textField.setEditable(false);
        textField.setOnMouseClicked(event -> textField.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMouseChange));
        gridPane.add(textField, 1, rowIndex);

        return textField;
    }

    private TextField createTextField(String labelText, String value, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        TextField textField = new TextField(value);
        gridPane.add(textField, 1, rowIndex);

        return textField;
    }

    private TextField createTextField(String labelText, int value, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        TextField textField = new TextField(Integer.toString(value));
        UnaryOperator<TextFormatter.Change> intFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*")) {
                try {
                    int newValue = Integer.parseInt(newText);
                    if (newValue >= 1 && newValue <= 15) {
                        return change;
                    }
                } catch (NumberFormatException e) {
                    // If the text is empty or has a minus sign, allow the change
                    if (newText.isEmpty() || newText.equals("-")) {
                        return change;
                    }
                }
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(intFilter));
        gridPane.add(textField, 1, rowIndex);

        return textField;
    }

    private TextField createTextField(String labelText, double value, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        TextField textField = new TextField(Double.toString(value));
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([0-9]*[.])?[0-9]*")) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(doubleFilter));
        gridPane.add(textField, 1, rowIndex);

        return textField;
    }

    private CheckBox createCheckBox(String labelText, boolean selected, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(selected);
        gridPane.add(checkBox, 1, rowIndex);

        return checkBox;
    }

    private ChoiceBox<String> createChoiceBox(String labelText, ParkourVersion value, GridPane gridPane, int rowIndex) {
        Label label = new Label(labelText);
        gridPane.add(label, 0, rowIndex);

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("V_1_8", "V_1_12");
        choiceBox.setValue(value.toString());
        gridPane.add(choiceBox, 1, rowIndex);

        return choiceBox;
    }

    private void handleKeyChange(KeyEvent keyEvent) {
        TextField source = (TextField) keyEvent.getSource();
        source.setText(keyEvent.getCode().getName());
        source.removeEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyChange);
        source.setOnMouseClicked(null); // Stop accepting key presses after a key has been set
    }

    private void handleMouseChange(MouseEvent mouseEvent) {
        TextField source = (TextField) mouseEvent.getSource();
        MouseButton button = mouseEvent.getButton();
        source.setText(button.toString());
        source.removeEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMouseChange);
    }

    private void resetConfig() {
        ConfigProperties configProperties = configManager.getConfigProperties();
        configProperties.resetToDefault();
        parkourVersion.setValue(configProperties.getVersion().toString());
        forward.setText(configProperties.getForward());
        backward.setText(configProperties.getBackward());
        left.setText(configProperties.getLeft());
        right.setText(configProperties.getRight());
        up.setText(configProperties.getUp());
        down.setText(configProperties.getDown());
        sprint.setText(configProperties.getSprint());

        placeBlock.setText(configProperties.getPlaceBlock());
        destroyBlock.setText(configProperties.getDestroyBlock());

        cameraSpeed.setText(configProperties.getCameraSpeed() + "");
        maxSpeedMultiplier.setText(configProperties.getMaxSpeedMultiplier() + "");
        maxMouseMultiplier.setText(configProperties.getMaxMouseMultiplier() + "");
        mouseSpeed.setText(configProperties.getMouseSpeed() + "");
        coordinatePrecision.setText(configProperties.getCoordinatePrecision() + "");

        previewBlock.setSelected(configProperties.isPreviewBlock());
        pathCollision.setSelected(configProperties.isPathCollision());
        realVelocity.setSelected(configProperties.isRealVelocity());
    }

    private void saveConfig() {
        ConfigProperties configProperties = configManager.getConfigProperties();

        configProperties.setVersion(ParkourVersion.valueOf(parkourVersion.getValue()));
        configProperties.setForward(forward.getText());
        configProperties.setBackward(backward.getText());
        configProperties.setLeft(left.getText());
        configProperties.setRight(right.getText());
        configProperties.setUp(up.getText());
        configProperties.setDown(down.getText());
        configProperties.setSprint(sprint.getText());

        configProperties.setPlaceBlock(placeBlock.getText());
        configProperties.setDestroyBlock(destroyBlock.getText());

        configProperties.setCameraSpeed(Double.parseDouble(cameraSpeed.getText()));
        configProperties.setMaxSpeedMultiplier(Double.parseDouble(maxSpeedMultiplier.getText()));
        configProperties.setMaxMouseMultiplier(Double.parseDouble(maxMouseMultiplier.getText()));
        configProperties.setMouseSpeed(Double.parseDouble(mouseSpeed.getText()));
        configProperties.setCoordinatePrecision(Integer.parseInt(coordinatePrecision.getText()));

        configProperties.setPreviewBlock(previewBlock.isSelected());
        configProperties.setPathCollision(pathCollision.isSelected());
        configProperties.setRealVelocity(realVelocity.isSelected());

        configManager.saveConfig();
        configManager.applyConfig();
        application.applyParkour(configProperties.getVersion());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Config Saved");
        alert.setHeaderText(null);
        alert.setContentText("Configuration has been saved successfully.");
        alert.showAndWait();

        this.close();
    }

}
