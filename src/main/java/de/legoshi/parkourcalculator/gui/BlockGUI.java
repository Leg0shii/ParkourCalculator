package de.legoshi.parkourcalculator.gui;

import de.legoshi.parkourcalculator.simulation.Parkour;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
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

    private BlockManager blockManager;
    private final HBox hBox;
    private StackPane selectedPane;

    public BlockGUI(Parkour parkour) {
        BorderPane.setAlignment(this, Pos.CENTER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5.0);
        hBox.setPadding(new Insets(8, 8, 20, 8));

        setContent(new AnchorPane(hBox));
        apply(parkour);
    }

    public void apply(Parkour parkour) {
        this.blockManager = parkour.getBlockManager();
        hBox.getChildren().clear();
        registerBlocks();
        System.out.println("BlockGUI applied");
    }

    private void registerBlocks() {
        DropShadow hoverEffect = new DropShadow();
        hoverEffect.setColor(Color.LIGHTGRAY);
        hoverEffect.setRadius(20.0);

        DropShadow clickEffect = new DropShadow();
        clickEffect.setColor(Color.GRAY);
        clickEffect.setRadius(20.0);

        blockManager.registeredBlocks.forEach(block -> {
            StackPane stackPane = new StackPane();
            ImageView imageView = new ImageView(block.image);
            stackPane.getChildren().add(imageView);
            hBox.getChildren().add(stackPane);

            stackPane.setOnMouseClicked(mouseEvent -> {
                if (selectedPane != null) selectedPane.setEffect(null);
                selectedPane = stackPane;

                stackPane.setEffect(clickEffect);
                blockManager.updateCurrentBlock(block);
            });

            stackPane.setOnMouseEntered(event -> stackPane.setEffect(hoverEffect));
            stackPane.setOnMouseExited(event -> stackPane.setEffect(selectedPane == stackPane ? clickEffect : null));

            if (blockManager.currentBlock.getClass().getSimpleName().equals(block.getClass().getSimpleName())) {
                selectedPane = stackPane;
                stackPane.setEffect(clickEffect);
            }
        });
    }

}
