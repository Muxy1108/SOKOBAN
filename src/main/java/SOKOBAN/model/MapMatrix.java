package SOKOBAN.model;

import java.io.*;
import SOKOBAN.controller.GameController;

public class MapMatrix {
    private int[][] matrix;

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }



    public static MapMatrix loadLevel(int level) {
        MapMatrix mapMatrix = new MapMatrix();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/SOKOBAN/resources/maps/level" + level + ".txt"))) {
            String line;
            int rows = 0;
            int cols = 0;

            while ((line = reader.readLine()) != null) {
                cols = Math.max(cols, line.length());
                rows++;
            }

            mapMatrix.matrix = new int[rows][cols];
            reader.close();

            try (BufferedReader secondReader = new BufferedReader(new FileReader("src/main/java/SOKOBAN/resources/maps/level" + level + ".txt"))) {
                int row = 0;
                while ((line = secondReader.readLine()) != null) {
                    for (int col = 0; col < line.length(); col++) {
                        char c = line.charAt(col);
                        mapMatrix.matrix[row][col] = switch (c) {
                            case '0' -> 0;
                            case '1' -> 1; // Wall
                            case '4' -> 4;  // Target
                            case '3' -> 3;  // Box
                            case '5' -> 5;  // Hero
                            case '2' -> 2;   // Empty space
                            default  -> 0;
                        };
                    }
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapMatrix;
    }

    public static MapMatrix loadFromFile(String filename) {
        MapMatrix loadedMatrix = new MapMatrix();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("resources/saves/" + filename))) {
            loadedMatrix = (MapMatrix) in.readObject();
            //loadedMatrix.setMatrix( (int[][]) in.readObject());
            //setMatrix(loadedMatrix);
            //GameController.initializeGame();
            System.out.println("Game loaded.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedMatrix;
    }

    /*public void initializeGame() {
        GameController.initializeGame();
    }*/

    public int[][] getMatrix() {
        return matrix;
    }
}
