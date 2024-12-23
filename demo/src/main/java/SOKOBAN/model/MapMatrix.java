package SOKOBAN.model;

import java.io.*;
import SOKOBAN.controller.GameController;
import SOKOBAN.view.GamePanel;

//0
//1 墙
//2空格
//3箱子
//4指定地点
//5hero
public class MapMatrix {
    private int[][] matrix;
    private int[][] initialMatrix;
    public static int loadNum;


    public void setInitialMatrix(int[][] initialMatrix) {
        this.initialMatrix = initialMatrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }



    public static MapMatrix loadLevel(int level) {
        loadNum = level;
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
            mapMatrix.initialMatrix = new int[rows][cols];
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
                            default  -> -1;
                        };
                        mapMatrix.initialMatrix[row][col] = switch (c) {
                            case '0' -> 0;
                            case '1' -> 1; // Wall
                            case '4' -> 4;  // Target
                            case '3' -> 3;  // Box
                            case '5' -> 5;  // Hero
                            case '2' -> 2;   // Empty space
                            default  -> -1;
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
        int steps = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int rows = 0;
            int cols = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Steps: ")) {
                    String input = line.trim();
                    //System.out.println(input);

                    // Step 1: Remove "steps: " from the string
                    String numberString = input.replace("Steps: ", "").trim();
                    //System.out.println(numberString);
                    // Step 2: Convert the string to an integer
                    steps = Integer.parseInt(numberString);
                    //steps = Integer.parseInt(line.substring(0).trim()); // Extract the steps after "Steps: "
                }
                cols = Math.max(cols, line.length());
                rows++;
            }

            loadedMatrix.matrix = new int[rows][cols];
            loadedMatrix.initialMatrix = new int[rows][cols];
            reader.close();

            try (BufferedReader secondReader = new BufferedReader(new FileReader(filename))) {
                int row = 0;
                while ((line = secondReader.readLine()) != null) {
                    for (int col = 0; col < line.length(); col++) {
                        char c = line.charAt(col);
                        loadedMatrix.matrix[row][col] = switch (c) {
                            case '0' -> 0;
                            case '1' -> 1; // Wall
                            case '4' -> 4;  // Target
                            case '3' -> 3;  // Box
                            case '5' -> 5;  // Hero
                            case '2' -> 2;   // Empty space
                            default  -> -1;
                        };
                        loadedMatrix.initialMatrix[row][col] = switch (c) {
                            case '0' -> 0;
                            case '1' -> 1; // Wall
                            case '4' -> 4;  // Target
                            case '3' -> 3;  // Box
                            case '5' -> 5;  // Hero
                            case '2' -> 2;   // Empty space
                            default  -> -1;
                        };
                    }
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        GamePanel.savedsteps = steps;
        //System.out.println(GamePanel.steps);
        return loadedMatrix;
    }

    /*public void initializeGame() {
        GameController.initializeGame();
    }*/

    public int[][] getMatrix() {
        return matrix;
    }
    public int[][] getInitialMatrix() {
        return initialMatrix;
    }
}
