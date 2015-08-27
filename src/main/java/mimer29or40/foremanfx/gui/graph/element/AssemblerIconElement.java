package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.MachinePermutation;
import mimer29or40.foremanfx.model.Module;

public class AssemblerIconElement extends GraphElement
{
    private final int maxFontSize = 14;
    public  MachinePermutation displayedMachine;
    private int                displayedNumber;
    private      float stringWidth = 0F;
    public final int   iconSize    = 32;

    private Text      text;
    private ImageView assemblerIcon;
    private ImageView[] moduleIcons = new ImageView[4];

    public AssemblerIconElement(MachinePermutation assembler, int number, ProductionGraphViewer parent)
    {
        super(parent);
        displayedMachine = assembler;
        displayedNumber = number;

        width = 54;
        height = 40;

        text = new Text();
        assemblerIcon = new ImageView(assembler.entity.icon != null ? assembler.entity.icon : DataCache.unknownIcon);

        this.getChildren().addAll(text, assemblerIcon);
    }

    @Override
    public void draw()
    {

        Point2D iconPoint = new Point2D(width - 36, 4);
        Point2D textPoint = new Point2D(4, height / 2 + 5);

        setDisplayedNumber(displayedNumber);

        assemblerIcon.setTranslateX(iconPoint.getX());
        assemblerIcon.setTranslateY(iconPoint.getY());

        text.setTranslateX(textPoint.getX());
        text.setTranslateY(textPoint.getY());

        if (!displayedMachine.modules.isEmpty())
        {
            int moduleCount = (int) displayedMachine.modules.stream().filter(m -> m instanceof Module).count();

            int x = (int) iconPoint.getX();
            int y = (int) iconPoint.getY();

            if (moduleCount == 1)
            {
                x += 16;
                y += 16;
            }

            for (int i = 0; i < moduleCount; i++)
            {
                moduleIcons[i] = new ImageView(displayedMachine.modules.get(i).getIcon());
                this.getChildren().add(moduleIcons[i]);
                moduleIcons[i].setScaleX(0.5);
                moduleIcons[i].setScaleY(0.5);
                moduleIcons[i].setTranslateX(x + 16 * (i % 2) - 8);
                moduleIcons[i].setTranslateY(y + 16 * Math.floor(i / 2) - 8);
            }
        }
    }

    @Override
    public void update()
    {

    }

    public void setDisplayedNumber(int number)
    {
        displayedNumber = number;
        this.text.setText(String.valueOf(number));
        this.text.setTranslateX((width - this.text.getLayoutBounds().getWidth()) / 2);
    }
}
