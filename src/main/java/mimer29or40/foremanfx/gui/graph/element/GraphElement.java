package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;

import java.util.HashSet;

public abstract class GraphElement
{
    public       HashSet<GraphElement> subElements;
    public       Point2D               location;
    public       int                   x;
    public       int                   y;
    public       Point2D               size;
    public       int                   width;
    public       int                   height;
    public       Rectangle2D           bounds;
    public final ProductionGraphViewer parent;

    public GraphElement(ProductionGraphViewer parent)
    {
        this.parent = parent;
        this.parent.elements.add(this);
    }

    public boolean containsPoint(Point2D point)
    {
        return false;
    }

    public void paint(GraphicsContext graphics)
    {
        for (GraphElement element : subElements)
        {
            graphics.translate(element.x, element.y);
            element.paint(graphics);
            graphics.translate(-element.x, -element.y);
        }
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
