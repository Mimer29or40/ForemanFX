package mimer29or40.foremanfx.gui.graph.element;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphElement extends Group
{
    public final ProductionGraphViewer parent;
    public    List<GraphElement> subElements = new ArrayList<>();
    //    protected int x;
//    protected int y;
//    protected int width;
//    protected int height;
    protected IntegerProperty    x           = new SimpleIntegerProperty(this, "x");
    protected IntegerProperty    y           = new SimpleIntegerProperty(this, "x");
    protected IntegerProperty    width       = new SimpleIntegerProperty(this, "x");
    protected IntegerProperty    height      = new SimpleIntegerProperty(this, "x");

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

    public void dispose() // TODO Proper node removing
    {
        parent.elements.remove(this);
    }

    public int getX()
    {
        return x.get();
    }

    public void setX(int x)
    {
        this.x.set(x);
    }

    public int getY()
    {
        return y.get();
    }

    public void setY(int y)
    {
        this.y.set(y);
    }

    public Point2D getLocation()
    {
        return new Point2D(x.get(), y.get());
    }

    public void move(Point2D point)
    {
        move(point.getX(), point.getY());
    }

    public void move(Number x, Number y)
    {
        this.x.set(x.intValue());
        this.y.set(y.intValue());
        setTranslateX(x.intValue());
        setTranslateY(y.intValue());
    }

    public int getWidth()
    {
        return width.get();
    }

    public void setWidth(int width)
    {
        this.width.set(width);
    }

    public int getHeight()
    {
        return height.get();
    }

    public void setHeight(int height)
    {
        this.height.set(height);
    }

    public Rectangle2D getBounds()
    {
        return new Rectangle2D(x.get(), y.get(), width.get(), height.get());
    }

    public void setBounds(Rectangle2D bounds)
    {
        setX((int) bounds.getMinX());
        setY((int) bounds.getMinY());
        this.width.setValue(bounds.getWidth());
        this.height.setValue(bounds.getHeight());
    }
}
