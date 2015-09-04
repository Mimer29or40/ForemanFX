package mimer29or40.foremanfx.event;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
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

    public EventHandler<MouseEvent> getOnMousePressed()
    {
        return onMousePressed;
    }

    public EventHandler<MouseEvent> getOnMouseDragged()
    {
        return onMouseDragged;
    }

    public EventHandler<MouseEvent> getOnMouseReleased()
    {
        return onMouseReleased;
    }

    public EventHandler<MouseEvent> getOnMouseEntered()
    {
        return onMouseEntered;
    }

    public EventHandler<MouseEvent> getOnMouseExited()
    {
        return onMouseExited;
    }

    private EventHandler<MouseEvent> onMousePressed = event ->
    {
        // left mouse button => dragging
        if (!event.isPrimaryButtonDown())
        { return; }
        canvas.getScene().setCursor(Cursor.MOVE);

        nodeDragContext.mouseAnchorX = event.getSceneX();
        nodeDragContext.mouseAnchorY = event.getSceneY();

        Node node = (Node) event.getSource();

        nodeDragContext.translateAnchorX = node.getTranslateX();
        nodeDragContext.translateAnchorY = node.getTranslateY();

    };

    private EventHandler<MouseEvent> onMouseDragged = event ->
    {
        // left mouse button => dragging
        if (!event.isPrimaryButtonDown())
        { return; }

        double scale = canvas.getScale();

        Node node = (Node) event.getSource();

        node.setTranslateX(nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
        node.setTranslateY(nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

        event.consume();
    };

    private EventHandler<MouseEvent> onMouseReleased = event -> canvas.getScene().setCursor(Cursor.HAND);

    private EventHandler<MouseEvent> onMouseEntered = event ->
    {
        if (!event.isPrimaryButtonDown())
        {
            canvas.getScene().setCursor(Cursor.HAND);
        }
    };

    private EventHandler<MouseEvent> onMouseExited = event ->
    {
        if (!event.isPrimaryButtonDown())
        {
            canvas.getScene().setCursor(Cursor.DEFAULT);
        }
    };
}
