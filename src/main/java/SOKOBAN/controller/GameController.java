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
    private final GridPane grid;

    public GridPane getGridPane() {
        return grid;
    }

    private final MapMatrix model;
    private Hero hero;

    public GameController(GridPane grid, MapMatrix model) {
        this.grid = grid;
        this.model = model;
        initializeGame();
    }

    public void initializeGame() {
        grid.getChildren().clear();
        int[][] matrix = model.getMatrix();

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                GridComponent cell;
                switch (matrix[row][col]) {
                    case -1 -> { // Wall
                        cell = new GridComponent(40, Color.DARKGRAY);
                    }
                    case 2 -> { // Target
                        cell = new GridComponent(40, Color.YELLOW);
                    }
                    case 1 -> { // Box
                        cell = new GridComponent(40, Color.BROWN);
                        Box box = new Box(row, col);
                    }
                    case 3 -> { // Hero
                        cell = new GridComponent(40, Color.BLUE);
                        hero = new Hero(row, col);
                    }
                    default -> { // Empty space
                        cell = new GridComponent(40, Color.WHITE);
                    }
                }
                grid.add(cell, col, row);
            }
        }
    }

    public void moveHero(Direction direction) {
        if (hero == null) return;

        int currentRow = hero.getRow();
        int currentCol = hero.getCol();
        int targetRow = currentRow + direction.getRowOffset();
        int targetCol = currentCol + direction.getColOffset();

        int[][] matrix = model.getMatrix();

        // Ensure within bounds and valid move
        if (targetRow < 0 || targetRow >= matrix.length || targetCol < 0 || targetCol >= matrix[0].length) return;

        if (matrix[targetRow][targetCol] == 0 || matrix[targetRow][targetCol] == 2) { // Move into empty or target
            matrix[currentRow][currentCol] = 0;
            matrix[targetRow][targetCol] = 3;
            hero.move(targetRow, targetCol);
            initializeGame();
        } else if (matrix[targetRow][targetCol] == 1) { // Push box
            int boxTargetRow = targetRow + direction.getRowOffset();
            int boxTargetCol = targetCol + direction.getColOffset();
            if (matrix[boxTargetRow][boxTargetCol] == 0 || matrix[boxTargetRow][boxTargetCol] == 2) {
                matrix[currentRow][currentCol] = 0;
                matrix[targetRow][targetCol] = 3;
                matrix[boxTargetRow][boxTargetCol] = 1;
                hero.move(targetRow, targetCol);
                initializeGame();
            }
        }
    }

    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/main/java/SOKOBAN/resources/" + filename + ".txt"))) {
            out.writeObject(model.getMatrix());

            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restartGame() {
        initializeGame();
        System.out.println("Game restarted.");
    }


}
