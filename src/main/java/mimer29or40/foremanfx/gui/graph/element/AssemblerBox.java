package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.MachinePermutation;

import java.util.HashMap;
import java.util.Map;

public class AssemblerBox extends GraphElement
{
    public  Map<MachinePermutation, Integer> assemblerList;
    private Point2D                          size;

    public AssemblerBox(ProductionGraphViewer parent)
    {
        super(parent);
        assemblerList = new HashMap<>();
    }

    public void update()
    {
        for (Object element : subElements.toArray())
        {
            if (element instanceof AssemblerIconElement)
            {
                if (!assemblerList.keySet().contains(((AssemblerIconElement) element).displayedMachine))
                {
                    subElements.remove(element);
                }
            }
        }

        for (MachinePermutation kep : assemblerList.keySet())
        {
            if (!subElements.stream().filter(e -> e instanceof AssemblerIconElement)
                            .anyMatch(aie -> ((AssemblerIconElement) aie).displayedMachine == kep))
            {
                subElements.add(new AssemblerIconElement(kep, assemblerList.get(kep), parent));
            }
        }

        int y = (int) (height / Math.ceil(assemblerList.size() / 2d));
        int widthOver2 = this.width / 2;

        int i = 0;
        for (Object element : subElements.stream().filter(e -> e instanceof AssemblerIconElement).toArray())
        {
            ((AssemblerIconElement) element).setDisplayedNumber(assemblerList.get(
                    ((AssemblerIconElement) element).displayedMachine));

            if (i % 2 == 0)
            {
                ((AssemblerIconElement) element).x = widthOver2 - ((AssemblerIconElement) element).width;
            }
            else
            {
                ((AssemblerIconElement) element).x = widthOver2;
            }
            ((AssemblerIconElement) element).y = (int) Math.floor(i / 2d) * y;

            if (assemblerList.size() == 1)
            {
                ((AssemblerIconElement) element).x = (width - ((AssemblerIconElement) element).width) / 2;
            }
            else if (i == assemblerList.size() - 1 && assemblerList.size() % 2 != 0)
            {
                ((AssemblerIconElement) element).x = widthOver2 - (((AssemblerIconElement) element).width / 2);
            }

            i++;
        }
    }

    @Override
    public void paint(GraphicsContext graphics)
    {
        super.paint(graphics);
    }
}
