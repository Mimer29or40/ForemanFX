package mimer29or40.foremanfx.gui.node;

import mimer29or40.foremanfx.gui.graph.ProductionGraph;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.MachinePermutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplyNode extends ProductionNode
{
    private Item suppliedItem;

    protected SupplyNode(Item suppliedItem, ProductionGraph graph)
    {
        super(graph);
        this.suppliedItem = suppliedItem;
    }

    public static SupplyNode create(Item suppliedItem, ProductionGraph graph)
    {
        SupplyNode node = new SupplyNode(suppliedItem, graph);
        node.graph.nodes.add(node);
        node.graph.invalidateCaches();
        return node;
    }

    @Override
    public float getRateLimitedByInputs()
    {
        return actualRate;
    }

    @Override
    public float getRateDemandedByOutputs()
    {
        float total = 0F;
        for (NodeLink link : outputLinks)
        {
            total += link.getDemand();
        }
        return total;
    }

    @Override
    public float getUnsatisfiedDemand(Item item)
    {
        return 0F;
    }

    @Override
    public float getExcessOutput(Item item)
    {
        if (rateType == RateType.Auto)
        {
            return 0f;
        }
        else
        {
            float excessSupply = actualRate;
            for (NodeLink link : (NodeLink[]) outputLinks.stream().filter(l -> l.item == item).toArray())
            {
                excessSupply -= link.throughput;
            }
            return (float) Math.round(excessSupply);
        }
    }

    @Override
    public float getTotalDemand(Item item)
    {
        return 0F;
    }

    @Override
    public float getTotalOutput(Item item)
    {
        if (suppliedItem != item)
        {
            return 0f;
        }
        else
        {
            return (float) Math.round(actualRate);
        }
    }

    public Map<MachinePermutation, Integer> getMinimumMiners()
    {
//        Map<MachinePermutation, Integer> results = new HashMap<>();
//
//        Resource defaultResource = DataCache.resources.values().stream()
//                                              .filter(r -> r.result.equals(suppliedItem.getName())).findAny().get();
//        Resource resource = Iterables.getFirst(DataCache.resources.values(), defaultResource);
//        if (resource == null)
//        {
//            return results;
//        }
//
//        Miner[] miners = (Miner[]) DataCache.miners.values().stream()
//                                    .filter(a -> a.enabled &&
//                                                 a.resourceCategories.contains(resource.category)).toArray();
//        List<Miner> allowedMiners = Arrays.asList(miners);
//
//        List<MachinePermutation> allowedPermutations = new ArrayList<>();
//
//        for (Miner miner : allowedMiners)
//        {
//            allowedPermutations.addAll(miner.getAllPermutations());
//        }
//
//        allowedPermutations.sort((item1, item2) -> Double.compare(item1.getRate(resource.time),
// item2.getRate(resource.time)));
//
//        if (allowedMiners.stream().findAny() != null)
//        {
//            float requiredRate = getRequiredOutput(suppliedItem);
//            MachinePermutation defaultPermutation = allowedPermutations.stream()
//                                               .filter(a -> a.getRate(resource.time) < requiredRate).findAny().get();
//            MachinePermutation permutationToAdd = Iterables.getLast(allowedPermutations, defaultPermutation);
//            if (permutationToAdd != null)
//            {
//                int numberToAdd = (int) Math.ceil(requiredRate / permutationToAdd.getRate(resource.time));
//                results.put(permutationToAdd, numberToAdd);
//            }
//        }
//        return results;
        return null;
    }

    @Override
    public List<Item> getInputs()
    {
        return new ArrayList<>();
    }

    @Override
    public List<Item> getOutputs()
    {
        return new ArrayList<>();
    }

    public Item getSuppliedItem()
    {
        return suppliedItem;
    }

    @Override
    public String getDisplayName()
    {
        return suppliedItem.getLocalizedName();
    }

    @Override
    public String toString()
    {
        return String.format("Supply Tree Node: %s", suppliedItem.getName());
    }
}
