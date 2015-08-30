package mimer29or40.foremanfx.gui.graph.element;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.MachinePermutation;
import mimer29or40.foremanfx.model.Module;

import java.util.HashMap;
import java.util.Map;

public class AssemblerInfoElement extends GraphElement
{
    public Map<MachinePermutation, Integer> assemblerList;
    public MachinePermutation               selectedMachine;

    public MachinePermutation displayedMachine;
    public int                displayedNumber;

    private Text        text;
    private ImageView   assemblerIcon;
    private ImageView[] moduleIcons;

    public AssemblerInfoElement(ProductionGraphViewer parent)
    {
        this(null, 0, parent);
    }

    public AssemblerInfoElement(MachinePermutation assembler, int number, ProductionGraphViewer parent)
    {
        super(parent);
        displayedMachine = assembler;
        displayedNumber = number;

        width = 54;
        height = 40;

        text = new Text();
        assemblerIcon = new ImageView();
        moduleIcons = new ImageView[4];

        this.getChildren().addAll(text, assemblerIcon);

        assemblerList = new HashMap<>();

        setOnMousePressed(event -> openContextMenu(event));
    }

    @Override
    public void setupElements()
    {
        super.setupElements();

        Point2D iconPoint = new Point2D(width - 36, 4);
        Point2D textPoint = new Point2D(4, height / 2 + 5);

        assemblerIcon.setImage(displayedMachine.entity.icon != null ? displayedMachine.entity.icon : DataCache.unknownIcon);
        assemblerIcon.setTranslateX(iconPoint.getX());
        assemblerIcon.setTranslateY(iconPoint.getY());

        setDisplayedNumber(displayedNumber);
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
//        if (assemblerList != null)
//        {
//            for (MachinePermutation key : assemblerList.keySet())
//            {
////                if (subElements.stream().filter(e -> ((AssemblerInfoElement) e).displayedMachine != key).findAny().get() != null)
//                    if (!subElements.isEmpty())
//                    {
//                        for (GraphElement obj : subElements)
//                        {
//                            if (obj instanceof AssemblerInfoElement)
//                            {
//                                AssemblerInfoElement element = (AssemblerInfoElement) obj;
//                                if (element.displayedMachine != key)
//                                {
//                                    subElements.add(new AssemblerInfoElement(key, assemblerList.get(key), parent));
//                                }
//                            }
//                        }
//                    }
//                    else
//                    {
//                        subElements.add(new AssemblerInfoElement(key, assemblerList.get(key), parent));
//                    }
//            }
//        }

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

    public void setDisplayedNumber(int number)
    {
        displayedNumber = number;
        this.text.setText(String.valueOf(number));
        this.text.setTranslateX((width - this.text.getLayoutBounds().getWidth()) / 2);
    }

    public void setDisplayedMachine(MachinePermutation permutation)
    {
        this.displayedMachine = permutation;
    }

    public void openContextMenu(MouseEvent event)
    {
        event.consume();

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        contextMenu.getItems().addAll(cut, copy, paste);

        contextMenu.show(parent, event.getScreenX(), event.getScreenY());
    }
}
