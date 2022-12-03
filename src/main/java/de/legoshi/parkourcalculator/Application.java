package de.legoshi.parkourcalculator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 1000, true);

        Controller controller = fxmlLoader.getController();
        controller.setUpModelScreen(scene);
        controller.registerObservers();

        // bad, make order better
        controller.minecraftScreen.addStartingBlock();

        stage.setTitle("Parkour Simulator!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}