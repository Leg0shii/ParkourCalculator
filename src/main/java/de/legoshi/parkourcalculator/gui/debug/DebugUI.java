package de.legoshi.parkourcalculator.gui.debug;

import de.legoshi.parkourcalculator.gui.debug.menu.MenuScreen;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DebugUI extends AnchorPane {

    private final CoordinateScreen coordinateScreen;
    private final MenuScreen menuScreen;

    public DebugUI(CoordinateScreen coordinateScreen, MenuScreen menuScreen) {
        BorderPane.setAlignment(this, Pos.CENTER);

        this.coordinateScreen = coordinateScreen;
        this.menuScreen = menuScreen;

        VBox vBox = new VBox(coordinateScreen, menuScreen);
        getChildren().add(vBox);
    }

}
