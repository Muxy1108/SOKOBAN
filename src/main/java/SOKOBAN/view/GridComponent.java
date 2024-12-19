package SOKOBAN.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GridComponent extends Rectangle {
    public GridComponent(int size, Color color) {
        super(size, size, color);
        setStroke(Color.BLACK);
    }
}
