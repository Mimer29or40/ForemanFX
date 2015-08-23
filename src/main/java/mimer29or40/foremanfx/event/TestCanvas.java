package mimer29or40.foremanfx.event;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TestCanvas extends Pane
{
    DoubleProperty scale = new SimpleDoubleProperty(1.0);

    public TestCanvas()
    {
        setPrefSize(600, 600);
//        resize(600,600);
        setStyle("-fx-background-color: lightgrey; -fx-border-color: blue;");

        // add scale transform
        scaleXProperty().bind(scale);
        scaleYProperty().bind(scale);
    }

    /**
     * Add a grid to the canvas, send it to back
     */
    public void addGrid()
    {
        double w = getBoundsInLocal().getWidth();
        double h = getBoundsInLocal().getHeight();

        // add grid
        Canvas grid = new Canvas(w, h);

        // don't catch mouse events
        grid.setMouseTransparent(true);

        GraphicsContext gc = grid.getGraphicsContext2D();

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

        // draw grid lines
        double offset = 50;
        for (double i = offset; i < w; i += offset)
        {
            gc.strokeLine(i, 0, i, h);
            gc.strokeLine(0, i, w, i);
        }

//        getChildren().add( grid);

        grid.toBack();
    }

    public double getScale()
    {
        return scale.get();
    }

    public void setScale(double scale)
    {
        this.scale.set(scale);
    }

    public void setPivot(double x, double y)
    {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }
}
