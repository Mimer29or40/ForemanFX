package mimer29or40.foremanfx.event;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;

public class CanvasEventHandler
{
    private static final double MAX_SCALE   = 3.0D;
    private static final double MIN_SCALE   = 0.2D;
    private static final double SCALE_DELTA = 1.1;

    private DragContext sceneDragContext = new DragContext();

    ProductionGraphViewer canvas;

    public CanvasEventHandler(ProductionGraphViewer canvas)
    {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler()
    {
        return (event) ->
        { // right mouse button => panning
            if (!event.isSecondaryButtonDown())
            { return; }

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();
        };
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler()
    {
        return (event) ->
        { // right mouse button => panning
            if (!event.isSecondaryButtonDown())
            { return; }

            canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
            canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

            event.consume();
        };
    }

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    public EventHandler<ScrollEvent> getOnScrollEventHandler()
    {
        return (event) ->
        {
            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (event.getDeltaY() < 0)
            { scale /= SCALE_DELTA; }
            else
            { scale *= SCALE_DELTA; }

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale) - 1;
            double dx = (event.getX() - (canvas.getBoundsInParent().getWidth() / 2 + canvas.getBoundsInParent()
                                                                                           .getMinX()));
            double dy = (event.getY() - (canvas.getBoundsInParent().getHeight() / 2 + canvas.getBoundsInParent()
                                                                                            .getMinY()));
            canvas.setScale(scale);

            // note: pivot value must be untransformed, i. e. without scaling
            canvas.setPivot(f * dx, f * dy);

            event.consume();
        };
    }

    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scrollPane)
    {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scrollPane.getViewportBounds().getWidth();
        double hScrollProportion = (scrollPane.getHvalue() - scrollPane.getHmin()) /
                                   (scrollPane.getHmax() - scrollPane.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scrollPane.getViewportBounds().getHeight();
        double vScrollProportion = (scrollPane.getVvalue() - scrollPane.getVmin()) /
                                   (scrollPane.getVmax() - scrollPane.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScrollPane(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset)
    {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0)
        {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            double newScrollXOffset = (scaleFactor - 1) * halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(
                    scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        }
        else
        {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0)
        {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(
                    scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        }
        else
        {
            scroller.setHvalue(scroller.getHmin());
        }
    }

    private static double clamp(double value, double min, double max)
    {

        if (Double.compare(value, min) < 0)
        { return min; }

        if (Double.compare(value, max) > 0)
        { return max; }

        return value;
    }
}
