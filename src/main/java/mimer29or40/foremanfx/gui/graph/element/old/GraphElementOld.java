package mimer29or40.foremanfx.gui.graph.element.old;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Shape;
import mimer29or40.foremanfx.gui.graph.ProductionGraphPaneOld;

import java.util.HashSet;

public abstract class GraphElementOld extends Shape
{
    public       HashSet<GraphElementOld> subElements;
    protected    Point2D                  location;
    protected    int                      x;
    protected    int                      y;
    protected    Point2D                  size;
    protected    int                      width;
    protected    int                      height;
    public       Rectangle2D              bounds;
    public final ProductionGraphPaneOld   parent;

    public GraphElementOld(ProductionGraphPaneOld parent)
    {
        super();
        this.parent = parent;
        this.parent.elements.add(this);
    }

    public boolean containsPoint(Point2D point)
    {
        return false;
    }

    public void paint(GraphicsContext graphics)
    {
        for (GraphElementOld element : subElements)
        {
            graphics.translate(element.x, element.y);
            element.paint(graphics);
            graphics.translate(-element.x, -element.y);
        }
    }

    public Point2D getLocation()
    {
        return location;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public Point2D getSize()
    {
        return size;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void mouseMoved(Point2D location)
    {}

    public void mouseDown(Point2D location, MouseButton button)
    {}

    public void mouseUp(Point2D location, MouseButton button)
    {}

    public void dragged(Point2D location)
    {}

    public void dispose()
    {
        parent.elements.remove(this);
//        parent.invalidate();
    }
}
