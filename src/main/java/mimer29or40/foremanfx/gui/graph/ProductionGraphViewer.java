package mimer29or40.foremanfx.gui.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.StackPane;
import mimer29or40.foremanfx.event.CanvasEventHandler;
import mimer29or40.foremanfx.event.NodeEventHandler;
import mimer29or40.foremanfx.gui.graph.element.GraphElement;

import java.util.ArrayList;
import java.util.List;

public class ProductionGraphViewer extends StackPane
{
    public        List<GraphElement> elements = new ArrayList<>();
    private final DoubleProperty     scale    = new SimpleDoubleProperty(1.0);
    public final CanvasEventHandler canvasEventHandler;
    public final NodeEventHandler   nodeEventHandler;
    public boolean showAssemblers = true;
    public boolean showMiners     = false;

    public ProductionGraphViewer()
    {
        canvasEventHandler = new CanvasEventHandler(this);
        nodeEventHandler = new NodeEventHandler(this);

        setPrefSize(600, 200);
//        resize(600,600);
        setStyle("-fx-border-color: blue;");

        // add scale transform
        scaleXProperty().bind(scale);
        scaleYProperty().bind(scale);
    }

    public void drawElement()
    {
        for (GraphElement element : elements)
        {
            element.setupElements();
        }
    }

    public double getScale()
    {
        return scale.get();
    }

    public void setScale(double scale)
    {
        this.scale.set(scale);
    }

    public void setPivot(double x, double y)
    {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }
}
