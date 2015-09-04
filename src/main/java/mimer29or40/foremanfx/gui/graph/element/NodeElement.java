package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.gui.node.ConsumerNode;
import mimer29or40.foremanfx.gui.node.ProductionNode;
import mimer29or40.foremanfx.gui.node.RecipeNode;
import mimer29or40.foremanfx.gui.node.SupplyNode;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.MachinePermutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeElement extends GraphElement
{
    public int dragOffsetX;
    public int dragOffsetY;
    private       Color  backgroundPaint  = Color.rgb(0xff, 0x7f, 0x6b);
    private final int    assemblerSize    = 32;
    private final int    assemblerBorderX = 10;
    private final int    assemblerBorderY = 30;
    private final int    tabPadding       = 8;
    public        String textValue        = "";
    private AssemblerInfoElement assemblerInfoElement;
    private List<ItemTab> inputTabs      = new ArrayList<>();
    private List<ItemTab> outputTabs     = new ArrayList<>();
    private ContextMenu   rightClickMenu = new ContextMenu();
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

        for (Item item : node.getOutputs())
        {
            ItemTab newTab = new ItemTab(item, LinkType.Output, parent);
            subElements.add(newTab);
            outputTabs.add(newTab);
        }
        for (Item item : node.getInputs())
        {
            ItemTab newTab = new ItemTab(item, LinkType.Input, parent);
            subElements.add(newTab);
            inputTabs.add(newTab);
//            newTab.setOnMousePressed(event ->
//                                     {
//                                         newTab.setX((int) event.getX());
//                                         newTab.setY((int) event.getY());
//                                     });
            newTab.setOnMouseDragged(event ->
                                     {
                                         DraggedLinkElement newLink = new DraggedLinkElement(parent, this, newTab.type, newTab.getItem());
                                         newLink.curveX = (int) event.getSceneX();
                                         newLink.curveY = (int) event.getSceneY();
                                         subElements.add(newLink);
                                         if (newTab.type == LinkType.Input)
                                         {
                                             newLink.consumerElement = this;
                                         }
                                         else
                                         {
                                             newLink.supplierElement = this;
                                         }
                                         newLink.update();
                                     });
        }

        if (displayedNode instanceof RecipeNode || displayedNode instanceof SupplyNode)
        {
            assemblerInfoElement = new AssemblerInfoElement(new MachinePermutation(DataCache.assemblers.get("assembling-machine-1"),
                                                                                   Arrays.asList(DataCache.modules.get("none"))), 0, parent);
            subElements.add(assemblerInfoElement);
        }

        background = new Rectangle();
        text = new Text();
        this.getChildren().addAll(background, text);

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, parent.nodeEventHandler.getOnMousePressed());
//        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, parent.nodeEventHandler.getOnMouseDragged());
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, parent.nodeEventHandler.getOnMouseReleased());
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, parent.nodeEventHandler.getOnMouseEntered());
        this.addEventFilter(MouseEvent.MOUSE_EXITED, parent.nodeEventHandler.getOnMouseExited());
    }

    @Override
    public void setupElements()
    {
        super.setupElements();
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.setStroke(backgroundPaint);
        background.setFill(backgroundPaint);

        text.setText(textValue);

        width = Math.max(Math.max(75, getIconWidths()), (int) text.getLayoutBounds().getWidth() + 8);
        height = 80;

        text.setTranslateX((getWidth() - this.text.getLayoutBounds().getWidth()) / 2);
        text.setTranslateY(getHeight() / 2 + 3);

        if (assemblerInfoElement != null)
        {
            if ((displayedNode instanceof RecipeNode && parent.showAssemblers) ||
                (displayedNode instanceof SupplyNode && parent.showMiners))
            {
                height = 120;
                if (displayedNode instanceof RecipeNode)
                {
                    assemblerInfoElement.assemblerList = ((RecipeNode) displayedNode).getMinimumAssemblersList();
                }
                else if (displayedNode instanceof SupplyNode)
                {
                    assemblerInfoElement.assemblerList = ((SupplyNode) displayedNode).getMinimumMiners();
                }
                assemblerInfoElement.update();
                width = Math.max(getWidth(), assemblerInfoElement.getWidth() + 20);
                height = assemblerInfoElement.getHeight() + 60;
                assemblerInfoElement.setX((getWidth() - assemblerInfoElement.getWidth()) / 2);
                assemblerInfoElement.setY((getHeight() - assemblerInfoElement.getHeight()) / 2 - 1);
            }
            else
            {
                assemblerInfoElement.setVisible(false);
                width = Math.max(100, getWidth());
                height = 90;
            }
        }
        else
        {
            height = 90;
        }

        background.setWidth(width);
        background.setHeight(height);

        updateTabOrder();
    }

    @Override
    public void update()
    {
        updateTabOrder();

        if (displayedNode instanceof SupplyNode)
        {
            SupplyNode node = (SupplyNode) displayedNode;
            if (!parent.showMiners)
            {
                if (node.getSuppliedItem().isMissingItem)
                {
                    textValue = String.format("Item not loaded! (%s)", node.getDisplayName());
                }
                else
                {
                    textValue = "Input: " + node.getSuppliedItem().getLocalizedName();
                }
            }
            else
            {
                textValue = "";
            }
        }
        else if (displayedNode instanceof ConsumerNode)
        {
            ConsumerNode node = (ConsumerNode) displayedNode;
            if (node.getConsumedItem().isMissingItem)
            {
                textValue = String.format("Item not loaded! (%s)", node.getDisplayName());
            }
            else
            {
                textValue = "Output: " + node.getConsumedItem().getLocalizedName();
            }
        }
        else if (displayedNode instanceof RecipeNode)
        {
            RecipeNode node = (RecipeNode) displayedNode;
            if (!parent.showAssemblers)
            {
                if (node.getBaseRecipe().isMissingRecipe)
                {
                    textValue = String.format("Recipe not loaded! (%s)", node.getDisplayName());
                }
                else
                {
                    textValue = "Recipe: " + node.getBaseRecipe().getLocalizedName();
                }
            }
            else
            {
                textValue = "";
            }
        }

        int minWidth = (int) text.getLayoutBounds().getWidth();

        width = Math.max(minWidth, Math.max(75, getIconWidths()));

//        for (ItemTab tab : Util.union(inputTabs, outputTabs))
//        {
//            tab.setBackground(chooseIconColour(tab.getItem(), tab.type));
//            tab.setText(getIconString(tab.getItem(), tab.type));
//        }

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
            result += tab.getWidth() + tabPadding;
        }
        return result;
    }

    private int getOutputIconWidths()
    {
        int result = tabPadding;
        for (ItemTab tab : outputTabs)
        {
            result += tab.getWidth() + tabPadding;
        }
        return result;
    }

    public void updateTabOrder()
    {
//        inputTabs = inputTabs.sort(it -> getItemTabXHeuristic(it)).ToList();
//        outputTabs = outputTabs.sort(it -> getItemTabXHeuristic(it)).ToList();

        int x = (getWidth() - getOutputIconWidths()) / 2;
        for (ItemTab tab : outputTabs)
        {
            x += tabPadding;
            tab.setTranslateX(x);
            tab.setTranslateY(-tab.getHeight() / 2);
            x += tab.getWidth();
        }
        x = (getWidth() - getInputIconWidths()) / 2;
        for (ItemTab tab : inputTabs)
        {
            x += tabPadding;
            tab.setTranslateX(x);
            tab.setTranslateY(getHeight() - tab.getHeight() / 2);
            x += tab.getWidth();
        }
    }

    private Color chooseIconColor(Item item, LinkType linkType)
    {
        Color notEnough = Color.SALMON;
        Color enough = Color.WHITE;
        Color tooMuch = Color.LIGHTBLUE;

        if (linkType == LinkType.Input)
        {
            if (displayedNode.getTotalDemand(item) <= displayedNode.getTotalInput(item)
                && displayedNode.getTotalDemand(item) != Float.MAX_VALUE)
            {
                return enough;
            }
            else
            {
                return notEnough;
            }
        }
        else
        {
            if (displayedNode.getTotalOutput(item) > displayedNode.getRequiredOutput(item))
            {
                return tooMuch;
            }
            else
            {
                return enough;
            }
        }
    }

    public Point2D getOutputLineConnectionPoint(Item item)
    {
        if (outputTabs.isEmpty())
        {
            return new Point2D(getX() + getWidth() / 2, getY());
        }
        ItemTab itemTab = null;
        for (ItemTab tab : outputTabs)
        {
            if (tab.getItem() == item)
            {
                itemTab = tab;
                break;
            }
        }
        return new Point2D(getX() + itemTab.getX() + itemTab.getWidth() / 2, getY() + itemTab.getY());
    }

    public Point2D getInputLineConnectionPoint(Item item)
    {
        if (inputTabs.isEmpty())
        {
            return new Point2D(getX() + getWidth() / 2, getY() + getHeight());
        }
        ItemTab itemTab = null;
        for (ItemTab tab : inputTabs)
        {
            if (tab.getItem() == item)
            {
                itemTab = tab;
                break;
            }
        }
        return new Point2D(getX() + itemTab.getX() + itemTab.getWidth() / 2, getY() + itemTab.getY() + itemTab.getHeight());
    }
}
