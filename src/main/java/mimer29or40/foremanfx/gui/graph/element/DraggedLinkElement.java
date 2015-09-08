package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.Item;

public class DraggedLinkElement extends GraphElement
{
    public NodeElement supplierElement;
    public NodeElement consumerElement;
    public Item        item;
    public LinkType    startConnectionType;
    public DragType    dragType;

    public int curveX;
    public int curveY;

    private CubicCurve curve;

    public DraggedLinkElement(ProductionGraphViewer parent, NodeElement startNode, LinkType startConnectionType, Item item)
    {
        super(parent);

        if (startConnectionType == LinkType.Input)
        {
            consumerElement = startNode;
        }
        else
        {
            supplierElement = startNode;
        }
        this.startConnectionType = startConnectionType;
        this.item = item;
//        if ((Control.MouseButtons & MouseButtons.Left) != 0)
//        {
//            DragType = DragType.MouseDown;
//        }
//        else
//        {
//            DragType = DragType.MouseUp;
//        }
        curve = new CubicCurve();

        getChildren().add(curve);

        curve.setFill(null);
        curve.setStroke(Color.BLUE);
    }

    @Override
    public void setupElements()
    {

    }

    @Override
    public void update()
    {
        Point2D pointN = new Point2D(curveX, curveY);
        Point2D pointM = new Point2D(curveX, curveY);

        if (supplierElement != null)
        {
            pointN = supplierElement.getOutputLineConnectionPoint(item);
        }
        if (consumerElement != null)
        {
            pointM = consumerElement.getInputLineConnectionPoint(item);
        }
//        Point2D pointN2 = new Point2D(pointN.getX(), pointN.getY() - Math.max((int) ((pointN.getY() - pointM.getY()) / 2), 40));
//        Point2D pointM2 = new Point2D(pointM.getX(), pointM.getY() + Math.max((int) ((pointN.getY() - pointM.getY()) / 2), 40));

        curve.setStartX(getX() + pointN.getX());
        curve.setStartY(getY() + pointN.getY());

//        curve.setControlX1(pointN2.getX());
//        curve.setControlY1(pointN2.getY());
//
//        curve.setControlX2(pointM2.getX());
//        curve.setControlY2(pointM2.getY());

        curve.setEndX(getX() + pointM.getX());
        curve.setEndY(getY() + pointM.getY());
//        Logger.debug("(%s,%s) (%s,%s)", pointN.getX(), pointN.getY(), pointM.getX(), pointM.getY());
    }
}
