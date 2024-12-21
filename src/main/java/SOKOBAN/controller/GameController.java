package SOKOBAN.controller;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import SOKOBAN.model.Direction;
import SOKOBAN.model.MapMatrix;
import SOKOBAN.view.Box;
import SOKOBAN.view.GridComponent;
import SOKOBAN.view.Hero;

import java.io.*;

public class GameController {
    private final GridPane grid; // The GridPane for displaying the game
    private final MapMatrix model; // The underlying game matrix model
    private Hero hero; // The hero object representing the player
    private Box[][] boxes; // 2D array to track boxes' positions

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
    public void initializeGame() {
        grid.getChildren().clear(); // Clear existing grid components
        int[][] matrix = model.getMatrix();

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                GridComponent cell;
                switch (matrix[row][col]) {
                    case 1 -> { // Wall
                        cell = new GridComponent(40, Color.DARKGRAY);
                    }
                    case 4 -> { // Target
                        cell = new GridComponent(40, Color.YELLOW);
                    }
                    case 3 -> { // Box
                        cell = new GridComponent(40, Color.BROWN);
                        // Add a Box object and store it in the boxes array
                        boxes[row][col] = new Box(row, col);
                    }
                    case 5 -> { // Hero
                        cell = new GridComponent(40, Color.BLUE);
                        // Add the hero object
                        hero = new Hero(row, col);
                    }
                    case 2 -> {
                        cell = new GridComponent(40, Color.WHITE);
                    }
                    default -> { // Empty space
                        cell = new GridComponent(40, Color.BLACK);
                    }
                }
                grid.add(cell, col, row); // Add cell to the grid
            }
        }
    }

    /**
     * Handles hero movement in the specified direction.
     */
    public void moveHero(Direction direction) {

        //if (hero == null) return; // If there's no hero, do nothing

        int currentRow = hero.getRow();
        int currentCol = hero.getCol();
        System.out.println("Current position: (" + currentRow + ", " + currentCol + ")");
        int targetRow = currentRow + direction.getRowOffset();
        int targetCol = currentCol + direction.getColOffset();
        System.out.println("Target position: (" + targetRow + ", " + targetCol + ")");

        int[][] matrix = model.getMatrix();

        // Ensure the move is within bounds
        if (targetRow < 0 || targetRow >= matrix.length || targetCol < 0 || targetCol >= matrix[0].length) {
            return; // Don't allow moves out of bounds
        }

        // Handle hero movement: Move to empty space or target
        if (matrix[targetRow][targetCol] == 2 || matrix[targetRow][targetCol] == 4) { // Move to empty space or target
            // Preserve target status (don't overwrite a target)

            matrix[currentRow][currentCol] = 2; // Set old hero position to empty space


            matrix[targetRow][targetCol] = 5; // Set new hero position
            hero.move(targetRow, targetCol); // Update hero position
            updateGridComponent(currentRow, currentCol, targetRow, targetCol); // Update grid without clearing it
        } else if (matrix[targetRow][targetCol] == 3) { // Push box
            // Compute the row and column of the cell after the box
            int boxTargetRow = targetRow + direction.getRowOffset();
            int boxTargetCol = targetCol + direction.getColOffset();

            // Ensure the target cell after the box is within bounds
            if (boxTargetRow < 0 || boxTargetRow >= matrix.length || boxTargetCol < 0 || boxTargetCol >= matrix[0].length) {
                return; // Don't attempt to push outside the bounds
            }

            // Check if the cell after the box is empty or a target (where the box can be pushed)
            if (matrix[boxTargetRow][boxTargetCol] == 2 || matrix[boxTargetRow][boxTargetCol] == 4) {
                // Move the box to the new position
                matrix[boxTargetRow][boxTargetCol] = 3; // New box position
                matrix[targetRow][targetCol] = 5; // Hero's new position
                matrix[currentRow][currentCol] = 2; // Clear hero's old position
                hero.move(targetRow, targetCol); // Update hero position

                // Move the box object in the box array
                boxes[boxTargetRow][boxTargetCol] = boxes[targetRow][targetCol]; // Move box to new position
                boxes[targetRow][targetCol] = null; // Remove box from old position

                // Update grid efficiently without clearing the entire grid
                updateGridComponent(currentRow, currentCol, targetRow, targetCol); // Update hero's new position
                updateGridComponent(targetRow, targetCol, boxTargetRow, boxTargetCol); // Update box's new position


            }
            System.out.println("Current position: (" + currentRow + ", " + currentCol + ")");
            System.out.println("Target position: (" + targetRow + ", " + targetCol + ")");
            System.out.println("Box target position: (" + boxTargetRow + ", " + boxTargetCol + ")");

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
            newCell = new GridComponent(40, Color.BLUE); // Hero cell
            grid.add(newCell, newCol, newRow);
        }

        // Update box position
        if (matrix[newRow][newCol] == 3) {
            newCell = new GridComponent(40, Color.BROWN); // Box cell
            grid.add(newCell, newCol, newRow);
        }

        // Update old position (might be empty, wall, or target)
        newCell = new GridComponent(40, getColorForCell(matrix[oldRow][oldCol]));
        grid.add(newCell, oldCol, oldRow); // Revert the old cell to its appropriate color
    }


    /**
     * Returns the color based on the matrix cell type.
     */
    private Color getColorForCell(int cellType) {
        switch (cellType) {
            case 1: return Color.DARKGRAY; // Wall
            case 4: return Color.YELLOW; // Target
            case 2: return Color.WHITE; // Empty space
            default: return Color.WHITE;
        }
    }

    /**
     * Saves the current game state to a file.
     *
     * @param filename The name of the file to save to.
     */
    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/main/java/SOKOBAN/resources/saves/" + filename + ".dat"))) {
            out.writeObject(model.getMatrix()); // Save the game matrix
            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restarts the game by reinitializing the grid.
     */
    public void restartGame() {
        initializeGame();
        System.out.println("Game restarted.");
    }
}
