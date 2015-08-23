package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.MachinePermutation;

public class AssemblerIconElement extends GraphElement
{
    final int maxFontSize = 14;
    public  MachinePermutation displayedMachine;
    private int                displayedNumber;
    private      float         stringWidth = 0F;
    //    private StringFormatter stringFormat = new StringFormatter();
    private      TextAlignment alignment   = TextAlignment.CENTER;
    public final int           iconSize    = 32;
    private      Font          font        = new Font("GenericSansSerif", 10);

    public AssemblerIconElement(MachinePermutation assembler, int displayedNumber, ProductionGraphViewer parent)
    {
        super(parent);
        this.displayedMachine = assembler;
        this.displayedNumber = displayedNumber;
//        centreFormat.Alignment = centreFormat.LineAlignment = StringAlignment.Center;
    }

    private void updateSize()
    {
        width = (int) stringWidth + iconSize;
        height = iconSize;
    }

    @Override
    public void paint(GraphicsContext graphics)
    {
        Point2D iconPoint = new Point2D((width + iconSize + stringWidth) / 2 - iconSize, (height - iconSize) / 2);

        graphics.drawImage(displayedMachine.assembler.icon, iconPoint.getX(), iconPoint.getY(), iconSize, iconSize);
        graphics.fillText(String.valueOf(displayedNumber), (width - iconSize - stringWidth) / 2 + stringWidth / 2,
                          height / 2);

        if (!displayedMachine.modules.isEmpty())
        {
            int moduleCount = displayedMachine.modules.size();
            int numModuleRows = (int) Math.ceil(moduleCount / 2d);
            int moduleSize = Math.min(iconSize / numModuleRows, iconSize / (2 - moduleCount % 2)) - 2;

            int i = 0;
            int x;

            if (moduleCount == 1)
            {
                x = (int) iconPoint.getX() + (iconSize - moduleSize) / 2;
            }
            else
            {
                x = (int) iconPoint.getX() + (iconSize - moduleSize - moduleSize) / 2;
            }
            int y = (int) iconPoint.getY() + (iconSize - (moduleSize * numModuleRows)) / 2;
            for (int r = 0; r < numModuleRows; r++)
            {
                graphics.drawImage(displayedMachine.modules.get(i).getIcon(), x, y + (r * moduleSize), moduleSize,
                                   moduleSize);
                i++;
                if (i < displayedMachine.modules.size() && displayedMachine.modules.get(i) != null)
                {
                    graphics.drawImage(displayedMachine.modules.get(i).getIcon(), x + moduleSize, y + (r * moduleSize),
                                       moduleSize, moduleSize);
                    i++;
                }
            }
        }
    }

    public void setDisplayedNumber(int number)
    {
        displayedNumber = number;
        final Text text = new Text(String.valueOf(number));
        new Scene(new Group(text));
        text.applyCss();
        stringWidth = (float) text.getLayoutBounds().getWidth();
    }

    public int getDisplayedNumber()
    {
        return displayedNumber;
    }
}
