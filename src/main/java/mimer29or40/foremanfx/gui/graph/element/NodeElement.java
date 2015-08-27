package mimer29or40.foremanfx.gui.graph.element;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.gui.node.ConsumerNode;
import mimer29or40.foremanfx.gui.node.ProductionNode;
import mimer29or40.foremanfx.gui.node.RecipeNode;
import mimer29or40.foremanfx.gui.node.SupplyNode;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class NodeElement extends GraphElement
{
    public int dragOffsetX;
    public int dragOffsetY;
    private       Color         backgroundPaint  = Color.rgb(0xff, 0x7f, 0x6b);
    private final int           assemblerSize    = 32;
    private final int           assemblerBorderX = 10;
    private final int           assemblerBorderY = 30;
    private final int           tabPadding       = 8;
    public        String        textValue        = "";
    //    private AssemblerBox assemblerBox;
    private       List<ItemTab> inputTabs        = new ArrayList<>();
    private       List<ItemTab> outputTabs       = new ArrayList<>();
    private       ContextMenu   rightClickMenu   = new ContextMenu();
    public ProductionNode displayedNode;
    public Boolean tooltipsEnabled = true;

    private Rectangle background;
    private Text      text;

    public NodeElement(ProductionNode node, ProductionGraphViewer parent)
    {
        super(parent);

        this.displayedNode = node;

        if (displayedNode instanceof ConsumerNode)
        {
            if (!((ConsumerNode) displayedNode).getConsumedItem().isMissingItem)
            {
                backgroundPaint = Color.rgb(249, 237, 195);
            }
        }
        else if (displayedNode instanceof SupplyNode)
        {
            if (!((SupplyNode) displayedNode).getSuppliedItem().isMissingItem)
            {
                backgroundPaint = Color.rgb(231, 214, 224);
            }
        }
        else if (displayedNode instanceof RecipeNode)
        {
            if (!((RecipeNode) displayedNode).getBaseRecipe().isMissingRecipe)
            {
                backgroundPaint = Color.rgb(190, 217, 212);
            }
        }

        if (node != null)
        {
            for (Item item : node.getInputs())
            {
                ItemTab newTab = new ItemTab(item, LinkType.Input, parent);
                subElements.add(newTab);
                inputTabs.add(newTab);
            }
            for (Item item : node.getOutputs())
            {
                ItemTab newTab = new ItemTab(item, LinkType.Output, parent);
                subElements.add(newTab);
                outputTabs.add(newTab);
            }
        }

        if (displayedNode instanceof RecipeNode || displayedNode instanceof SupplyNode)
        {
//            assemblerBox = new AssemblerBox(parent);
//            subElements.add(assemblerBox);
//            assemblerBox.height = assemblerBox.width = 50;
//            this.getChildren().add(assemblerBox);
        }

        background = new Rectangle();
        text = new Text();
        this.getChildren().addAll(background, text);

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, parent.nodeEventHandler.getOnMousePressedEventHandler());
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, parent.nodeEventHandler.getOnMouseDraggedEventHandler());
    }

    @Override
    public void draw()
    {
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setStroke(backgroundPaint);
        background.setFill(backgroundPaint);
        background.setFill(backgroundPaint.deriveColor(1, 1, 1, 0.5));

        width = Math.max(Math.max(75, getIconWidths()), (int) text.getLayoutBounds().getWidth());
        Logger.debug(width);
        height = 80;
        background.setWidth(width);
        background.setHeight(height);

        for (ItemTab tab : outputTabs)
        {
            tab.draw();
            this.getChildren().add(tab);
        }
        for (ItemTab tab : inputTabs)
        {
            tab.draw();
            this.getChildren().add(tab);
        }
        updateTabOrder();
    }

    @Override
    public void update()
    {
        updateTabOrder();

        
    }

    private int getIconWidths()
    {
        return Math.max(getInputIconWidths(), getOutputIconWidths());
    }

    private int getInputIconWidths()
    {
        int result = tabPadding;
        for (ItemTab tab : inputTabs)
        {
            result += tab.width + tabPadding;
        }
        return result;
    }

    private int getOutputIconWidths()
    {
        int result = tabPadding;
        for (ItemTab tab : outputTabs)
        {
            result += tab.width + tabPadding;
        }
        return result;
    }

    public void updateTabOrder()
    {
//        inputTabs = inputTabs.sort(it -> getItemTabXHeuristic(it)).ToList();
//        outputTabs = outputTabs.sort(it -> getItemTabXHeuristic(it)).ToList();

        int x = (width - getOutputIconWidths()) / 2;
        for (ItemTab tab : outputTabs)
        {
            x += tabPadding;
            tab.setTranslateX(x);
            tab.setTranslateY(-tab.height / 2);
            x += tab.width;
        }
        x = (width - getInputIconWidths()) / 2;
        for (ItemTab tab : inputTabs)
        {
            x += tabPadding;
            tab.setTranslateX(x);
            tab.setTranslateY(height - tab.height / 2);
            x += tab.width;
        }
    }
}
