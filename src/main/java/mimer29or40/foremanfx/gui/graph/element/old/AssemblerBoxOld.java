package mimer29or40.foremanfx.gui.graph.element.old;

import com.sun.javafx.geom.Shape;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mimer29or40.foremanfx.gui.graph.ProductionGraphPaneOld;
import mimer29or40.foremanfx.model.MachinePermutation;

import java.util.HashMap;
import java.util.Map;

public class AssemblerBoxOld extends GraphElementOld
{
    public  Map<MachinePermutation, Integer> assemblerList;
    private Point2D                          size;

    public AssemblerBoxOld(ProductionGraphPaneOld parent)
    {
        super(parent);
        assemblerList = new HashMap<>();
    }

    public void update()
    {
        for (Object element : subElements.toArray())
        {
            if (element instanceof AssemblerIconElementOld)
            {
                if (!assemblerList.keySet().contains(((AssemblerIconElementOld) element).displayedMachine))
                {
                    subElements.remove(element);
                }
            }
        }

        for (MachinePermutation kep : assemblerList.keySet())
        {
            if (!subElements.stream().filter(e -> e instanceof AssemblerIconElementOld)
                            .anyMatch(aie -> ((AssemblerIconElementOld) aie).displayedMachine == kep))
            {
                subElements.add(new AssemblerIconElementOld(kep, assemblerList.get(kep), parent));
            }
        }

        int y = (int) (height / Math.ceil(assemblerList.size() / 2d));
        int widthOver2 = this.width / 2;

        int i = 0;
        for (Object element : subElements.stream().filter(e -> e instanceof AssemblerIconElementOld).toArray())
        {
            ((AssemblerIconElementOld) element).setDisplayedNumber(assemblerList.get(
                    ((AssemblerIconElementOld) element).displayedMachine));

            if (i % 2 == 0)
            {
                ((AssemblerIconElementOld) element).x = widthOver2 - ((AssemblerIconElementOld) element).width;
            }
            else
            {
                ((AssemblerIconElementOld) element).x = widthOver2;
            }
            ((AssemblerIconElementOld) element).y = (int) Math.floor(i / 2d) * y;

            if (assemblerList.size() == 1)
            {
                ((AssemblerIconElementOld) element).x = (width - ((AssemblerIconElementOld) element).width) / 2;
            }
            else if (i == assemblerList.size() - 1 && assemblerList.size() % 2 != 0)
            {
                ((AssemblerIconElementOld) element).x = widthOver2 - (((AssemblerIconElementOld) element).width / 2);
            }

            i++;
        }
    }

    @Override
    public void paint(GraphicsContext graphics)
    {
        super.paint(graphics);
    }

    @Override
    public Shape impl_configShape()
    {
        return null;
    }
}
