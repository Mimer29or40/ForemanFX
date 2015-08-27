package mimer29or40.foremanfx.gui.graph.element.old;

import com.sun.javafx.geom.Shape;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mimer29or40.foremanfx.gui.graph.ProductionGraphPaneOld;
import mimer29or40.foremanfx.gui.node.NodeLink;
import mimer29or40.foremanfx.gui.node.ProductionNode;
import mimer29or40.foremanfx.model.Item;

public class LinkElementOld extends GraphElementOld
{
    public NodeLink       displayedLink;
    public ProductionNode supplier;
    public ProductionNode consumer;
    public NodeElementOld supplierElement;
    public NodeElementOld consumerElement;
    public Item           item;
    public Point2D        location;
    public int            x;
    public int            y;
    public Point2D        size;
    public int            width;
    public int            height;

    public LinkElementOld(ProductionGraphPaneOld parent, NodeLink displayedLink)
    {
        super(parent);
        displayedLink = displayedLink;
    }

    @Override
    public void paint(GraphicsContext graphics)
    {
//        Point2D pointN = supplierElement.getOutputLineConnectionPoint(item);
//        Point2D pointM = consumerElement.getInputLineConnectionPoint(item);
//        Point2D pointN2 = new Point2D(pointN.getX(), pointN.getY() - Math.max((int) ((pointN.getY() - pointM.getY()
// ) / 2),
//                                                                              40));
//        Point2D pointM2 = new Point2D(pointM.getX(), pointM.getY() + Math.max((int) ((pointN.getY() - pointM.getY()
// ) / 2),
//                                                                              40));
//        graphics.setFill(DataCache.iconAverageColour(item.getIcon()), 3f);
//
//        graphics.bezierCurveTo(pointN, pointN2, pointM2, pointM);
    }

    public Point2D getLocation()
    {
        return Point2D.ZERO;
    }

    public int getX()
    {
        return 0;
    }

    public int getY()
    {
        return 0;
    }

    public Point2D getSize()
    {
        return Point2D.ZERO;
    }

    public int getWidth()
    {
        return 0;
    }

    public int getHeight()
    {
        return 0;
    }

    @Override
    public Shape impl_configShape()
    {
        return null;
    }
}
