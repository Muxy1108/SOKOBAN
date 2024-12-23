package SOKOBAN.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import SOKOBAN.model.Direction;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.view.Box;
import SOKOBAN.view.GamePanel;
import SOKOBAN.view.GridComponent;
import SOKOBAN.view.Hero;

import java.io.*;
import java.util.Stack;

public class GameController {

    private final GridPane grid;  // The GridPane for displaying the game
    private final MapMatrix model;  // The underlying game matrix model
    private final GamePanel gamePanel;  // Reference to the GamePanel for animations
    private Hero hero;  // The hero object representing the player
    private Box[][] boxes;  // 2D array to track boxes' positions
    private final Stack<int[][]> stateStack; // Stack to track previous states

    // Paths to the images
    private final Image wallImage = new Image("file:src/main/java/SOKOBAN/resources/images/wall.png");
    private final Image targetImage = new Image("file:src/main/java/SOKOBAN/resources/images/target.png");
    private final Image boxImage = new Image("file:src/main/java/SOKOBAN/resources/images/box.png");
    private final Image heroImage = new Image("file:src/main/java/SOKOBAN/resources/images/hero.png");
    private final Image emptyImage = new Image("file:src/main/java/SOKOBAN/resources/images/empty.png");

    public GridPane getGridPane() {
        return grid;
    }

    public GameController(GridPane grid, MapMatrix model, GamePanel gamePanel) {
        this.grid = grid;
        this.model = model;
        this.gamePanel = gamePanel;
        this.boxes = new Box[model.getMatrix().length][model.getMatrix()[0].length]; // Initialize box grid
        this.stateStack = new Stack<>(); // Initialize the state stack
        initializeGame();
    }

    /**
     * Initializes the game by building the grid from the current state of the matrix.
     */
    private void initializeGame() {
        grid.getChildren().clear();  // Clear existing grid components
        int[][] matrix = model.getMatrix(); // Get the current game state
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                GridComponent cell = createGridCell(matrix[row][col], row, col);
                grid.add(cell, col, row);  // Add cell to the grid
            }
        }
    }

    /**
     * Saves the current game state to the stack.
     */
    private void saveState() {
        int[][] currentState = model.getMatrix();
        int[][] copy = new int[currentState.length][currentState[0].length];

        // Deep copy the current state
        for (int i = 0; i < currentState.length; i++) {
            System.arraycopy(currentState[i], 0, copy[i], 0, currentState[i].length);
        }

        stateStack.push(copy);
    }
    public void returnToLastStep() {
        if (!stateStack.isEmpty()) {
            int[][] previousState = stateStack.pop(); // Get the last state
            model.setMatrix(previousState); // Update the model with the previous state
            initializeGame(); // Reinitialize the grid
        } else {
            System.out.println("No previous step to return to!");
        }
    }
    /**
     * Creates a GridComponent for the specified cell type and adds it to the grid.
     */
    private GridComponent createGridCell(int cellType, int row, int col) {
        ImageView imageView;
        switch (cellType) {
            case 1:  // Wall
                imageView = new ImageView(wallImage);
                break;
            case 4:  // Target
                imageView = new ImageView(targetImage);
                break;
            case 3:  // Box
                imageView = new ImageView(boxImage);
                boxes[row][col] = new Box(row, col);  // Store Box object
                break;
            case 5:  // Hero
                imageView = new ImageView(heroImage);
                hero = new Hero(row, col);  // Initialize Hero object
                break;
            case 2:  // Empty space
            default:
                imageView = new ImageView(emptyImage);
                break;
        }
        imageView.setFitWidth(40);  // Resize the image
        imageView.setFitHeight(40);
        return new GridComponent(40, imageView);
    }

    /**
     * Handles hero movement in the specified direction.
     */
    public void moveHero(Direction direction) {
        if (hero == null) return;  // If there's no hero, do nothing

        int currentRow = hero.getRow();
        int currentCol = hero.getCol();
        int targetRow = currentRow + direction.getRowOffset();
        int targetCol = currentCol + direction.getColOffset();

        int[][] matrix = model.getMatrix();

        // Ensure the move is within bounds and the target is valid
        if (isValidMove(targetRow, targetCol, matrix)) {
            if (matrix[targetRow][targetCol] == 2 || matrix[targetRow][targetCol] == 4) {
                // Move hero to empty space or target
                moveHeroToEmptyOrTarget(matrix, currentRow, currentCol, targetRow, targetCol);
            } else if (matrix[targetRow][targetCol] == 3) {
                // Push box if the target is a box
                handleBoxPush(direction, currentRow, currentCol, targetRow, targetCol);
            }
        }

        // Check success or fail conditions
        if (isSuccess(matrix)) {
            gamePanel.showSuccessAnimation(); // Trigger success animation
            saveState();
        } else if (isFail(matrix)) {
            gamePanel.showFailAnimation(); // Trigger fail animation
        }
    }

    private void handleBoxPush(Direction direction, int currentRow, int currentCol, int targetRow, int targetCol) {
        int[][] matrix = model.getMatrix();
        int boxTargetRow = targetRow + direction.getRowOffset();
        int boxTargetCol = targetCol + direction.getColOffset();

        // Ensure the box is being pushed to a valid location (empty space or target)
        if (isValidMove(boxTargetRow, boxTargetCol, matrix) && (matrix[boxTargetRow][boxTargetCol] == 2 || matrix[boxTargetRow][boxTargetCol] == 4)) {
            // Move the box
            matrix[boxTargetRow][boxTargetCol] = 3;  // New box position
            matrix[targetRow][targetCol] = 5;  // Hero's new position

            // Clear hero's old position
            matrix[currentRow][currentCol] = model.getInitialMatrix()[currentRow][currentCol] == 4 ? 4 : 2;
            hero.move(targetRow, targetCol);// Update hero position

            printMatrix();
            printInitialMapMatrix();

            // Update grid efficiently
            initializeGame(); // Refresh the game grid
        }
    }

    private void moveHeroToEmptyOrTarget(int[][] matrix, int currentRow, int currentCol, int targetRow, int targetCol) {
        matrix[currentRow][currentCol] = model.getInitialMatrix()[currentRow][currentCol] == 4 ? 4 : 2;
        matrix[targetRow][targetCol] = 5;  // Set new hero position to blue
        hero.move(targetRow, targetCol);  // Update hero's position
        printMatrix();
        printInitialMapMatrix();
        saveState();
        // Update grid efficiently without clearing the entire grid
        initializeGame(); // Refresh the game grid
    }

    /**
     * Checks if the target position is valid (inside bounds and not a wall).
     */
    private boolean isValidMove(int row, int col, int[][] matrix) {
        return row >= 0 && row < matrix.length && col >= 0 && col < matrix[0].length &&
                matrix[row][col] != 1;  // Not a wall
    }

    /**
     * Determines if the game is successfully completed.
     */
    private boolean isSuccess(int[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == 4) {
                    return false;  // A target is empty
                }
            }
        }
        return true;  // All boxes are on targets
    }

    /**
     * Determines if the game has failed (a box is stuck and the game is unsolvable).
     */
    private boolean isFail(int[][] matrix) {
        int cannotmovecounter = 0;
        int boxcounter = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == 3) {
                    boxcounter++;
                    if(!canBoxMove(row, col, matrix)){
                        System.out.println("row = " + row + ", col = " + col);
                        cannotmovecounter++;
                        /*if (model.getInitialMatrix()[row][col] != 4) {
                            /*System.out.println("Initial: " + "row = " + row + ", col = " + col + " is " + model.getInitialMatrix()[row][col]);
                            printMatrix();
                            printInitialMapMatrix();*/
                            //System.out.println("isFail");

                        // A box is stuck and not on a target
                        //}
                    }
                }
            }
        }
        if(cannotmovecounter == boxcounter) return true;
        else return false;  // No boxes are stuck
    }

    /**
     * Checks if a box can move to any adjacent empty space or target.
     */
    private boolean canBoxMove(int row, int col, int[][] matrix) {
        //need to be modified
        System.out.println("matrix row + 1 : " + matrix[row + 1][col]);
        System.out.println("matrix row - 1 : " + matrix[row - 1][col]);
        if(matrix[row + 1][col] == 2 || matrix[row + 1][col] == 5 || matrix[row + 1][col] == 4 ){
            if(matrix[row - 1][col] == 2 || matrix[row - 1][col] == 5 || matrix[row - 1][col] == 4 ){
                System.out.println("canBoxMove");
                return true;
            }
        }
        if(matrix[row][col + 1] == 2 || matrix[row][col + 1] == 5 || matrix[row][col + 1] == 4 ){
            if(matrix[row][col - 1] == 2 || matrix[row][col - 1] == 5 || matrix[row][col - 1] == 4 ){
                return true;
            }
        }
        return false;
        /*for (Direction direction : Direction.values()) {
            int newRow = row + direction.getRowOffset();
            int newCol = col + direction.getColOffset();
            if (isValidMove(newRow, newCol, matrix) && (matrix[newRow][newCol] == 2 || matrix[newRow][newCol] == 4)) {
                return true;  // Box can move
            }
        }*/

    }

    /**
     * Restarts the game by reinitializing the grid.
     */
    public void restartGame() {
        model.setMatrix(model.getInitialMatrix()); // Reset the model to its initial state
        initializeGame();
        System.out.println("Game restarted.");
    }

    /**
     * Restarts the game with a new map.
     */
    public void restartGameWithNewMap(MapMatrix newMap) {
        model.setMatrix(newMap.getMatrix());
        model.setInitialMatrix(newMap.getInitialMatrix());
        initializeGame(); // Reinitialize the game grid
    }

    /**
     * Saves the current game state to a file.
     */
    public void saveGame(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/SOKOBAN/resources/saves/" + filename + ".txt"))) {
            int[][] matrix = model.getMatrix();
            for (int row = 0; row < matrix.length; row++) {
                for (int col = 0; col < matrix[row].length; col++) {
                    writer.write(matrix[row][col] + " "); // Write each cell separated by a space
                }
                writer.newLine(); // Move to the next line after each row
            }
            System.out.println("Game saved to " + filename + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printMatrix() {
        int[][] matrix = model.getMatrix();  // Get the game matrix
        System.out.println("Mapmatrix : ");
        // Iterate through each row of the matrix
        for (int row = 0; row < matrix.length; row++) {
            // Iterate through each column in the current row
            for (int col = 0; col < matrix[row].length; col++) {
                // Print the matrix element, followed by a space
                System.out.print(matrix[row][col] + " ");
            }
            // After each row, print a new line for the next row
            System.out.println();
        }
    }
    public void printInitialMapMatrix() {
        int[][] matrix = model.getInitialMatrix();  // Get the game matrix
        System.out.println("Initial Matrix : ");
        // Iterate through each row of the matrix
        for (int row = 0; row < matrix.length; row++) {
            // Iterate through each column in the current row
            for (int col = 0; col < matrix[row].length; col++) {
                // Print the matrix element, followed by a space
                System.out.print(matrix[row][col] + " ");
            }
            // After each row, print a new line for the next row
            System.out.println();
        }
    }

}
