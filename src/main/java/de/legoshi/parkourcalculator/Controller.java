package de.legoshi.parkourcalculator;

import de.legoshi.parkourcalculator.gui.*;
import de.legoshi.parkourcalculator.parkour.environment.Environment;
import de.legoshi.parkourcalculator.parkour.PositionVisualizer;
import de.legoshi.parkourcalculator.parkour.environment.blocks.*;
import de.legoshi.parkourcalculator.parkour.environment.blocks.Pane;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.Player;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public BorderPane borderPane;
    public Button addButton;
    public VBox vBox;
    public HBox itemBox;
    public SubScene subScene;
    public ScrollPane tickList;
    public MenuBar menuBar;
    public AnchorPane debugHolder;

    private InputTickGUI inputTickGUI;
    private PositionVisualizer positionVisualizer;
    private MovementEngine movementEngine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setObjects(InputTickGUI inputTickGUI, PositionVisualizer positionVisualizer, MovementEngine movementEngine) {
        this.inputTickGUI = inputTickGUI;
        this.positionVisualizer = positionVisualizer;
        this.movementEngine = movementEngine;
    }

    public void loadConnectionUI() {
        new ConnectionGUI().show();
    }

    public void loadEditPlayerUI() {
        new EditPlayerGUI(movementEngine, positionVisualizer, inputTickGUI).show();
    }
}