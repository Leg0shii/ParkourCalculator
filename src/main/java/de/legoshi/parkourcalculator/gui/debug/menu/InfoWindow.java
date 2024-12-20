package de.legoshi.parkourcalculator.gui.debug.menu;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.ai.BruteforceOptions;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.*;

public class InfoWindow extends Stage {
    private BruteforceOptions options;

    private Label statusLabel;
    private Label timePassedLabel;
    private Label globalBestLabel;
    private Label nextSyncLabel;

    @Getter
    private Map<String, BruteforcerUI> bruteforcerMap = new HashMap<>();
    private VBox bruteforcerListContainer;

    public InfoWindow() {
        Label titleLabel = new Label("Bruteforcer Information");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        statusLabel = new Label("   Status: ");
        timePassedLabel = new Label("   Time Passed: ");
        globalBestLabel = new Label("   Global Best Solution: ");
        nextSyncLabel = new Label("   Next Bruteforcer Sync: ");

        VBox topBox = new VBox(5, titleLabel, statusLabel, timePassedLabel, globalBestLabel, nextSyncLabel);
        Label listHeader = new Label("Bruteforcer List");
        listHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        bruteforcerListContainer = new VBox(5);
        ScrollPane scrollPane = new ScrollPane(bruteforcerListContainer);
        scrollPane.setFitToWidth(true);

        VBox mainLayout = new VBox(10, topBox, listHeader, scrollPane);
        mainLayout.setPadding(new Insets(10));
        setTitle("Bruteforcer Information");

        Scene scene = new Scene(mainLayout, 400, 900);
        scene.getStylesheets().add(Application.class.getResource("darkmode.css").toExternalForm());
        setScene(scene);
    }

    public void clearWindow() {
        bruteforcerMap.clear();
        bruteforcerListContainer.getChildren().clear();
    }

    public void updateStatus(String text) {
        Platform.runLater(() -> statusLabel.setText("   Status: " + text));
    }

    public void updateTimePassed(String text) {
        Platform.runLater(() -> timePassedLabel.setText("   Time Passed: " + text + "s"));
    }

    public void updateGlobalBest(String text) {
        Platform.runLater(() -> globalBestLabel.setText("   Global Best Solution: " + text + " Ticks"));
    }

    public void updateNextSync(String text) {
        Platform.runLater(() -> nextSyncLabel.setText("   Next Bruteforcer Sync: " + text + "s"));
    }

    public void addBruteforcer(String id) {
        Label titleLabel = new Label("Bruteforcer " + id);
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label generationLabel = new Label("   Random Generation from Tick: ");
        Label intervalLabel = new Label("   Current Tick Selection Intervall: ");
        Label nextIntervalLabel = new Label("   Next Tick Intervall: ");
        Label bestSolutionLabel = new Label("   Current Best Solution: ");

        VBox bruteforcerBox = new VBox(3);
        bruteforcerBox.getChildren().add(titleLabel);
        bruteforcerBox.getChildren().add(generationLabel);
        if (options.isWindowed()) {
            bruteforcerBox.getChildren().add(intervalLabel);
            bruteforcerBox.getChildren().add(nextIntervalLabel);
        } else {
            bruteforcerBox.getChildren().add(intervalLabel);
        }
        bruteforcerBox.getChildren().add(bestSolutionLabel);

        bruteforcerBox.setStyle("-fx-background-color: #3a3a3a; -fx-padding: 10; -fx-border-color: #4a4a4a; -fx-border-width: 0 0 1 0;");
        bruteforcerMap.put(id, new BruteforcerUI(generationLabel, intervalLabel, nextIntervalLabel, bestSolutionLabel));
        Platform.runLater(() -> bruteforcerListContainer.getChildren().add(bruteforcerBox));
    }

    public void updateBruteforcer(String id, String generation, String interval, String nextInterval, String bestSolution) {
        BruteforcerUI ui = bruteforcerMap.get(id);
        if (ui != null) {
            Platform.runLater(() -> {
                ui.generationLabel.setText("   Random Generation from Tick: " + generation);
                if (options.isWindowed()) {
                    ui.intervalLabel.setText("   Current Tick Selection Intervall: [" + interval + "]");
                    ui.nextIntervalLabel.setText("   Next Tick Intervall: " + nextInterval + "s");
                } else {
                    ui.intervalLabel.setText("   Current Tick Selection Intervall: " + interval + "s");
                }
                ui.bestSolutionLabel.setText("   Current Best Solution: " + bestSolution + " Ticks");
            });
        }
    }

    public void setBFOptions(BruteforceOptions bruteforceOptions) {
        this.options = bruteforceOptions;
    }

    private static class BruteforcerUI {
        Label generationLabel;
        Label intervalLabel;
        Label nextIntervalLabel;
        Label bestSolutionLabel;

        BruteforcerUI(Label generationLabel, Label intervalLabel, Label nextIntervalLabel, Label bestSolutionLabel) {
            this.generationLabel = generationLabel;
            this.intervalLabel = intervalLabel;
            this.nextIntervalLabel = nextIntervalLabel;
            this.bestSolutionLabel = bestSolutionLabel;
        }
    }
}
