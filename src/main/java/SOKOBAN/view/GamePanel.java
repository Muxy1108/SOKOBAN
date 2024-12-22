package SOKOBAN.view;

import SOKOBAN.controller.GameController;
import SOKOBAN.model.Direction;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.model.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;

public class GamePanel {
    private final StackPane root; // Root pane to hold background and map
    private final VBox content;  // VBox for buttons and map
    private final HBox controlPanel; // Control panel for buttons
    private final GameController controller; // Controller for game logic
    private final GridPane gameGrid; // GridPane for the map

    public GamePanel(Stage stage, User user) {
        root = new StackPane();
        content = new VBox(20); // Vertical space between elements
        content.setAlignment(Pos.CENTER);

        controlPanel = new HBox(15);
        controlPanel.setAlignment(Pos.CENTER);

        gameGrid = new GridPane();

        MapMatrix map = loadGameOrLevel(user, 1); // Default level is 1
        controller = new GameController(gameGrid, map);

        setupControlPanel(stage, user);
        setBackgroundImage();
        content.getChildren().addAll(controlPanel, createCenteredGameGrid());

        root.getChildren().add(content);

        // Create the scene and set up key event handling
        Scene scene = new Scene(root, 800, 600);
        setupKeyEvents(); // Enable keyboard controls
        root.requestFocus(); // Request focus for root initially

        scene.setOnMouseClicked(event -> root.requestFocus()); // Regain focus on mouse click

        stage.setScene(scene);
    }

    /**
     * Adds a background image to the root layout.
     */
    private void setBackgroundImage() {
        Image backgroundImage = new Image("file:src/main/java/SOKOBAN/resources/images/game_background.png");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 600, false, false, false, false)
        );
        root.setBackground(new Background(bgImage));
    }

    /**
     * Loads the game state or initializes a specific level.
     */
    private MapMatrix loadGameOrLevel(User user, int level) {

        if (!user.isGuest() && new File(user.getSaveFileName()).exists()) {
            return MapMatrix.loadFromFile(user.getSaveFileName());
        } else {
            return MapMatrix.loadLevel(level);
        }
    }

    /**
     * Sets up the control panel with Save, Restart, and Load Level buttons.
     */
    private void setupControlPanel(Stage stage, User user) {
        // Create Save button
        Button saveButton = createStyledButton("Save", e -> {
            if (!user.isGuest()) {
                controller.saveGame(user.getUsername());
            } else {
                System.out.println("Guests cannot save games.");
            }
            root.requestFocus();
        });

        // Create Restart button
        Button restartButton = createStyledButton("Restart", e -> {
            controller.restartGame();
            root.requestFocus();
        });

        // Create Load Level dropdown
        ComboBox<Integer> levelDropdown = new ComboBox<>();
        levelDropdown.getItems().addAll(1, 2, 3, 4, 5); // Add levels (can be adjusted based on available levels)
        levelDropdown.setPromptText("Select Level");
        levelDropdown.setOnAction(e -> {
            Integer selectedLevel = levelDropdown.getValue();
            if (selectedLevel != null) {
                // Reload the selected level
                MapMatrix newMap = MapMatrix.loadLevel(selectedLevel);

                controller.restartGameWithNewMap(newMap);
                System.out.println("Loaded Level " + selectedLevel);
            }
            root.requestFocus();
        });

        controlPanel.getChildren().addAll(saveButton, restartButton, levelDropdown);
    }

    /**
     * Creates a styled button with the specified text and action.
     */
    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #6B8E23; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 10;");
        button.setOnAction(action);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #98FB98; -fx-text-fill: black; -fx-font-size: 16px; -fx-background-radius: 10;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #6B8E23; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 10;"));
        return button;
    }

    /**
     * Creates a centered game grid for better visibility and alignment.
     */
    private Pane createCenteredGameGrid() {
        StackPane centeredGridContainer = new StackPane();
        centeredGridContainer.setMaxWidth(400);
        centeredGridContainer.setMaxHeight(400);
        centeredGridContainer.setAlignment(Pos.CENTER);
        centeredGridContainer.getChildren().add(gameGrid);
        return centeredGridContainer;
    }

    /**
     * Sets up key event handling for hero movement.
     */
    private void setupKeyEvents() {
        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> controller.moveHero(Direction.UP);
                case DOWN -> controller.moveHero(Direction.DOWN);
                case LEFT -> controller.moveHero(Direction.LEFT);
                case RIGHT -> controller.moveHero(Direction.RIGHT);
            }
            root.requestFocus();
        });
    }

    /**
     * Returns the root layout for this panel.
     */
    public StackPane getRoot() {
        return root;
    }
}
