module de.legoshi.parkourcalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    opens de.legoshi.parkourcalculator to javafx.fxml;
    exports de.legoshi.parkourcalculator;
    exports de.legoshi.parkourcalculator.parkour.environment.blocks;
    opens de.legoshi.parkourcalculator.parkour.environment.blocks to javafx.fxml;
    exports de.legoshi.parkourcalculator.parkour;
    opens de.legoshi.parkourcalculator.parkour to javafx.fxml;
    exports de.legoshi.parkourcalculator.parkour.tick;
    opens de.legoshi.parkourcalculator.parkour.tick to javafx.fxml;
    exports de.legoshi.parkourcalculator.parkour.environment;
    opens de.legoshi.parkourcalculator.parkour.environment to javafx.fxml;
    exports de.legoshi.parkourcalculator.parkour.simulator;
    exports de.legoshi.parkourcalculator.util;
    exports de.legoshi.parkourcalculator.gui;
}