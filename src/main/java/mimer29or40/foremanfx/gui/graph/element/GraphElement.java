package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphElement extends Group
{
    public final ProductionGraphViewer parent;
    public List<GraphElement> subElements = new ArrayList<>();
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public GraphElement(ProductionGraphViewer parent)
    {
        super();
        this.parent = parent;
        this.parent.elements.add(this);
        this.parent.getChildren().add(this);
    }

    public void setupElements()
    {
        for (GraphElement element : subElements)
        {
            element.setupElements();
            this.getChildren().add(element);
        }

        update();
    }

    public abstract void update();

    public void dispose()
    {
        parent.elements.remove(this);
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
        setTranslateX(x);
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
        setTranslateY(y);
    }

    public Point2D getLocation()
    {
        return new Point2D(x, y);
    }

    public void setLocation(Point2D point)
    {
        setX((int) point.getX());
        setY((int) point.getY());
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public Rectangle2D getBounds()
    {
        return new Rectangle2D(x, y, width, height);
    }

    public void setBounds(Rectangle2D bounds)
    {
        setX((int) bounds.getMinX());
        setY((int) bounds.getMinY());
        this.width = (int) bounds.getWidth();
        this.height = (int) bounds.getHeight();
    }
}
