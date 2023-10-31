package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;

public class ExperimentalSettings extends TitledPane {

    private final Application application;

    public ExperimentalSettings(Application application) {
        this.application = application;
        
        Text titleText = new Text("Experimental Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);
        
        HBox hBox = new HBox();
        hBox.setSpacing(2);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setAlignment(Pos.BASELINE_CENTER);
        setContent(hBox);

        disable();
    }
    
    private void disable() {
    
    }

}
