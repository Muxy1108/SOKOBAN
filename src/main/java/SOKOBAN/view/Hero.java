package SOKOBAN.view;

public class Hero extends Movable {

    public Hero(int row, int col) {
        this.setRow(row);
        this.setCol(col);
    }

    public void move(int newRow, int newCol) {
        this.setRow(newRow);
        this.setCol(newCol);
    }
}
