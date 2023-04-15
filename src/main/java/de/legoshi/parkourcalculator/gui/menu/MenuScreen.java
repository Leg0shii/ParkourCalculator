package de.legoshi.parkourcalculator.gui.menu;

import javafx.scene.control.Accordion;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Collections;

public class MenuScreen extends Accordion {

    public BlockSettings blockSettings;
    public PlayerSettings playerSettings;

    public MenuScreen() {
        this.blockSettings = new BlockSettings();
        this.playerSettings = new PlayerSettings();

        getPanes().addAll(blockSettings, playerSettings);
    }

}
