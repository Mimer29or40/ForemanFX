package mimer29or40.foremanfx.gui.graph.element;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.Item;

public class ItemTab extends GraphElement
{
    public LinkType type;

    public Color  fillColor;
    public Color  borderColor;
    public String textValue;

    private Item item;

    private Rectangle base;
    private Text      text;
    private ImageView itemIcon;

    public ItemTab(Item item, LinkType type, ProductionGraphViewer parent)
    {
        super(parent);
        this.item = item;
        this.type = type;
        fillColor = Color.WHITE;
        borderColor = Color.GREY;
        width = 40;
        height = 56;

        base = new Rectangle(width, height);
        text = new Text();
        itemIcon = new ImageView(item.getIcon() != null ? item.getIcon() : DataCache.unknownIcon);

        this.getChildren().addAll(base, text, itemIcon);
    }

    @Override
    public void draw()
    {
        base.setStroke(borderColor);
        base.setFill(fillColor);
        base.setArcWidth(10);
        base.setArcHeight(10);

        text.setText(textValue);
        text.setTranslateX((width - text.getLayoutBounds().getWidth()) / 2);
        text.setFont(new Font(10));

        itemIcon.setTranslateX(4);

        if (type == LinkType.Output)
        {
            text.setTranslateY(text.getLayoutBounds().getHeight());
            itemIcon.setTranslateY(height - 4 - 32);
        }
        else
        {
            text.setTranslateY(height - 4);
            itemIcon.setTranslateY(4);
        }
    }

    @Override
    public void update()
    {

    }

    public void setText(String text)
    {
        textValue = text;
        this.text.setText(text);
        this.text.setTranslateX((width - this.text.getLayoutBounds().getWidth()) / 2);
    }

    public void setBackground(Color color)
    {
        base.setFill(color);
    }

    public Item getItem()
    {
        return item;
    }
}
