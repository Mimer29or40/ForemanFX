package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GhostNodeElement extends GraphElement
{
    public        HashSet<Item> items       = new HashSet<>();
    private final int           iconSize    = 32;
    private       List<Point2D> offsetOrder = new ArrayList<>(Arrays.asList(
            new Point2D(0, 0),
            new Point2D(0, 35),
            new Point2D(0, -35),
            new Point2D(-35, 0),
            new Point2D(35, 0),
            new Point2D(-35, -35),
            new Point2D(35, 35),
            new Point2D(-35, 35),
            new Point2D(35, -35)
                                                                           ));

    public GhostNodeElement(ProductionGraphViewer parent)
    {
        super(parent);
        width = 96;
        height = 96;
    }

    @Override
    public void paint(GraphicsContext graphics)
    {
        int i = 0;

        for (Item item : items)
        {
            if (i >= offsetOrder.size())
            { break; }
            Point2D position = offsetOrder.get(i).subtract(iconSize / 2, iconSize / 2);
            int scale = iconSize / (int) parent.viewScale;
            Image icon = item.getIcon() != null ? item.getIcon() : DataCache.unknownIcon;
            graphics.drawImage(icon, position.getX(), position.getY(), scale, scale);
        }
    }

    @Override
    public void dispose()
    {
        if (parent.ghostDragElement == null)
        {
            parent.ghostDragElement = null;
        }
        super.dispose();
    }
}
