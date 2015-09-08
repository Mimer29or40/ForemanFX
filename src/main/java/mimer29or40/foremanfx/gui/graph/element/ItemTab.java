package mimer29or40.foremanfx.gui.graph.element;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    public DraggedLinkElement draggedLinkElement;

    public Color fillColor;
    public Color borderColor;

    private int borderSize = 4;

    public StringProperty textValue = new SimpleStringProperty(this, "text", "0");

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
        width.set(40);
        height.set(52);

        base = new Rectangle();
        text = new Text();
        itemIcon = new ImageView(item.getIcon() != null ? item.getIcon() : DataCache.unknownIcon);

        this.getChildren().addAll(base, text, itemIcon);
    }

    @Override
    public void setupElements()
    {
        super.setupElements();
        base.widthProperty().bind(width);
        base.heightProperty().bind(height);
        base.setStroke(borderColor);
        base.setFill(fillColor);
        base.setArcWidth(10);
        base.setArcHeight(10);

        text.textProperty().bind(textValue);

        int fontSize = 10;
        text.setFont(new Font(fontSize));
        while (this.text.getLayoutBounds().getWidth() > getWidth())
        {
            fontSize -= 0.5;
            this.text.setFont(new Font(fontSize));
        }

        text.setTranslateX((getWidth() - text.getLayoutBounds().getWidth()) / 2);
        itemIcon.setTranslateX(borderSize);

        if (type == LinkType.Output)
        {
            text.setTranslateY(getHeight() - borderSize - itemIcon.getLayoutBounds().getHeight() - 5);
            itemIcon.setTranslateY(getHeight() - borderSize - itemIcon.getLayoutBounds().getHeight());
        }
        else
        {
            text.setTranslateY(getHeight() - borderSize);
            itemIcon.setTranslateY(borderSize);
        }
    }

    @Override
    public void update()
    {

    }

    public void setText(String text)
    {
        textValue.set(text);
        if (this.text.getLayoutBounds().getWidth() > getWidth())
        { this.text.setFont(new Font(8)); }
        this.text.setTranslateX((getWidth() - this.text.getLayoutBounds().getWidth()) / 2);
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
