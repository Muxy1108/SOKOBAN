package SOKOBAN.view;

public class Box {
    private int row;
    private int col;

    public Box(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void move(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }
}
