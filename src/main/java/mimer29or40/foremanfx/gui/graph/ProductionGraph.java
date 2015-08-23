package mimer29or40.foremanfx.gui.graph;

import mimer29or40.foremanfx.gui.node.NodeLink;
import mimer29or40.foremanfx.gui.node.ProductionNode;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.Recipe;

import java.util.*;
import java.util.stream.Collectors;

public class ProductionGraph
{
    public List<ProductionNode> nodes                 = new ArrayList<>();
    public int[][]              pathMatrixCache       = null;
    public int[][]              adjacencyMatrixCache  = null;
    public HashSet<Recipe>      cyclicRecipes         = new HashSet<>();
    public AmountType           selectedAmountType    = AmountType.FixedAmount;
    public RateUnit             selectedUnit          = RateUnit.perSecond;
    public boolean              oneAssemblerPerRecipe = false;

    public Enumeration<NodeLink> getAllNodeLinks()
    {
        Vector<NodeLink> links = new Vector<>();
        for (ProductionNode node : nodes)
        {
            links.addAll(node.getInputLinks().stream().collect(Collectors.toList()));
        }
        return links.elements();
    }

    public ProductionGraph()
    {}

    public void invalidateCaches()
    {
        pathMatrixCache = null;
        adjacencyMatrixCache = null;
    }

    public int[][] getAdjacencyMatrix()
    {
        if (adjacencyMatrixCache == null)
        {
            int[][] matrix = new int[nodes.size()][nodes.size()];
            for (int i = 0; i < nodes.size(); i++)
            {
                for (int j = 0; j < nodes.size(); j++)
                {
                    for (NodeLink link : nodes.get(j).getInputLinks())
                    {
                        if (link.supplier == nodes.get(i))
                        {
                            matrix[i][j] = 1;
                        }
                    }
                }
            }
            adjacencyMatrixCache = matrix;
        }
        return adjacencyMatrixCache.clone();
    }

    public Enumeration<ProductionNode> getSuppliers(Item item)
    {
        Vector<ProductionNode> links = new Vector<>();
        for (ProductionNode node : nodes)
        {
            if (node.getOutputs().contains(item))
            { links.add(node); }
        }
        return links.elements();
    }

    public Enumeration<ProductionNode> getConsumers(Item item)
    {
        Vector<ProductionNode> links = new Vector<>();
        for (ProductionNode node : nodes)
        {
            if (node.getInputs().contains(item))
            { links.add(node); }
        }
        return links.elements();
    }

    public void linkUpAllInputs()
    {
        boolean graphChanged;
        do
        {
            graphChanged = false;
            for (ProductionNode node : nodes)
            {
                for (Item item : node.getInputs())
                {
                    if (node.getInputLinks().stream().anyMatch(link -> link.item == item))
                    {
                        if (createAppropiateLink(node, item))
                        {
                            graphChanged = true;
                        }
                    }
                }
            }
        }
        while (graphChanged);
    }

    public void linkUpAllOutputs()
    {
        for (ProductionNode node : nodes)
        {
            for (Item item : node.getOutputs())
            {
                if (node.getExcessOutput(item) > 0 || !node.getOutputLinks().stream().anyMatch(
                        link -> link.item == item))
                {
                    NodeLink.create(node, nodes.stream().filter(n -> n.getInputs().contains(item)).findFirst().get(),
                                    item);
                }
                else
                {
                    NodeLink newNode = ConsumerNode
                }
            }
        }
    }
}
