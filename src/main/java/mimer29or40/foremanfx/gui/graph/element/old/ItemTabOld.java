package mimer29or40.foremanfx.gui.graph.element.old;

import com.sun.javafx.geom.Shape;
import javafx.scene.text.Font;
import mimer29or40.foremanfx.gui.graph.ProductionGraphPaneOld;
import mimer29or40.foremanfx.gui.graph.element.LinkType;
import mimer29or40.foremanfx.model.Item;

public class ItemTabOld extends GraphElementOld
{
    public LinkType Type;

    //    private final int iconSize = 32;
//    private final int border = 4;
//    private int textHeight = 11;
//    private StringFormat centreFormat = new StringFormat();
//    private Pen borderPen = new Pen(Color.Gray, 3);
//    private Brush textBrush = new SolidBrush(Color.Black);
//    private Brush fillBrush;
//
//    private Color fillColour;
//    public Color FillColour
//    {
//        get
//        {
//            return fillColour;
//        }
//        set {
//        fillColour = value;
//        if (fillBrush != null)
//        {
//            fillBrush.Dispose();
//        }
//        fillBrush = new SolidBrush(value);
//    }
//    }
    private String text;
//    {
//        get { return text; }
//        set
//        {
//            text = value;
//            textHeight = (int)parent.CreateGraphics().MeasureString(value, font).Height;
//        }
//    }

    public Font font = new Font("GenericSansSerif", 7);
    private Item item;

    public ItemTabOld(Item item, LinkType type, ProductionGraphPaneOld parent)
    {
        super(parent);
        this.item = item;
        this.Type = type;
//        centreFormat.Alignment = centreFormat.LineAlignment = StringAlignment.Center;
//        FillColour = Color.White;
    }

//    public override System.Drawing.Point Size
//    {
//        get
//        {
//            return new Point(iconSize + border * 3, iconSize + textHeight + border * 3);
//        }
//        set
//        {
//        }
//    }
//
//    public override void Paint(Graphics graphics)
//    {
//        Point iconPoint = Point.Empty;
//        if (Type == LinkType.Output)
//        {
//            iconPoint = new Point((int)(border * 1.5), Height - (int)(border * 1.5) - iconSize);
//        }
//        else
//        {
//            iconPoint = new Point((int)(border * 1.5), (int)(border * 1.5));
//        }
//
//        if (Type == LinkType.Output)
//        {
//            GraphicsStuff.FillRoundRect(0, 0, Width, Height, border, graphics, fillBrush);
//            GraphicsStuff.DrawRoundRect(0, 0, Width, Height, border, graphics, borderPen);
//            graphics.DrawString(Text, font, textBrush, new PointF(Width / 2, (textHeight + border) / 2),
// centreFormat);
//        }
//        else
//        {
//            GraphicsStuff.FillRoundRect(0, 0, Width, Height, border, graphics, fillBrush);
//            GraphicsStuff.DrawRoundRect(0, 0, Width, Height, border, graphics, borderPen);
//            graphics.DrawString(Text, font, textBrush, new PointF(Width / 2, Height - (textHeight + border) / 2),
// centreFormat);
//        }
//        graphics.DrawImage(Item.Icon ?? DataCache.UnknownIcon, iconPoint.X, iconPoint.Y, iconSize, iconSize);
//    }
//
//    public override void Dispose()
//    {
//        textBrush.Dispose();
//        fillBrush.Dispose();
//        centreFormat.Dispose();
//        borderPen.Dispose();
//        font.Dispose();
//        base.Dispose();
//    }

    @Override
    public Shape impl_configShape()
    {
        return null;

    }
}
