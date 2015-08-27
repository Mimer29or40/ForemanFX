package mimer29or40.foremanfx.gui.node;

import mimer29or40.foremanfx.gui.graph.ProductionGraph;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.util.Util;

import java.io.Serializable;
import java.util.*;

public abstract class ProductionNode implements Serializable
{
    public static final int RoundingDP = 4;
    public    ProductionGraph graph;
    protected String          displayName;
    protected List<Item>     inputs      = new ArrayList<>();
    protected List<Item>     outputs     = new ArrayList<>();
    protected List<NodeLink> inputLinks  = new ArrayList<>();
    protected List<NodeLink> outputLinks = new ArrayList<>();
    public    RateType       rateType    = RateType.Auto;
    public    float          actualRate  = 0F;
    public    float          desiredRate = 0F;

    public abstract float getExcessOutput(Item item);

    public abstract float getUnsatisfiedDemand(Item item);

    public abstract float getTotalOutput(Item item);

    public abstract float getTotalDemand(Item item);

    public abstract float getRateLimitedByInputs();

    public abstract float getRateDemandedByOutputs();

    protected ProductionNode(ProductionGraph graph)
    {
        this.graph = graph;
    }

    public boolean takesFrom(ProductionNode node)
    {
        for (NodeLink link : node.outputLinks)
        {
            if (link.consumer == this)
            { return true; }
        }
        return false;
    }

    public boolean givesTo(ProductionNode node)
    {
        for (NodeLink link : node.inputLinks)
        {
            if (link.supplier == this)
            { return true; }
        }
        return false;
    }

    public float getTotalInput(Item item)
    {
        float total = 0F;
        for (NodeLink link : inputLinks)
        {
            if (link.item == item)
            { total += link.throughput; }
        }
        return (float) Math.round(total);
    }

    public float getUsedOutput(Item item)
    {
        float total = 0F;
        for (NodeLink link : outputLinks)
        {
            if (link.item == item)
            { total += link.throughput; }
        }
        return (float) Math.round(total);
    }

    public float getUnusedOutput(Item item)
    {
        return getTotalOutput(item) - getUsedOutput(item);
    }

    public float getRequiredOutput(Item item)
    {
        float amount = 0;
        for (NodeLink link : outputLinks)
        {
            if (link.item == item)
            {
                amount += link.getDemand();
            }
        }
        return (float) Math.round(amount);
    }

    public boolean canUltimatelyTakeFrom(ProductionNode node)
    {
        Queue<ProductionNode> queue = new LinkedList();
        HashSet<ProductionNode> set = new HashSet<>();

        set.add(this);
        queue.add(this);

        while (queue.peek() != null)
        {
            ProductionNode node1 = queue.poll();
            if (node == node1)
            { return true; }
            for (NodeLink link : node1.inputLinks)
            {
                ProductionNode node2 = link.supplier;
                if (!set.contains(node2))
                {
                    set.add(node2);
                    queue.add(node2);
                }
            }
        }
        return false;
    }

    public void destroy()
    {
        for (NodeLink link : Util.union(inputLinks, outputLinks))
        {
            link.destroy();
        }
        graph.nodes.remove(this);
        graph.invalidateCaches();
    }

    public List<Item> getInputs()
    {
        return inputs;
    }

    public void setInput(Item item)
    {
        inputs.add(item);
    }

    public List<Item> getOutputs()
    {
        return outputs;
    }

    public void setOutputs(Item item)
    {
        outputs.add(item);
    }

    public List<NodeLink> getInputLinks()
    {
        return inputLinks;
    }

    public void setInputLinks(NodeLink nodeLink)
    {
        inputLinks.add(nodeLink);
    }

    public List<NodeLink> getOutputLinks()
    {
        return outputLinks;
    }

    public void setOutputLinks(NodeLink nodeLink)
    {
        outputLinks.add(nodeLink);
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
