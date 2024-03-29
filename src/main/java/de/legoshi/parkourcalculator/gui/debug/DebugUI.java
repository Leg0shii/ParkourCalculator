package de.legoshi.parkourcalculator.gui.debug;

import de.legoshi.parkourcalculator.gui.debug.menu.MenuScreen;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DebugUI extends AnchorPane {

    public DebugUI(InformationScreen informationScreen, CoordinateScreen coordinateScreen, MenuScreen menuScreen) {
        BorderPane.setAlignment(this, Pos.CENTER);
        VBox vBox = new VBox(informationScreen, coordinateScreen, menuScreen);
        getChildren().add(vBox);
    }

}
