package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.parkour.environment.Environment;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class BlockGUI extends ScrollPane {

    private AnchorPane anchorPane;
    private HBox hBox;

    private StackPane selectedPane;

    public BlockGUI() {
        BorderPane.setAlignment(this, Pos.CENTER);

        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5.0);
        hBox.setPadding(new Insets(8, 8, 20, 8));

        anchorPane = new AnchorPane(hBox);
        setContent(anchorPane);

        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        registerBlocks();
    }

    private void registerBlocks() {
        DropShadow hoverEffect = new DropShadow();
        hoverEffect.setColor(Color.LIGHTGRAY);
        hoverEffect.setRadius(20.0);

        DropShadow clickEffect = new DropShadow();
        clickEffect.setColor(Color.GRAY);
        clickEffect.setRadius(20.0);

        Environment.registeredBlocks.forEach(block -> {
            StackPane stackPane = new StackPane();
            ImageView imageView = new ImageView(block.image);
            stackPane.getChildren().add(imageView);
            hBox.getChildren().add(stackPane);

            stackPane.setOnMouseClicked(mouseEvent -> {
                if (selectedPane != null) selectedPane.setEffect(null);
                selectedPane = stackPane;

                stackPane.setEffect(clickEffect);
                Environment.updateCurrentBlock(block);
            });

            stackPane.setOnMouseEntered(event -> stackPane.setEffect(hoverEffect));
            stackPane.setOnMouseExited(event -> stackPane.setEffect(selectedPane == stackPane ? clickEffect : null));

            if (Environment.currentBlock.getClass().getSimpleName().equals(block.getClass().getSimpleName())) {
                selectedPane = stackPane;
                stackPane.setEffect(clickEffect);
            }
        });
    }

}
