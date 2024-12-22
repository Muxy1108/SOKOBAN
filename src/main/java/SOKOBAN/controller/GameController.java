package SOKOBAN.controller;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import SOKOBAN.model.Direction;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.view.Box;
import SOKOBAN.view.GridComponent;
import SOKOBAN.view.Hero;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GameController {

    private final GridPane grid;  // The GridPane for displaying the game
    private final MapMatrix model;  // The underlying game matrix model
    private Hero hero;  // The hero object representing the player
    private Box[][] boxes;  // 2D array to track boxes' positions

    public GridPane getGridPane() {
        return grid;
    }



    public GameController(GridPane grid, MapMatrix model) {
        this.grid = grid;
        this.model = model;
        this.boxes = new Box[model.getMatrix().length][model.getMatrix()[0].length]; // Initialize box grid
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
     * Updates the grid component without clearing the whole grid.
     * This is for efficient updates during hero movement or box push.
     */
    private void updateGridComponent(int oldRow, int oldCol, int newRow, int newCol) {
        int[][] matrix = model.getMatrix();
        GridComponent newCell;

        // Update hero position
        if (matrix[newRow][newCol] == 5) {
            newCell = new GridComponent(40, Color.BLUE);
            grid.add(newCell, newCol, newRow);
        }

        // Update box position
        if (matrix[newRow][newCol] == 3) {
            newCell = new GridComponent(40, Color.BROWN);
            grid.add(newCell, newCol, newRow);
        }

        // Update old position (might be empty, wall, or target)
        newCell = new GridComponent(40, getColorForCell(matrix[oldRow][oldCol]));
        grid.add(newCell, oldCol, oldRow);  // Revert the old cell to its appropriate color
    }
    /**
     * Returns the color based on the matrix cell type.
     */
    private Color getColorForCell(int cellType) {
        switch (cellType) {
            case 1: return Color.DARKGRAY;  // Wall
            case 4: return Color.YELLOW;  // Target
            case 2: return Color.WHITE;  // Empty space
            default: return Color.WHITE;
        }
    }


    /**
     * Creates a GridComponent for the specified cell type and adds it to the grid.
     */
    private GridComponent createGridCell(int cellType, int row, int col) {
        GridComponent cell;
        switch (cellType) {
            case 1:  // Wall
                cell = new GridComponent(40, Color.DARKGRAY);
                break;
            case 4:  // Target
                cell = new GridComponent(40, Color.YELLOW);
                break;
            case 3:  // Box
                cell = new GridComponent(40, Color.BROWN);
                boxes[row][col] = new Box(row, col);  // Store Box object
                break;
            case 5:  // Hero
                cell = new GridComponent(40, Color.BLUE);
                hero = new Hero(row, col);  // Initialize Hero object
                break;
            case 2:  // Empty space
            default:
                cell = new GridComponent(40, Color.WHITE);
                break;
        }
        return cell;
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
            System.out.println("Congratulations! You have completed the puzzle!");
        } else if (isFail(matrix)) {
            System.out.println("Game over! A box is stuck and the puzzle is unsolvable.");
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

            printMapMatrix();

            hero.move(targetRow, targetCol);  // Update hero position

            // Update grid efficiently
            updateGridComponent(targetRow, targetCol, boxTargetRow, boxTargetCol);  // Update box
            updateGridComponent(currentRow, currentCol, targetRow, targetCol);  // Update hero
        }
    }

    private void moveHeroToEmptyOrTarget(int[][] matrix, int currentRow, int currentCol, int targetRow, int targetCol) {
        matrix[currentRow][currentCol] = model.getInitialMatrix()[currentRow][currentCol] == 4 ? 4 : 2;
        printMapMatrix();
        printInitialMapMatrix();
        matrix[targetRow][targetCol] = 5;  // Set new hero position to blue
        hero.move(targetRow, targetCol);  // Update hero's position

        // Update grid efficiently without clearing the entire grid
        updateGridComponent(currentRow, currentCol, targetRow, targetCol);  // Update hero's new position
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
                    return false;  // A box is not on a target
                }
            }
        }
        return true;  // All boxes are on targets
    }

    /**
     * Determines if the game has failed (a box is stuck and the game is unsolvable).
     */
    private boolean isFail(int[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] == 3 && !canBoxMove(row, col, matrix)) {
                    if (model.getInitialMatrix()[row][col] != 4) {
                        return true;  // A box is stuck and not on a target
                    }
                }
            }
        }
        return false;  // No boxes are stuck
    }

    /**
     * Checks if a box can move to any adjacent empty space or target.
     */
    private boolean canBoxMove(int row, int col, int[][] matrix) {
        for (Direction direction : Direction.values()) {
            int newRow = row + direction.getRowOffset();
            int newCol = col + direction.getColOffset();
            if (isValidMove(newRow, newCol, matrix) && (matrix[newRow][newCol] == 2 || matrix[newRow][newCol] == 4)) {
                return true;  // Box can move
            }
        }
        return false;  // Box cannot move
    }


    /**
     * Restarts the game by reinitializing the grid.
     */
    public void printInitialMapMatrix() {
        int[][] matrix = model.getInitialMatrix();  // Get the game matrix
        System.out.println("Initial Mapmatrix:");
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
        System.out.println();
    }
    public void printMapMatrix() {
        int[][] matrix = model.getMatrix();  // Get the game matrix
        System.out.println("Map matrix:");
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
        System.out.println();
    }


    /**
     * Saves the current game state to a file.
     *
     * @param filename The name of the file to save to.
     */
    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/main/java/SOKOBAN/resources/saves/" + filename + ".dat"))) {
            out.writeObject(model.getMatrix());  // Save the game matrix
            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Restarts the game by reinitializing the grid.
     */
    public void restartGame() {
        model.loadLevel(model.loadNum);
        //model.setMatrix(model.getInitialMatrix()); // Reset the model to its initial state
        initializeGame();
        System.out.println("Game restarted.");
    }
    public void restartGameWithNewMap(MapMatrix newMap) {

        model.setMatrix(newMap.getMatrix());
        model.setInitialMatrix(newMap.getMatrix()); // Replace the model's matrix with the new level's matrix
        initializeGame(); // Reinitialize the game grid
    }

}
