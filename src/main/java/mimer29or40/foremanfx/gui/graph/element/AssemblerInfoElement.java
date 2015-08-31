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

import java.util.HashMap;
import java.util.Map;

public class AssemblerInfoElement extends GraphElement
{
    public Map<MachinePermutation, Integer> assemblerList;

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
        this.getChildren().addAll(text, assemblerIcon);

        moduleIcons = new ImageView[4];
        for (int i = 0; i < moduleIcons.length; i++)
        {
            moduleIcons[i] = new ImageView(DataCache.unknownIcon);
            this.getChildren().add(moduleIcons[i]);
            moduleIcons[i].setScaleX(0.5);
            moduleIcons[i].setScaleY(0.5);
        }

        assemblerList = new HashMap<>();

//        setOnMouseReleased(this::openContextMenu);
        setEventHandler(MouseEvent.MOUSE_RELEASED, this::openContextMenu);
    }

    @Override
    public void setupElements()
    {
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
            int moduleCount = displayedMachine.modules.size();

            int x = (int) iconPoint.getX();
            int y = (int) iconPoint.getY();

            if (moduleCount == 1)
            {
                x += 8;
                y += 8;
            }

            for (int i = 0; i < moduleCount; i++)
            {
                moduleIcons[i].setTranslateX(x + 16 * (i % 2) - 8);
                moduleIcons[i].setTranslateY(y + 16 * Math.floor(i / 2) - 8);
            }
        }
        super.setupElements();
    }

    @Override
    public void update()
    {
        text.setText(String.valueOf(displayedNumber));
        assemblerIcon.setImage(displayedMachine.entity.icon);

        if (!displayedMachine.modules.isEmpty())
        {
            int moduleX = (int) assemblerIcon.getBoundsInParent().getMinX();
            int moduleY = (int) assemblerIcon.getBoundsInParent().getMinY();

            if (displayedMachine.modules.size() == 1)
            {
                moduleX += 8;
                moduleY += 8;
            }

            for (int i = 0; i < moduleIcons.length; i++)
            {
                moduleIcons[i].setImage(displayedMachine.modules.get(0).getIcon());
                moduleIcons[i].setTranslateX(moduleX + 16 * (i % 2) - 8);
                moduleIcons[i].setTranslateY(moduleY + 16 * Math.floor(i / 2) - 8);
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
        this.assemblerIcon.setImage(this.displayedMachine.entity.icon);
    }

    public void openContextMenu(MouseEvent event)
    {
        if (event.isStillSincePress())
        {
            final ContextMenu contextMenu = new ContextMenu();
            contextMenu.setAutoHide(true);

            for (MachinePermutation permutation : assemblerList.keySet())
            {
                MenuItem item = new MenuItem(permutation.entity.getLocalizedName() + " with " + permutation.modules.get(0).getLocalizedName());
                item.setOnAction(event1 -> changeAssembler(permutation, assemblerList.get(permutation)));
                contextMenu.getItems().add(item);
            }

            contextMenu.show(parent.getScene().getWindow(), event.getScreenX(), event.getScreenY());
        }
    }

    public void changeAssembler(MachinePermutation permutation, int number)
    {
        this.displayedMachine = permutation;
        this.displayedNumber = number;
        update();
    }
}
