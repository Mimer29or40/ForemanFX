package mimer29or40.foremanfx.gui.graph;

import com.google.common.collect.Iterables;
import javafx.scene.canvas.Canvas;
import mimer29or40.foremanfx.gui.node.*;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.Recipe;
import mimer29or40.foremanfx.util.ListUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ProductionGraph extends Canvas
{
    public List<ProductionNode> nodes                 = new ArrayList<>();
    public int[][]              pathMatrixCache       = null;
    public int[][]              adjacencyMatrixCache  = null;
    public HashSet<Recipe>      cyclicRecipes         = new HashSet<>();
    public AmountType           selectedAmountType    = AmountType.FixedAmount;
    public RateUnit             selectedUnit          = RateUnit.perSecond;
    public boolean              oneAssemblerPerRecipe = false;

    public List<NodeLink> getAllNodeLinks()
    {
        List<NodeLink> links = new ArrayList<>();
        for (ProductionNode node : nodes)
        {
            links.addAll(node.getInputLinks().stream().collect(Collectors.toList()));
        }
        return links;
    }

    public ProductionGraph()
    {
        super();
    }

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

    public List<ProductionNode> getSuppliers(Item item)
    {
        List<ProductionNode> links = new ArrayList<>();
        for (ProductionNode node : nodes)
        {
            if (node.getOutputs().contains(item))
            { links.add(node); }
        }
        return links;
    }

    public List<ProductionNode> getConsumers(Item item)
    {
        List<ProductionNode> links = new ArrayList<>();
        for (ProductionNode node : nodes)
        {
            if (node.getInputs().contains(item))
            { links.add(node); }
        }
        return links;
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
                        if (createAppropriateLink(node, item))
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
                    ConsumerNode newNode = ConsumerNode.create(item, null);
                    newNode.rateType = RateType.Auto;
                    NodeLink.create(node, newNode, item);
                }
            }
        }
    }

    public void updateNodeValues()
    {
        nodes.stream().filter(n -> n.rateType == RateType.Manual).forEach(node -> node.desiredRate = node.actualRate);
        nodes.stream().filter(n -> n.rateType == RateType.Auto).forEach(
                node -> node.desiredRate = node.actualRate = 0F);

        for (NodeLink link : getAllNodeLinks())
        {
            link.throughput = 0;
        }

        nodes.stream().filter(n -> n.rateType == RateType.Manual).forEach(startingNode ->
                                                                          {
                                                                              Stack<NodeLink> routeHome = new Stack<>();
                                                                              HashSet<ProductionNode> nodesInStack
                                                                                      = new HashSet<>(Arrays.asList(
                                                                                      startingNode));
                                                                              int[] linkIndices = new int[nodes.size()];

                                                                              ProductionNode currentNode = startingNode;

                                                                              do
                                                                              {
                                                                                  if (linkIndices[routeHome
                                                                                          .size()] < currentNode
                                                                                              .getInputLinks().size())
                                                                                  {
                                                                                      NodeLink nextLink = currentNode
                                                                                              .getInputLinks().get(
                                                                                                      linkIndices[routeHome
                                                                                                              .size()]);
                                                                                      linkIndices[routeHome.size()]++;
                                                                                      if (nextLink.supplier.rateType
                                                                                          == RateType.Auto &&
                                                                                          !nodesInStack
                                                                                              .contains(
                                                                                                      nextLink.supplier))
                                                                                      {
                                                                                          routeHome.push(nextLink);
                                                                                          nextLink.throughput
                                                                                                  += currentNode
                                                                                                  .getUnsatisfiedDemand(
                                                                                                          nextLink.item);

                                                                                          currentNode
                                                                                                  = nextLink.supplier;
                                                                                          currentNode.actualRate
                                                                                                  = currentNode
                                                                                                  .getRateDemandedByOutputs();
                                                                                          currentNode.desiredRate
                                                                                                  = currentNode
                                                                                                  .actualRate;
                                                                                          nodesInStack.add(currentNode);
                                                                                      }
                                                                                      else
                                                                                      {
                                                                                          nextLink.throughput += Math
                                                                                                  .min(nextLink.supplier
                                                                                                               .getUnusedOutput(
                                                                                                                       nextLink.item),
                                                                                                       currentNode
                                                                                                               .getUnsatisfiedDemand(
                                                                                                                       nextLink.item));
                                                                                      }
                                                                                  }
                                                                                  else
                                                                                  {
                                                                                      if (!routeHome.isEmpty())
                                                                                      {
                                                                                          linkIndices[routeHome.size()]
                                                                                                  = 0;
                                                                                          nodesInStack.remove(
                                                                                                  currentNode);
                                                                                          currentNode = routeHome
                                                                                                  .pop().consumer;
                                                                                      }
                                                                                  }
                                                                              }
                                                                              while (!(currentNode == startingNode &&
                                                                                       linkIndices[0] >= startingNode
                                                                                      .getInputLinks().size()));
                                                                          });

        //Go up the list and make each node go as fast as it can, given the amounts being input to it
        List<ProductionNode> sortedNodes = getTopologicalSort();
        for (ProductionNode node : sortedNodes)
        {
            if (node.rateType == RateType.Auto)
            {
                node.actualRate = node.getRateLimitedByInputs();
            }
            for (Item item : node.getOutputs())
            {
                float remainingOutput = node.getTotalOutput(item);
                for (NodeLink link : (NodeLink[]) node.getOutputLinks().stream().filter(l -> l.item == item).toArray())
                {
                    link.throughput = Math.min(link.throughput, remainingOutput);
                    remainingOutput -= link.throughput;
                }
            }
        }

        //Find any remaining auto nodes with rate = 0 and make them use as many items as they can
        //These nodes are probably nodes at the top of the flowchart with nothing above them demanding items
        for (ProductionNode node : (ProductionNode[]) sortedNodes.stream().filter(
                n -> n.rateType == RateType.Auto && n.desiredRate == 0).toArray())
        {
            for (NodeLink link : node.getInputLinks())
            {
                link.throughput = link.supplier.getTotalOutput(link.item) - link.supplier.getUsedOutput(link.item);
            }
            node.actualRate = node.getRateLimitedByInputs();
            if (node.actualRate > 0)
            {
                node.desiredRate = node.actualRate;
            }
            else
            {
                //This node can't run with the available items, so free them up for another node to potentially use
                // (and so the node doesn't display the wrong throughput for each item)
                node.getInputLinks().stream().forEach(l -> l.throughput = 0);
            }
        }
    }

    //Returns true if a new link was created
    public boolean createAppropriateLink(ProductionNode node, Item item)
    {
        if (node instanceof RecipeNode && cyclicRecipes.contains(((RecipeNode) node).getBaseRecipe()))
        {
            return false;
        }

        if (nodes.stream().anyMatch(n -> n.getOutputs().contains(item)))    //Add link from existing node
        {
            ProductionNode existingNode = nodes.stream().filter(n -> n.getOutputs().contains(item)).findAny().get();
            if (existingNode == node)
            {
                return false;
            }
            NodeLink.create(existingNode, node, item);
        }
        else if (item.getRecipes().stream().anyMatch(r -> !cyclicRecipes.contains(
                r)))    //Create new recipe node and link from it
        {
            RecipeNode newNode = RecipeNode.create(item.getRecipes()
                                                       .stream().filter(r -> !cyclicRecipes.contains(r)).findFirst()
                                                       .get(), this);
            NodeLink.create(newNode, node, item);
        }
        else //Create new supply node and link from it
        {
            SupplyNode newNode = SupplyNode.create(item, this);
            NodeLink.create(newNode, node, item);
        }

        replaceCycles();
        return true;
    }

    //Replace recipe cycles with a simple supplier node so that they don't cause infinite loops. This is a workaround.
    public void replaceCycles()
    {
        getStronglyConnectedComponents().stream().filter(scc -> scc.size() > 1).forEach(strongComponent ->
                                                                                        {
                                                                                            for (ProductionNode node : strongComponent)
                                                                                            {
                                                                                                List<NodeLink> union = ListUtil.union(
                                                                                                        node.getInputLinks(), node.getOutputLinks());

                                                                                                union.forEach(NodeLink::destroy);
                                                                                                cyclicRecipes.add(
                                                                                                        ((RecipeNode) node).getBaseRecipe());
                                                                                                nodes.remove(node);
                                                                                            }
                                                                                        });
        invalidateCaches();
    }

    public List<ProductionNode> getInputlessNodes()
    {
        List<ProductionNode> nodes = new ArrayList<>();
        for (ProductionNode node : nodes)
        {
            if (node.getInputLinks().isEmpty())
            {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private class TarzanNode
    {
        public ProductionNode sourceNode;
        public int                 index   = -1;
        public int                 lowLink = -1;
        public HashSet<TarzanNode> links = new HashSet<>(); //Links to other nodes

        public TarzanNode(ProductionNode sourceNode)
        {
            this.sourceNode = sourceNode;
        }
    }

    public List<List<ProductionNode>> getStronglyConnectedComponents()
    {
        List<List<ProductionNode>> strongList = new ArrayList<>();
        Stack<TarzanNode> S = new Stack<TarzanNode>();
        Map<ProductionNode, TarzanNode> tNodes = new HashMap<ProductionNode, TarzanNode>();
        int indexCounter = 0;

        for (ProductionNode n : nodes)
        {
            tNodes.put(n, new TarzanNode(n));
        }

        for (ProductionNode n : nodes)
        {
            for (ProductionNode m : nodes)
            {
                if (m.getInputLinks().stream().filter(l -> l.supplier == n).findAny().isPresent())
                {
                    tNodes.get(n).links.add(tNodes.get(m));
                }
            }
        }

        for (TarzanNode v : tNodes.values())
        {
            if (v.index == -1)
            {
                StrongConnect(strongList, S, indexCounter, v);
            }
        }

        return strongList;
    }

    private void StrongConnect(List<List<ProductionNode>> strongList, Stack<TarzanNode> S, int indexCounter,
                               TarzanNode v)
    {
        v.index = indexCounter;
        v.lowLink = indexCounter++;
        S.push(v);

        for (TarzanNode w : v.links)
        {
            if (w.index == -1)
            {
                StrongConnect(strongList, S, indexCounter, w);
                v.lowLink = Math.min(v.lowLink, w.lowLink);
            }
            else if (S.contains(w))
            {
                v.lowLink = Math.min(v.lowLink, w.lowLink);
            }
        }

        {
            TarzanNode w = null;
            if (v.lowLink == v.index)
            {
                strongList.add(new ArrayList<>());
                do
                {
                    w = S.pop();
                    Iterables.getLast(strongList).add(w.sourceNode);
                }
                while (w != v);
            }
        }
    }

    public List<ProductionNode> getTopologicalSort()
    {
        int[][] matrix = getAdjacencyMatrix();
        List<ProductionNode> L = new ArrayList<>(); //Final sorted list
        List<ProductionNode> S = new ArrayList<>(Arrays.asList((ProductionNode[]) getInputlessNodes().toArray()));

        while (!S.isEmpty())
        {
            ProductionNode node = Iterables.getFirst(S, null);
            S.remove(node);
            L.add(node);

            int n = nodes.indexOf(node);

            for (int m = 0; m < nodes.size(); m++)
            {
                if (matrix[n][m] == 1)
                {
                    matrix[n][m] = 0;
                    int edgeCount = 0;
                    for (int i = 0; i < matrix[1].length; i++)
                    {
                        edgeCount += matrix[i][m];
                    }
                    if (edgeCount == 0)
                    {
                        S.add(0, nodes.get(m));
                    }
                }
            }
        }

        for (int i = 0; i < matrix[0].length; i++)
        {
            for (int j = 0; j < matrix[1].length; j++)
            {
                // Edges mean there's a cycle somewhere and the sort can't be completed
            }
        }

        return L;
    }
}
