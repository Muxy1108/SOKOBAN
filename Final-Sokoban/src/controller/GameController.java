package controller;

import model.Direction;
import model.MapMatrix;
import view.game.Box;
import view.game.GamePanel;
import view.game.GridComponent;
import view.game.Hero;
//2323332
/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    private final GamePanel view;
    private final MapMatrix model;

    public GameController(GamePanel view, MapMatrix model) {
        this.view = view;
        this.model = model;
        view.setController(this);
    }

    public void restartGame() {
        System.out.println("Do restart game here");
    }

    public boolean doMove(int row, int col, Direction direction) {
        GridComponent currentGrid = view.getGridComponent(row, col);
        //target row can column.
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();

        GridComponent targetGrid = view.getGridComponent(tRow, tCol);

        int[][] map = model.getMatrix();

        if (map[tRow][tCol] % 10 == 0 || map[tRow][tCol] % 10 == 2) {
            //update hero in MapMatrix
            model.getMatrix()[row][col] -= 20;
            model.getMatrix()[tRow][tCol] += 20;
            //Update hero in GamePanel
            Hero h = currentGrid.removeHeroFromGrid();
            targetGrid.setHeroInGrid(h);
            //Update the row and column attribute in hero
            if(model.getMatrix()[tRow][tCol] / 10 == 3) {
                GridComponent tGplus = view.getGridComponent(tRow + direction.getRow(), tCol + direction.getCol());
                model.getMatrix()[tRow][tCol] -= 10;
                model.getMatrix()[tRow + direction.getRow()][tCol + direction.getCol()] += 10;
                Box b = currentGrid.removeBoxFromGrid();
                tGplus.setBoxInGrid(b);

            }


            return true;
            //这里加箱子的移动。？？
        }
        System.out.println("Move Failed");
        return false;
    }

    //todo: add other methods such as loadGame, saveGame...

}
