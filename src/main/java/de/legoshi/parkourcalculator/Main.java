package de.legoshi.parkourcalculator;

public class Main extends Application {

    public static void main(String[] args) {
        Application.main(args);
    }

}

/*    public class Main extends Application {

        @Override
        public void start(Stage primaryStage) {
            // Create the expandable tabs using TitledPane
            TitledPane tab1 = new TitledPane("Tab 1", new Label("Content for Tab 1"));
            TitledPane tab2 = new TitledPane("Tab 2", new Label("Content for Tab 2"));
            TitledPane tab3 = new TitledPane("Tab 3", new Label("Content for Tab 3"));

            // Create the Accordion and add the TitledPanes to it
            Accordion accordion = new Accordion();
            accordion.getPanes().addAll(tab1, tab2, tab3);

            // Create a VBox and add the Accordion to it
            VBox sideBar = new VBox(accordion);

            // Create a BorderPane and add the sideBar to the left side
            BorderPane root = new BorderPane();
            root.setLeft(sideBar);

            // Create the Scene and set it on the Stage
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("JavaFX Side-bar Menu");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
*/
