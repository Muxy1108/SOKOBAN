package SOKOBAN;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import SOKOBAN.view.LoginPanel;

public class SokobanApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginPanel loginPanel = new LoginPanel(primaryStage);

        Scene scene = new Scene(loginPanel.getRoot(), 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sokoban Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
