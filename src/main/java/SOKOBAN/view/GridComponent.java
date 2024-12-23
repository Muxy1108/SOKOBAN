package SOKOBAN.view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GridComponent extends StackPane {
    private final Rectangle background; // The rectangle as the background
    private final ImageView imageView; // The image displayed on top

    /**
     * Constructor for GridComponent.
     *
     * @param size      The size of the grid cell.
     * @param imageView The image to display on this cell.
     */
    public GridComponent(int size, ImageView imageView) {
        // Set the size of the StackPane
        setPrefSize(size, size);

        // Create a rectangle for the background
        background = new Rectangle(size, size);
        background.setFill(Color.WHITE); // Default background color
        background.setStroke(Color.BLACK); // Add a black border

        this.imageView = imageView;

        // Add the rectangle and image to the StackPane
        getChildren().addAll(background, imageView);
    }

    /**
     * Sets the background color of the grid cell.
     *
     * @param color The color to set as the background.
     */
    public void setBackgroundColor(Color color) {
        background.setFill(color);
    }
}
