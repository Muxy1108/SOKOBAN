package SOKOBAN.view;

import SOKOBAN.controller.GameController;
import SOKOBAN.model.Direction;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.model.User;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
/*import javafx.scene.media;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;*/



import javax.print.attribute.standard.Media;
import java.io.File;
import java.util.Optional;

public class GamePanel {
    private final StackPane root; // Root pane to hold background and map
    private final VBox content;  // VBox for buttons and map
    private final HBox controlPanel; // Control panel for buttons
    private final HBox infoPanel; // Info panel for displaying step counter
    private final GameController controller; // Controller for game logic
    private final GridPane gameGrid; // GridPane for the map
    private final Label stepCounterLabel; // Step counter label
    private int steps; // Step counter variable
    private Sound sound;//private MediaPlayer musicPlayer;

    public GamePanel(Stage stage, User user) {
        root = new StackPane();
        content = new VBox(20); // Vertical space between elements
        content.setAlignment(Pos.CENTER);

        controlPanel = new HBox(15);
        controlPanel.setAlignment(Pos.CENTER);

        infoPanel = new HBox();
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setSpacing(10);

        gameGrid = new GridPane();

        MapMatrix map = loadGameOrLevel(user, 1); // Default level is 1
        controller = new GameController(gameGrid, map, this); // Pass `this` for animation callbacks

        // Step counter initialization
        steps = 0;
        stepCounterLabel = new Label("Steps: 0");
        stepCounterLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        setupControlPanel(stage, user);
        setBackgroundImage();
        infoPanel.getChildren().add(stepCounterLabel); // Add step counter to info panel
        content.getChildren().addAll(infoPanel, controlPanel, createCenteredGameGrid());

        root.getChildren().add(content);

        // Create the scene and set up key event handling
        Scene scene = new Scene(root, 800, 600);
        setupKeyEvents(); // Enable keyboard controls
        root.requestFocus(); // Request focus for root initially

        scene.setOnMouseClicked(event -> root.requestFocus()); // Regain focus on mouse click

        stage.setScene(scene);

        sound = new Sound();
        sound.setMusic("nor.mid"); // Set the gameplay music file
        sound.loadSound(); // Start playing the music

        //playMusic("src/main/java/SOKOBAN/resources/audio/gameplay.mp3");
    }

    /**
     * Plays a music file.
     *
     * @param filePath The path to the music file.
     */
    /*private void playMusic(String filePath) {
        if (musicPlayer != null) {
            musicPlayer.stop(); // Stop any existing music
        }

        Media media = new Media(new File(filePath).toURI().toString());
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
        musicPlayer.play();

    }*/

    /**
     * Stops the current music.
     */
    /*private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
    */

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
     * Sets up the control panel with Save, Restart, Load Level, and Start with Saves buttons.
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
            resetStepCounter(); // Reset step counter
            root.requestFocus();
        });

        // Create Load Level dropdown
        Button loadLevelButton = createStyledButton("Load Level", e -> {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Load Level");
            dialog.setHeaderText("Enter the level number to load:");
            dialog.setContentText("Level:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(level -> {
                try {
                    int levelNumber = Integer.parseInt(level);
                    MapMatrix newMap = MapMatrix.loadLevel(levelNumber);

                    controller.restartGameWithNewMap(newMap);
                    resetStepCounter(); // Reset step counter
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid level number.");
                }
            });
            root.requestFocus();
        });

        // Create Start with Saves button
        Button startWithSavesButton = createStyledButton("Start with Saves", e -> {
            TextInputDialog dialog = new TextInputDialog("save_file");
            dialog.setTitle("Load Saved Game");
            dialog.setHeaderText("Enter the saved filename to load:");
            dialog.setContentText("Filename:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(filename -> {
                File saveFile = new File("src/main/java/SOKOBAN/resources/saves/" + filename + ".txt");
                if (saveFile.exists()) {
                    MapMatrix savedMap = MapMatrix.loadFromFile("src/main/java/SOKOBAN/resources/saves/" + filename + ".txt");
                    controller.restartGameWithNewMap(savedMap);
                    resetStepCounter(); // Reset step counter
                } else {
                    System.out.println("Save file not found.");
                }
            });
            root.requestFocus();
        });
        Button returnButton = createStyledButton("Undo Step", e -> {
            controller.returnToLastStep(); // Call the returnToLastStep method
            steps--; // Decrement step counter
            if (steps < 0) steps = 0; // Ensure step count doesn't go negative
            stepCounterLabel.setText("Steps: " + steps);
            root.requestFocus();
        });


        controlPanel.getChildren().addAll(saveButton, restartButton, loadLevelButton, startWithSavesButton,returnButton);
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
                case UP, DOWN, LEFT, RIGHT -> {
                    controller.moveHero(Direction.valueOf(event.getCode().name()));
                    incrementStepCounter(); // Increment step counter when hero moves
                }
            }
            root.requestFocus();
        });
    }

    /**
     * Resets the step counter.
     */
    private void resetStepCounter() {
        steps = 0;
        stepCounterLabel.setText("Steps: 0");
    }

    /**
     * Increments the step counter and updates the label.
     */
    private void incrementStepCounter() {
        steps++;
        stepCounterLabel.setText("Steps: " + steps);
    }

    /**
     * Displays a success animation overlay.
     */
    public void showSuccessAnimation() {
        sound.mystop();
        sound.setMusic("success.mid"); // Set success music file
        sound.loadSound();
        Label successLabel = new Label("Congratulations! You Won!");
        successLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        successLabel.setStyle("-fx-text-fill: green;");

        FadeTransition fade = new FadeTransition(Duration.seconds(3), successLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> root.getChildren().remove(successLabel));

        root.getChildren().add(successLabel);
        fade.play();
    }

    /**
     * Displays a fail animation overlay.
     */
    public void showFailAnimation() {

        sound.mystop();
        sound.setMusic("fail.mid"); // Set fail music file
        sound.loadSound();

        Label failLabel = new Label("Game Over! A Box is Stuck!");
        failLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        failLabel.setStyle("-fx-text-fill: red;");

        FadeTransition fade = new FadeTransition(Duration.seconds(3), failLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> root.getChildren().remove(failLabel));

        root.getChildren().add(failLabel);
        fade.play();
    }


    /**
     * Returns the root layout for this panel.
     */
    public StackPane getRoot() {
        return root;
    }
}
