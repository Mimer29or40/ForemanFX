package mimer29or40.foremanfx.gui.node;

import mimer29or40.foremanfx.model.Item;

import java.io.Serializable;

public class NodeLink implements Serializable
{
    public ProductionNode supplier;
    public ProductionNode consumer;
    public Item           item;
    public float          throughput;

    private NodeLink(ProductionNode supplier, ProductionNode consumer, Item item)
    {
        this.supplier = supplier;
        this.consumer = consumer;
        this.item = item;
    }

    public static NodeLink create(ProductionNode supplier, ProductionNode consumer, Item item)
    {
        for (NodeLink link : supplier.outputLinks)
        {
            if (link.item == item && link.consumer == consumer)
            { return null; }
        }
        NodeLink link = new NodeLink(supplier, consumer, item);
        supplier.outputLinks.add(link);
        consumer.inputLinks.add(link);
        supplier.graph.invalidateCaches();
        return link;
    }

    public void destroy()
    {
        supplier.outputLinks.remove(this);
        consumer.inputLinks.remove(this);
    }

    public float getDemand()
    {
        return consumer.getTotalDemand(item);
    }
}
