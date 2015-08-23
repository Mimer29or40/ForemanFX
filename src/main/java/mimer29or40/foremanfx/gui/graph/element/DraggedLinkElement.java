package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.Item;

public class DraggedLinkElement extends GraphElement
{
    public NodeElement supplierElement;
    public NodeElement consumerElement;
    public Item        item;
    public LinkType    startConnectionType;
    public DragType    dragType;

    public Point2D location;
    public int     x;
    public int     y;
    public Point2D size;
    public int     width;
    public int     height;

    public DraggedLinkElement(ProductionGraphViewer parent, NodeElement startNode, LinkType startConnectionType,
                              Item item)
    {
        super(parent);
        if (startConnectionType == LinkType.Input)
        { consumerElement = startNode; }
        else
        { supplierElement = startNode; }
        this.startConnectionType = startConnectionType;
        this.item = item;
//        if ((Control.MouseButtons & MouseButtons.Left) != 0)
//        {
//            dragType = DragType.MouseDown;
//        }
//        else
//        {
//            dragType = DragType.MouseUp;
//        }
    }
//
//    @Override
//    public void paint(GraphicsContext graphics)
//    {
//        Point2D pointN = parent.screenToLocal(MouseInfo.getPointerInfo().getLocation().getX(),
//                                              MouseInfo.getPointerInfo().getLocation().getY());
//        Point2D pointM = pointN;
//
//        if (supplierElement != null)
//            pointN = supplierElement.getOutputLineConnectionPoint(item);
//        if (consumerElement != null)
//            pointM = consumerElement.getInputLineConnectionPoint(item);
//        Point2D pointN2 = new Point2D(pointN.getX(),
//                                      pointN.getY() - Math.max((int)((pointN.getY() - pointM.getY()) / 2), 40));
//        Point2D pointM2 = new Point2D(pointM.getX(),
//                                      pointM.getY() + Math.max((int)((pointN.getY() - pointM.getY()) / 2), 40));
//        graphics.beginPath();
//        graphics.moveTo(pointN.getX(), pointN.getY());
//        graphics.lineTo(pointM.getX(), pointM.getY());
//        graphics.fill();
////        graphics.bezierCurveTo(pointN, pointN2, pointM, pointM2);
//    }
//
//    @Override
//    public boolean containsPoint(Point2D point)
//    {
//        return true;
//    }
//
//    private void endDrag(Point2D point)
//    {
//        if (supplierElement != null && consumerElement != null)
//        {
//            if (startConnectionType == LinkType.Input)
//            {
//                NodeLink.create(supplierElement.displayedNode, consumerElement.DisplayedNode, Item,
//                                ConsumerElement.DisplayedNode.GetUnsatisfiedDemand(Item));
//            }
//            else
//            {
//                NodeLink.Create(SupplierElement.DisplayedNode, ConsumerElement.DisplayedNode, Item,
// SupplierElement.DisplayedNode.GetExcessOutput(Item));
//            }
//        }
//        else if (StartConnectionType == LinkType.Output && ConsumerElement == null)
//        {
//            List<ChooserControl> recipeOptionList = new List<ChooserControl>();
//            recipeOptionList.Add(new ItemChooserControl(Item, "Create output node", Item.FriendlyName));
//            foreach (Recipe recipe in DataCache.Recipes.Values.Where(r => r.Ingredients.Keys.Contains(Item)))
//            {
//                recipeOptionList.Add(new RecipeChooserControl(recipe, "Use recipe " + recipe.FriendlyName,
// recipe.FriendlyName));
//            }
//
//            var chooserPanel = new ChooserPanel(recipeOptionList, Parent);
//            chooserPanel.Show(c =>
//            {
//            if (c != null)
//            {
//                NodeElement newElement = null;
//                if (c is RecipeChooserControl)
//                {
//                    Recipe selectedRecipe = (c as RecipeChooserControl).DisplayedRecipe;
//                    newElement = new NodeElement(RecipeNode.Create(selectedRecipe, Parent.Graph), Parent);
//                }
//                else if (c is ItemChooserControl)
//                {
//                    Item selectedItem = (c as ItemChooserControl).DisplayedItem;
//                    newElement = new NodeElement(ConsumerNode.Create(selectedItem, Parent.Graph), Parent);
//                    (newElement.DisplayedNode as ConsumerNode).rateType = RateType.Auto;
//                }
//                newElement.Update();
//                newElement.Location = Point.Add(location, new Size(-newElement.Width / 2, -newElement.Height / 2));
//                new LinkElement(Parent, NodeLink.Create(SupplierElement.DisplayedNode, newElement.DisplayedNode,
// Item));
//            }
//
//            Parent.Graph.UpdateNodeValues();
//            Parent.AddRemoveElements();
//            Parent.UpdateNodes();
//            });
//
//        }
//        else if (StartConnectionType == LinkType.Input && SupplierElement == null)
//        {
//            List<ChooserControl> recipeOptionList = new List<ChooserControl>();
//            recipeOptionList.Add(new ItemChooserControl(Item, "Create infinite supply node", Item.FriendlyName));
//            foreach (Recipe recipe in DataCache.Recipes.Values.Where(r => r.Results.Keys.Contains(Item)))
//            {
//                recipeOptionList.Add(new RecipeChooserControl(recipe, "Use recipe " + recipe.FriendlyName,
// recipe.FriendlyName));
//            }
//
//            var chooserPanel = new ChooserPanel(recipeOptionList, Parent);
//
//            chooserPanel.Show(c =>
//            {
//            if (c != null)
//            {
//                NodeElement newElement = null;
//                if (c is RecipeChooserControl)
//                {
//                    Recipe selectedRecipe = (c as RecipeChooserControl).DisplayedRecipe;
//                    newElement = new NodeElement(RecipeNode.Create(selectedRecipe, Parent.Graph), Parent);
//                }
//                else if (c is ItemChooserControl)
//                {
//                    Item selectedItem = (c as ItemChooserControl).DisplayedItem;
//                    newElement = new NodeElement(SupplyNode.Create(selectedItem, Parent.Graph), Parent);
//                }
//                newElement.Update();
//                newElement.Location = Point.Add(location, new Size(-newElement.Width / 2, -newElement.Height / 2));
//                new LinkElement(Parent, NodeLink.Create(newElement.DisplayedNode, ConsumerElement.DisplayedNode,
// Item));
//            }
//
//            Parent.Graph.UpdateNodeValues();
//            Parent.AddRemoveElements();
//            Parent.UpdateNodes();
//            });
//        }
//
//        Parent.Graph.UpdateNodeValues();
//        Parent.AddRemoveElements();
//        Parent.UpdateNodes();
//        Dispose();
//    }
}
