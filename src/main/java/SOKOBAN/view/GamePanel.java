package SOKOBAN.view;

import SOKOBAN.controller.GameController;
import SOKOBAN.model.Direction;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.model.User;

import java.io.File;

public class GamePanel {
    private final VBox root;
    private final HBox controlPanel;
    private final GameController controller;

    public GamePanel(Stage stage, User user) {
        root = new VBox();
        controlPanel = new HBox(10);

        MapMatrix map = loadGameOrLevel(user);
        controller = new GameController(new javafx.scene.layout.GridPane(), map);

        setupControlPanel(stage, user);
        root.getChildren().addAll(controlPanel, controller.getGridPane());

        Scene scene = new Scene(root, 800, 600);
        setupKeyEvents(scene);

        stage.setScene(scene);
    }

    private MapMatrix loadGameOrLevel(User user) {
        if (!user.isGuest() && new File(user.getSaveFileName()).exists()) {
            return MapMatrix.loadFromFile(user.getSaveFileName());
        }
        else {
            return MapMatrix.loadLevel(1);
        }
    }

    private void setupControlPanel(Stage stage, User user) {
        Button saveButton = new Button("Save");
        Button restartButton = new Button("Restart");

        saveButton.setOnAction(e -> {
            if (!user.isGuest()) {
                File file = new File("src/main/java/SOKOBAN/resources/saves/" + user.getUsername() + ".txt");
                /*boolean exists = file.exists();
                if (!exists) {
                    file.createNewFile();
                }*/
                controller.saveGame(user.getUsername());
            } else {
                System.out.println("Guests cannot save games.");
            }
        });

        restartButton.setOnAction(e -> controller.restartGame());

        controlPanel.getChildren().addAll(saveButton, restartButton);
    }

    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> controller.moveHero(Direction.UP);
                case DOWN -> controller.moveHero(Direction.DOWN);
                case LEFT -> controller.moveHero(Direction.LEFT);
                case RIGHT -> controller.moveHero(Direction.RIGHT);
            }
        });
    }

    public VBox getRoot() {
        return root;
    }
}
