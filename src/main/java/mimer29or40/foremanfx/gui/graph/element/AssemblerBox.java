package mimer29or40.foremanfx.gui.graph.element;

import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.model.MachinePermutation;

import java.util.HashMap;

public class AssemblerBox extends GraphElement
{
    public HashMap<MachinePermutation, Integer> assemblerList;

    public AssemblerBox(ProductionGraphViewer parent)
    {
        super(parent);
        assemblerList = new HashMap<>();
    }

    @Override
    public void draw()
    {

    }

    @Override
    public void update()
    {

    }
}
