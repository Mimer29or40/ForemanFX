package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.gui.node.NodeLink;
import mimer29or40.foremanfx.gui.node.ProductionNode;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.util.Logger;

public class LinkElement extends GraphElement
{
    public  NodeLink     displayedLink;
    private Path         path;
    private MoveTo       moveTo;
    private CubicCurveTo curve;

    public LinkElement(ProductionGraphViewer parent, NodeLink displayedLink)
    {
        super(parent);
        this.displayedLink = displayedLink;

        path = new Path();
        moveTo = new MoveTo();
        curve = new CubicCurveTo();
        path.getElements().addAll(moveTo, curve);
        this.getChildren().add(path);
    }

    @Override
    public void setupElements()
    {

    }

    @Override
    public void update()
    {
        Point2D supplier = getSupplierElement().getOutputLineConnectionPoint(getItem());
        Point2D consumer = getSupplierElement().getInputLineConnectionPoint(getItem());

        Point2D supplierMid = new Point2D(supplier.getX(), supplier.getY() - Math.max((int) (supplier.getY() - consumer.getY()) / 2, 40));
        Point2D consumerMid = new Point2D(consumer.getX(), consumer.getY() + Math.max((int) (supplier.getY() - consumer.getY()) / 2, 40));

        Logger.debug("(%s,%s) (%s,%s)", supplier.getX(), supplier.getY(), consumer.getX(), consumer.getY());

        moveTo.setX(supplier.getX());
        moveTo.setY(supplier.getY());
        curve.setControlX1(supplierMid.getX());
        curve.setControlY2(supplierMid.getY());
        curve.setControlX2(consumerMid.getX());
        curve.setControlY2(consumerMid.getY());
        curve.setX(consumer.getX());
        curve.setY(consumer.getY());
    }

    public ProductionNode getSupplier()
    {
        return displayedLink.supplier;
    }

    public ProductionNode getConsumer()
    {
        return displayedLink.consumer;
    }

    public Item getItem()
    {
        return displayedLink.item;
    }

    public NodeElement getSupplierElement()
    {
        return parent.getElementForNode(getSupplier());
    }

    public NodeElement getConsumerElement()
    {
        return parent.getElementForNode(getConsumer());
    }
}
