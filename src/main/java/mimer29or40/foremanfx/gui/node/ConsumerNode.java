package mimer29or40.foremanfx.gui.node;

import mimer29or40.foremanfx.gui.graph.ProductionGraph;
import mimer29or40.foremanfx.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsumerNode extends ProductionNode
{
    private Item consumedItem;

    protected ConsumerNode(Item consumedItem, ProductionGraph graph)
    {
        super(graph);
        this.consumedItem = consumedItem;
    }

    public static ConsumerNode create(Item item, ProductionGraph graph)
    {
        ConsumerNode node = new ConsumerNode(item, graph);
        node.graph.nodes.add(node);
        node.graph.invalidateCaches();
        return node;
    }

    @Override
    public float getRateLimitedByInputs()
    {
        return getTotalInput(consumedItem);
    }

    @Override
    public float getRateDemandedByOutputs()
    {
        return 0f;
    }

    @Override
    public float getUnsatisfiedDemand(Item item)
    {
        return (float) Math.round(desiredRate - getTotalInput(item));
    }

    @Override
    public float getExcessOutput(Item item)
    {
        return 0;
    }

    @Override
    public float getTotalDemand(Item item)
    {
        if (consumedItem != item)
        {
            return 0f;
        }
        else
        {
            return (float) Math.round(desiredRate);
        }
    }

    @Override
    public float getTotalOutput(Item item)
    {
        return 0;
    }

    @Override
    public List<Item> getInputs()
    {
        return new ArrayList<>(Arrays.asList(consumedItem));
    }

    @Override
    public List<Item> getOutputs()
    {
        return new ArrayList<>();
    }

    public Item getConsumedItem()
    {
        return consumedItem;
    }

    @Override
    public String getDisplayName()
    {
        return consumedItem.getLocalizedName();
    }

    @Override
    public String toString()
    {
        return String.format("Supply Tree Node: %s", consumedItem.getName());
    }
}
