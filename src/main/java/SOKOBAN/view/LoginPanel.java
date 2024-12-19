package SOKOBAN.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import SOKOBAN.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LoginPanel {
    private final StackPane root; // To allow layering of background and UI elements

    public LoginPanel(Stage stage) {
        root = new StackPane();

        // Set the background image
        ImageView background = new ImageView(new Image("file:src/main/java/SOKOBAN/resources/images/login_background.png"));
        background.setFitWidth(800);
        background.setFitHeight(600);
        background.setPreserveRatio(true);

        // Main container for login UI elements
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);

        // Custom font (ensure the font file is available in your resources/fonts folder)
        Font customFont = Font.loadFont("file:src/main/java/SOKOBAN/resources/fonts/StardewFont.ttf", 18);

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setMaxWidth(300);
        usernameField.setFont(customFont);

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(300);
        passwordField.setFont(customFont);

        // Buttons
        Button loginButton = createStyledButton("Login", customFont);
        Button registerButton = createStyledButton("Register", customFont);
        Button guestButton = createStyledButton("Play as Guest", customFont);

        // Button actions
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (!username.isEmpty() && isUserExists(username)) {
                File PasswordFile = new File("src/main/java/SOKOBAN/resources/users/" + username + ".txt");
                //BufferedReader reader = new BufferedReader(new FileReader(filePath));
                User user = new User(username, false,PasswordFile);
                if (user.PasswordCorrect(password)) {
                    GamePanel gamePanel = new GamePanel(stage, user);
                    stage.getScene().setRoot(gamePanel.getRoot());
                } else {
                    System.out.println("Password is incorrect.");
                }
            } else {
                System.out.println("User not found. Please register first.");
            }
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (!username.isEmpty() && !isUserExists(username)) {
                registerUser(username, password);
                System.out.println("User registered successfully.");
            } else {
                System.out.println("Username is invalid or already exists.");
            }
        });

        guestButton.setOnAction(e -> {
            User guest = new User("Guest", true,null);
            GamePanel gamePanel = new GamePanel(stage, guest);
            stage.getScene().setRoot(gamePanel.getRoot());
        });

        // Add all UI elements to the content box
        content.getChildren().addAll(usernameField, passwordField, loginButton, registerButton, guestButton);

        // Add background and content to the root
        root.getChildren().addAll(background, content);
    }

    public static String getPassword(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("Password:")) {
                    return line.substring("Password:".length()).trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isUserExists(String username) {
        File userFile = new File("src/main/java/SOKOBAN/resources/users/" + username + ".txt");
        return userFile.exists();
    }

    private void registerUser(String username, String password) {
        try {
            File userFile = new File("src/main/java/SOKOBAN/resources/users/" + username + ".txt");
            userFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(userFile)) {
                writer.write(password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);
        button.setStyle("-fx-background-color: #6B8E23; " + // Stardew green style
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 15; " +
                "-fx-font-size: 16;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #98FB98; -fx-text-fill: black; -fx-background-radius: 15; -fx-font-size: 16;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #6B8E23; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 16;"));
        return button;
    }

    public StackPane getRoot() {
        return root;
    }
}
