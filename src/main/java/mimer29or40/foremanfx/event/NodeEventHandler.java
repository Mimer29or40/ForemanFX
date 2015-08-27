package mimer29or40.foremanfx.event;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;

public class NodeEventHandler
{
    private DragContext nodeDragContext = new DragContext();

    ProductionGraphViewer canvas;

    public NodeEventHandler(ProductionGraphViewer canvas)
    {
        this.canvas = canvas;

    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler()
    {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler()
    {
        return onMouseDraggedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = event ->
    {
        // left mouse button => dragging
        if (!event.isPrimaryButtonDown())
        { return; }

        nodeDragContext.mouseAnchorX = event.getSceneX();
        nodeDragContext.mouseAnchorY = event.getSceneY();

        Node node = (Node) event.getSource();

        nodeDragContext.translateAnchorX = node.getTranslateX();
        nodeDragContext.translateAnchorY = node.getTranslateY();

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = event ->
    {
        // left mouse button => dragging
        if (!event.isPrimaryButtonDown())
        { return; }

        double scale = canvas.getScale();

        Node node = (Node) event.getSource();

        node.setTranslateX(
                nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
        node.setTranslateY(
                nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

        event.consume();
    };
}
