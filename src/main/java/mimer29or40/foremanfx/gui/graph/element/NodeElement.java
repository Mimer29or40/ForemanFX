package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.gui.node.ProductionNode;

public class NodeElement extends GraphElement
{
    public int dragOffsetX;
    public int dragOffsetY;

    public Point2D mousePosition = Point2D.ZERO;

    private Color recipeColor    = Color.color(190, 217, 212);
    private Color supplyColour   = Color.color(249, 237, 195);
    private Color consumerColour = Color.color(231, 214, 224);
    private Color missingColour  = Color.color(0xff, 0x7f, 0x6b);
    private Color backgroundColor;

    private final int assemblerSize    = 32;
    private final int assemblerBorderX = 10;
    private final int assemblerBorderY = 30;

    private final int tabPadding = 8;

    public String text = "";

    private AssemblerBox assemblerBox;

//    private List<ItemTab> inputTabs  = new List<ItemTab>();
//    private List<ItemTab> outputTabs = new List<ItemTab>();

    private ContextMenu rightClickMenu = new ContextMenu();

    public ProductionNode DisplayedNode;

    //    private Brush backgroundBrush;
    private Font size10Font = new Font("GenericSansSerif", 10);
//    private StringFormat centreFormat = new StringFormat();

    public Boolean tooltipsEnabled = true;

    public NodeElement(ProductionNode node, ProductionGraphViewer parent)
    {
        super(parent);
        width = 100;
        height = 90;

        DisplayedNode = node;

//        if (DisplayedNode is ConsumerNode)
//        {
//            backgroundColour = supplyColour;
//            if (((ConsumerNode) DisplayedNode).ConsumedItem.IsMissingItem)
//            {
//                backgroundColour = missingColour;
//            }
//        }
//        else if (DisplayedNode is SupplyNode)
//        {
//            backgroundColour = consumerColour;
//            if (((SupplyNode) DisplayedNode).SuppliedItem.IsMissingItem)
//            {
//                backgroundColour = missingColour;
//            }
//        }
//        else if (DisplayedNode is RecipeNode)
//        {
//            backgroundColour = recipeColour;
//            if (((RecipeNode) DisplayedNode).BaseRecipe.IsMissingRecipe)
//            {
//                backgroundColour = missingColour;
//            }
//        }
//        backgroundBrush = new SolidBrush(backgroundColour);
//
//        foreach (Item item in node.Inputs)
//        {
//            ItemTab newTab = new ItemTab(item, LinkType.Input, Parent);
//            SubElements.Add(newTab);
//            inputTabs.Add(newTab);
//        }
//        foreach (Item item in node.Outputs)
//        {
//            ItemTab newTab = new ItemTab(item, LinkType.Output, Parent);
//            SubElements.Add(newTab);
//            outputTabs.Add(newTab);
//        }
//
//        if (DisplayedNode is RecipeNode || DisplayedNode is SupplyNode)
//        {
//            assemblerBox = new AssemblerBox(Parent);
//            SubElements.Add(assemblerBox);
//            assemblerBox.Height = assemblerBox.Width = 50;
//        }
//
//        centreFormat.Alignment = centreFormat.LineAlignment = StringAlignment.Center;
    }

//    private int getIconWidths()
//    {
//        return Math.Max(GetInputIconWidths(), GetOutputIconWidths());
//    }
//
//    private int GetInputIconWidths()
//    {
//        int result = tabPadding;
//        foreach (ItemTab tab in inputTabs)
//        {
//            result += tab.Width + tabPadding;
//        }
//        return result;
//    }
//    private int GetOutputIconWidths()
//    {
//        int result = tabPadding;
//        foreach (ItemTab tab in outputTabs)
//        {
//            result += tab.Width + tabPadding;
//        }
//        return result;
//    }
//
//    public void Update()
//    {
//        UpdateTabOrder();
//
//        if (DisplayedNode is SupplyNode)
//        {
//            SupplyNode node = (SupplyNode)DisplayedNode;
//            if (!Parent.ShowMiners)
//            {
//                if (node.SuppliedItem.IsMissingItem)
//                {
//                    text = String.Format("Item not loaded! ({0})", node.DisplayName);
//                }
//                else
//                {
//                    text = "Input: " + node.SuppliedItem.FriendlyName;
//                }
//            }
//            else
//            {
//                text = "";
//            }
//        }
//        else if (DisplayedNode is ConsumerNode)
//        {
//            ConsumerNode node = (ConsumerNode)DisplayedNode;
//            if (node.ConsumedItem.IsMissingItem)
//            {
//                text = String.Format("Item not loaded! ({0})", node.DisplayName);
//            }
//            else
//            {
//                text = "Output: " + node.ConsumedItem.FriendlyName;
//            }
//        }
//        else if (DisplayedNode is RecipeNode)
//        {
//            RecipeNode node = (RecipeNode)DisplayedNode;
//            if (!Parent.ShowAssemblers)
//            {
//                if (node.BaseRecipe.IsMissingRecipe)
//                {
//                    text = String.Format("Recipe not loaded! ({0})", node.DisplayName);
//                }
//                else
//                {
//                    text = "Recipe: " + node.BaseRecipe.FriendlyName;
//                }
//            }
//            else
//            {
//                text = "";
//            }
//        }
//
//        Graphics graphics = Parent.CreateGraphics();
//        int minWidth = (int)graphics.MeasureString(text, size10Font).Width;
//
//        Width = Math.Max(75, getIconWidths());
//        Width = Math.Max(Width, minWidth);
//
//        if (assemblerBox != null)
//        {
//            if ((DisplayedNode is RecipeNode && Parent.ShowAssemblers)
//            || (DisplayedNode is SupplyNode && Parent.ShowMiners))
//            {
//                Height = 120;
//                if (DisplayedNode is RecipeNode)
//                {
//                    assemblerBox.AssemblerList = (DisplayedNode as RecipeNode).GetMinimumAssemblers();
//                }
//                else if (DisplayedNode is SupplyNode)
//                {
//                    assemblerBox.AssemblerList = (DisplayedNode as SupplyNode).GetMinimumMiners();
//                }
//                assemblerBox.Update();
//                Width = Math.Max(Width, assemblerBox.Width + 20);
//                Height = assemblerBox.Height + 80;
//                assemblerBox.X = (Width - assemblerBox.Width) / 2 + 2;
//                assemblerBox.Y = (Height - assemblerBox.Height) / 2 + 2;
//            }
//            else
//            {
//                assemblerBox.AssemblerList.Clear();
//                Width = Math.Max(100, Width);
//                Height = 90;
//                assemblerBox.Update();
//            }
//        }
//
//        else
//        {
//            Height = 90;
//        }
//
//        foreach (ItemTab tab in inputTabs.Union(outputTabs))
//        {
//            tab.FillColour = chooseIconColour(tab.Item, tab.Type);
//            tab.Text = getIconString(tab.Item, tab.Type);
//        }
//
//        UpdateTabOrder();
//    }
//
//    public void UpdateTabOrder()
//    {
//        inputTabs = inputTabs.OrderBy(it => GetItemTabXHeuristic(it)).ToList();
//        outputTabs = outputTabs.OrderBy(it => GetItemTabXHeuristic(it)).ToList();
//
//        int x = (Width - GetOutputIconWidths()) / 2;
//        foreach (ItemTab tab in outputTabs)
//        {
//            x += tabPadding;
//            tab.X = x;
//            tab.Y = -tab.Height / 2;
//            x += tab.Width;
//        }
//        x = (Width - GetInputIconWidths()) / 2;
//        foreach (ItemTab tab in inputTabs)
//        {
//            x += tabPadding;
//            tab.X = x;
//            tab.Y = Height - tab.Height / 2;
//            x += tab.Width;
//        }
//    }
//
//    public int GetItemTabXHeuristic(ItemTab tab)
//    {
//        int total = 0;
//        if (tab.Type == LinkType.Input)
//        {
//            foreach (NodeLink link in DisplayedNode.InputLinks.Where(l => l.Item == tab.Item))
//            {
//                NodeElement node = Parent.GetElementForNode(link.Supplier);
//                Point diff = Point.Subtract(new Point(node.Location.X + node.Width / 2,
// node.Location.Y + node.Height / 2), new Size(Location.X + Width / 2, Location.Y + Height / 2));
//                diff.Y = Math.Max(0, diff.Y);
//                total += Convert.ToInt32(Math.Atan2(diff.X, diff.Y) * 1000);
//            }
//        }
//        else
//        {
//            foreach (NodeLink link in DisplayedNode.OutputLinks.Where(l => l.Item == tab.Item))
//            {
//                NodeElement node = Parent.GetElementForNode(link.Consumer);
//                Point diff = Point.Subtract(new Point(node.Location.X + node.Width / 2,
// -node.Location.Y + -node.Height / 2), new Size(Location.X + Width / 2, -Location.Y + -Height / 2));
//                diff.Y = Math.Max(0, diff.Y);
//                total += Convert.ToInt32(Math.Atan2(diff.X, diff.Y) * 1000);
//            }
//        }
//        return total;
//    }
//
//    public Point GetOutputLineConnectionPoint(Item item)
//    {
//        if (!outputTabs.Any())
//        {
//            return new Point(X + Width / 2, Y);
//        }
//        ItemTab tab = outputTabs.First(it => it.Item == item);
//        return new Point(X + tab.X + tab.Width / 2, Y + tab.Y);
//    }
//
//    public Point GetInputLineConnectionPoint(Item item)
//    {
//        if (!inputTabs.Any())
//        {
//            return new Point(X + Width / 2, Y + Height);
//        }
//        ItemTab tab = inputTabs.First(it => it.Item == item);
//        return new Point(X + tab.X + tab.Width / 2, Y + tab.Y + tab.Height);
//    }
//
//    public override void Paint(Graphics graphics)
//{
//    GraphicsStuff.FillRoundRect(0, 0, Width, Height, 8, graphics, backgroundBrush);
//    graphics.DrawString(text, size10Font, Brushes.White, Width / 2, Height / 2, centreFormat);
//
//    base.Paint(graphics);
//}
//
//    private String getIconString(Item item, LinkType linkType)
//    {
//        String line1Format = "{0:0.##}{1}";
//        String line2Format = "\n({0:0.##}{1})";
//        String finalString = "";
//
//        String unit = "";
//
//        float actualAmount = 0;
//        float desiredAmount = 0;
//        if (linkType == LinkType.Input)
//        {
//            actualAmount = DisplayedNode.GetTotalInput(item);
//            desiredAmount = DisplayedNode.GetTotalDemand(item);
//        }
//        else
//        {
//            actualAmount = DisplayedNode.GetTotalOutput(item);
//        }
//        if (Parent.Graph.SelectedAmountType == AmountType.Rate && Parent.Graph.SelectedUnit == RateUnit.PerSecond)
//        {
//            unit = "/s";
//        }
//        else if (Parent.Graph.SelectedAmountType == AmountType.Rate && Parent.Graph.SelectedUnit == RateUnit
// .PerMinute)
//        {
//            unit = "/m";
//            actualAmount *= 60;
//            desiredAmount *= 60;
//        }
//
//        if (linkType == LinkType.Input)
//        {
//            finalString = String.Format(line1Format, actualAmount, unit);
//            if (DisplayedNode.GetTotalDemand(item) > DisplayedNode.GetTotalInput(item)
//                && DisplayedNode.GetTotalDemand(item) != float.PositiveInfinity)
//            {
//                finalString += String.Format(line2Format, desiredAmount, unit);
//            }
//        }
//        else
//        {
//            finalString = String.Format(line1Format, actualAmount, unit);
//        }
//
//        return finalString;
//    }
//
//    private Color chooseIconColour(Item item, LinkType linkType)
//    {
//        Color notEnough = Color.FromArgb(255, 255, 193, 193);
//        Color enough = Color.White;
//        Color tooMuch = Color.FromArgb(255, 214, 226, 230);
//
//        if (linkType == LinkType.Input)
//        {
//            if (DisplayedNode.GetTotalDemand(item) <= DisplayedNode.GetTotalInput(item)
//                && DisplayedNode.GetTotalDemand(item) != float.PositiveInfinity)
//            {
//                return enough;
//            }
//            else
//            {
//                return notEnough;
//            }
//        }
//        else
//        {
//            if (DisplayedNode.GetTotalOutput(item) > DisplayedNode.GetRequiredOutput(item))
//            {
//                return tooMuch;
//            }
//            else
//            {
//                return enough;
//            }
//        }
//    }
//
//    public override void MouseUp(Point location, MouseButtons button)
//{
//    if (Parent.DraggedElement == this && !Parent.clickHasBecomeDrag)
//    {
//        if (button == MouseButtons.Left)
//        {
//            beginEditingNodeRate();
//        }
//    }
//
//    if (button == MouseButtons.Right)
//    {
//        rightClickMenu.MenuItems.Clear();
//        rightClickMenu.MenuItems.Add(new MenuItem("Delete node",
//                                                  new EventHandler((o, e) =>
//                                     {
//                                             Parent.DeleteNode(this);
//        })));
//        rightClickMenu.Show(Parent, Parent.GraphToScreen(Point.Add(location, new Size(X, Y))));
//    }
//}
//
//    public void beginEditingNodeRate()
//    {
//        RateOptionsPanel newPanel = new RateOptionsPanel(DisplayedNode, Parent);
//        new FloatingTooltipControl(newPanel, Direction.Right, new Point(Location.X, Location.Y + Height / 2), Parent);
//    }
//
//    public override void MouseMoved(Point location)
//{
//    ItemTab mousedTab = null;
//    foreach (ItemTab tab in SubElements.OfType<ItemTab>())
//    {
//        if (tab.bounds.Contains(location))
//        {
//            mousedTab = tab;
//        }
//    }
//
//    if (tooltipsEnabled)
//    {
//        TooltipInfo tti = new TooltipInfo();
//        if (mousedTab != null)
//        {
//            tti.Text = mousedTab.Item.FriendlyName;
//            if (mousedTab.Type == LinkType.Input)
//            {
//                tti.Text += "\nDrag to create a new connection";
//                tti.Direction = Direction.Up;
//                tti.ScreenLocation = Parent.GraphToScreen(GetInputLineConnectionPoint(mousedTab.Item));
//            }
//            else
//            {
//                tti.Text = mousedTab.Item.FriendlyName;
//                tti.Text += "\nDrag to create a new connection";
//                tti.Direction = Direction.Down;
//                tti.ScreenLocation = Parent.GraphToScreen(GetOutputLineConnectionPoint(mousedTab.Item));
//            }
//            Parent.AddTooltip(tti);
//        }
//        else if (DisplayedNode is RecipeNode)
//        {
//            tti.Direction = Direction.Left;
//            tti.ScreenLocation = Parent.GraphToScreen(Point.Add(Location, new Size(Width, Height / 2)));
//            tti.Text = String.Format("Recipe: {0}", (DisplayedNode as RecipeNode).BaseRecipe.FriendlyName);
//            tti.Text += String.Format("\n--Base Time: {0}s", (DisplayedNode as RecipeNode).BaseRecipe.Time);
//            tti.Text += String.Format("\n--Base Ingredients:");
//            foreach (var kvp in (DisplayedNode as RecipeNode).BaseRecipe.Ingredients)
//            {
//                tti.Text += String.Format("\n----{0} ({1})", kvp.Key.FriendlyName, kvp.Value.ToString());
//            }
//            tti.Text += String.Format("\n--Base Results:");
//            foreach (var kvp in (DisplayedNode as RecipeNode).BaseRecipe.Results)
//            {
//                tti.Text += String.Format("\n----{0} ({1})", kvp.Key.FriendlyName, kvp.Value.ToString());
//            }
//            if (Parent.ShowAssemblers)
//            {
//                tti.Text += String.Format("\n\nAssemblers:");
//                foreach (var kvp in assemblerBox.AssemblerList)
//                {
//                    tti.Text += String.Format("\n----{0} ({1})", kvp.Key.assembler.FriendlyName,
// kvp.Value.ToString());
//                    foreach (var Module in kvp.Key.modules.Where(m => m != null))
//                    {
//                        tti.Text += String.Format("\n------{0}", Module.FriendlyName);
//                    }
//                }
//            }
//            Parent.AddTooltip(tti);
//        }
//
//        TooltipInfo helpToolTipInfo = new TooltipInfo();
//        helpToolTipInfo.Text = "Left click on this node to edit how fast it runs\nRight click to delete it";
//        helpToolTipInfo.Direction = Direction.None;
//        helpToolTipInfo.ScreenLocation = new Point(10, 10);
//        Parent.AddTooltip(helpToolTipInfo);
//    }
//}
//
//    public override bool ContainsPoint(Point point)
//{
//    if (new Rectangle(0, 0, Width, Height).Contains(point.X, point.Y))
//    {
//        return true;
//    }
//    foreach (ItemTab tab in SubElements.OfType<ItemTab>())
//    {
//        if (tab.bounds.Contains(point))
//        {
//            return true;
//        }
//    }
//    return false;
//}
//
//    public override void MouseDown(Point location, MouseButtons button)
//{
//    if (button == MouseButtons.Left)
//    {
//        Parent.DraggedElement = this;
//        DragOffsetX = location.X;
//        DragOffsetY = location.Y;
//    }
//}
//
//    public override void Dragged(Point location)
//{
//    ItemTab draggedTab = null;
//
//    foreach (ItemTab tab in SubElements.OfType<ItemTab>())
//    {
//        if (tab.bounds.Contains(new Point(DragOffsetX, DragOffsetY)))
//        {
//            draggedTab = tab;
//        }
//    }
//
//    if (draggedTab != null)
//    {
//        DraggedLinkElement newLink = new DraggedLinkElement(Parent, this, draggedTab.Type, draggedTab.Item);
//        if (draggedTab.Type == LinkType.Input)
//        {
//            newLink.ConsumerElement = this;
//        }
//        else
//        {
//            newLink.SupplierElement = this;
//        }
//        Parent.DraggedElement = newLink;
//    }
//    else
//    {
//        X += location.X - DragOffsetX;
//        Y += location.Y - DragOffsetY;
//
//        foreach (ProductionNode node in DisplayedNode.InputLinks.Select<NodeLink, ProductionNode>(l => l.Supplier))
//        {
//            Parent.GetElementForNode(node).UpdateTabOrder();
//        }
//        foreach (ProductionNode node in DisplayedNode.OutputLinks.Select<NodeLink, ProductionNode>(l => l.Consumer))
//        {
//            parent.getElementForNode(node).UpdateTabOrder();
//        }
//    }
//}
//
//    @Override
//    public void dispose()
//    {
//        size10Font.dispose();
//        centreFormat.dispose();
//        backgroundBrush.dispose();
//        if (assemblerBox != null)
//        {
//            assemblerBox.dispose();
//        }
//        rightClickMenu.dispose();
//        super.dispose();
//    }
}
