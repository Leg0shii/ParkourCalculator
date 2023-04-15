package de.legoshi.parkourcalculator.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class MenuGUI extends MenuBar {

    public MenuGUI() {
        Menu fileMenu = new Menu("File");
        MenuItem openStratMenuItem = new MenuItem("openStrat");

        openStratMenuItem.setOnAction(event -> {
            System.out.println("openStrat option selected");
        });

        fileMenu.getItems().add(openStratMenuItem);
        getMenus().add(fileMenu);

        VBox layout = new VBox(this);
    }

}
