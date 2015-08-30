package mimer29or40.foremanfx.gui.graph.element;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.MachinePermutation;

import java.util.HashMap;
import java.util.Map;

public class AssemblerBox extends GraphElement
{
    public Map<MachinePermutation, Integer> assemblerList;
    public MachinePermutation               selectedMachine;

    public AssemblerBox(ProductionGraphViewer parent)
    {
        super(parent);
        assemblerList = new HashMap<>();

        setOnMousePressed(event -> openContextMenu(event));
    }

    @Override
    public void update()
    {

        if (assemblerList != null)
        {
            for (MachinePermutation key : assemblerList.keySet())
            {
                if (subElements.stream().filter(e -> ((AssemblerInfoElement) e).displayedMachine != key).findAny().get() != null)
                {
                    if (!subElements.isEmpty())
                    {
                        for (GraphElement obj : subElements)
                        {
                            if (obj instanceof AssemblerInfoElement)
                            {
                                AssemblerInfoElement element = (AssemblerInfoElement) obj;
                                if (element.displayedMachine != key)
                                {
                                    subElements.add(new AssemblerInfoElement(key, assemblerList.get(key), parent));
                                }
                            }
                        }
                    }
                    else
                    {
                        subElements.add(new AssemblerInfoElement(key, assemblerList.get(key), parent));
                    }
                }
            }
        }

        int y = (int) (height / Math.ceil(assemblerList.size() / 2));
        int widthOver2 = this.width / 2;

        int i = 0;
        for (GraphElement obj : subElements)
        {
            if (obj instanceof AssemblerInfoElement)
            {
                AssemblerInfoElement element = (AssemblerInfoElement) obj;

                element.setDisplayedNumber(assemblerList.get(element.displayedMachine));

                if (i % 2 == 0)
                {
                    element.setX(widthOver2 - element.width);
                }
                else
                {
                    element.setX(widthOver2);
                }
                element.setY((int) Math.floor(i / 2d) * y);

                if (assemblerList.size() == 1)
                {
                    element.setX((width - element.width) / 2);
                }
                else if (i == assemblerList.size() - 1 && assemblerList.size() % 2 != 0)
                {
                    element.setX(widthOver2 - (element.width / 2));
                }

                i++;
            }
        }
    }

    public void openContextMenu(MouseEvent event)
    {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        contextMenu.getItems().addAll(cut, copy, paste);

        contextMenu.show(parent, event.getScreenX(), event.getScreenY());
    }
}
