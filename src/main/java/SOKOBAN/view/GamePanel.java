package SOKOBAN.view;

import SOKOBAN.controller.GameController;
import SOKOBAN.model.Direction;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.model.User;

import java.io.File;

public class GamePanel {
    private final StackPane root; // Root pane to hold background and map
    private final VBox content;  // VBox for buttons and map
    private final HBox controlPanel; // Control panel for buttons
    private final GameController controller; // Controller for game logic
    private final GridPane gameGrid; // GridPane for the map

    public GamePanel(Stage stage, User user) {
        root = new StackPane(); // Root layout with a background layer
        content = new VBox();  // Content layout for buttons and map
        content.setAlignment(Pos.CENTER); // Center the content vertically and horizontally
        content.setSpacing(20); // Add spacing between control panel and game grid

        controlPanel = new HBox(15); // Horizontal box for control panel buttons
        controlPanel.setAlignment(Pos.CENTER); // Center the control buttons horizontally

        // Create the game grid
        gameGrid = new GridPane();

        // Load the game map and initialize the controller
        MapMatrix map = loadGameOrLevel(user);
        controller = new GameController(gameGrid, map);

        // Set up the control panel (Save and Restart buttons)
        setupControlPanel(stage, user);

        // Add background image
        setBackgroundImage();

        // Add control panel and game grid to the content layout
        content.getChildren().addAll(controlPanel, createCenteredGameGrid());

        // Add content layout to the root layout
        root.getChildren().add(content);

        // Create the scene and set up key event handling
        Scene scene = new Scene(root, 800, 600);
        setupKeyEvents(); // Enable keyboard controls
        root.requestFocus(); // Request focus for root initially

        // Set up mouse click to refocus on the root if necessary
        scene.setOnMouseClicked(event -> root.requestFocus()); // Regain focus on mouse click

        /*scene.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                root.requestFocus(); // Ensure focus stays on root if lost
            }
        });*/

        // Set the scene on the stage
        stage.setScene(scene);
    }

    /**
     * Adds a background image to the root layout.
     */
    private void setBackgroundImage() {
        // Load the background image (ensure the file exists in the resources/images directory)
        Image backgroundImage = new Image("file:src/main/java/SOKOBAN/resources/images/game_background.jpg");

        // Create a background with the image
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 600, false, false, false, false) // Fixed background size
        );

        // Set the background on the root layout
        root.setBackground(new Background(bgImage));
    }

    /**
     * Loads the game state or initializes a new level if no save file is found.
     */
    private MapMatrix loadGameOrLevel(User user) {
        if (!user.isGuest() && new File(user.getSaveFileName()).exists()) {
            return MapMatrix.loadFromFile(user.getSaveFileName());
        } else {
            return MapMatrix.loadLevel(1);
        }
    }

    /**
     * Sets up the control panel with Save and Restart buttons.
     */
    private void setupControlPanel(Stage stage, User user) {
        // Create styled Save button
        Button saveButton = createStyledButton("Save");
        saveButton.setOnAction(e -> {
            if (!user.isGuest()) {
                controller.saveGame(user.getUsername());
            } else {
                System.out.println("Guests cannot save games.");
            }
            root.requestFocus(); // Regain focus after clicking
        });

        // Create styled Restart button
        Button restartButton = createStyledButton("Restart");
        restartButton.setOnAction(e -> {
            controller.restartGame();
            root.requestFocus(); // Regain focus after clicking
        });

        controlPanel.getChildren().addAll(saveButton, restartButton);
    }

    /**
     * Creates a centered game grid for better visibility and alignment.
     */
    private Pane createCenteredGameGrid() {
        StackPane centeredGridContainer = new StackPane();
        centeredGridContainer.setMaxWidth(400); // Set maximum width for the grid
        centeredGridContainer.setMaxHeight(400); // Set maximum height for the grid
        centeredGridContainer.setAlignment(Pos.CENTER); // Center the grid within the container
        centeredGridContainer.getChildren().add(gameGrid); // Add the grid to the container
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
            root.requestFocus(); // Regain focus after key press
        });
    }

    /**
     * Creates a styled button for the control panel.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #6B8E23; " + // Green background
                "-fx-text-fill: white; " +         // White text
                "-fx-font-size: 16px; " +         // Font size
                "-fx-padding: 10 20; " +          // Padding
                "-fx-background-radius: 10;");   // Rounded corners
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #98FB98; -fx-text-fill: black; -fx-font-size: 16px; -fx-background-radius: 10;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #6B8E23; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 10;"));
        return button;
    }

    /**
     * Returns the root layout for this panel.
     */
    public StackPane getRoot() {
        return root;
    }
}
