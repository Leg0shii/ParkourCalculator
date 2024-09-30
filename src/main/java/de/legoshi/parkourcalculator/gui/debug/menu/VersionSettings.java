package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.simulation.ParkourVersion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;

public class VersionSettings extends TitledPane{

    @Getter private final ComboBox<String> versionComboBox = new ComboBox<>();

    public VersionSettings(Application application) {

        Text titleText = new Text("Version Settings");
        titleText.setFill(Color.WHITE);
        setGraphic(titleText);

        versionComboBox.getItems().addAll("V_1_8", "V_1_12", "V_1_20_4");
        versionComboBox.setValue(application.parkourVersion.toString());

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(new Label("Select Version"), 0, 0);
        gridPane.add(versionComboBox, 1, 0);

        versionComboBox.setOnAction(event -> {
            String newValue = versionComboBox.getValue();
            application.applyParkour(ParkourVersion.valueOf(newValue));
        });

        setContent(gridPane);
    }

}
