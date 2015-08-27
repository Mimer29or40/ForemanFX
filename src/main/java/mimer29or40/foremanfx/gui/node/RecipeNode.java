package mimer29or40.foremanfx.gui.node;

import mimer29or40.foremanfx.gui.graph.AmountType;
import mimer29or40.foremanfx.gui.graph.ProductionGraph;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.MachinePermutation;
import mimer29or40.foremanfx.model.Recipe;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeNode extends ProductionNode
{
    private Recipe baseRecipe;

    protected RecipeNode(Recipe baseRecipe, ProductionGraph graph)
    {
        super(graph);
        this.baseRecipe = baseRecipe;
    }

    public static RecipeNode create(Recipe baseRecipe, ProductionGraph graph)
    {
        RecipeNode node = new RecipeNode(baseRecipe, graph);
        node.graph.nodes.add(node);
        node.graph.invalidateCaches();
        return node;
    }

    @Override
    public float getRateLimitedByInputs()
    {
        if (baseRecipe.isMissingRecipe) return 0F;

        float total = Float.POSITIVE_INFINITY;
        for (Item item : getInputs())
        {
            total = Math.min(total, getTotalInput(item) / baseRecipe.getIngredients().get(item));
        }
        return validateRecipeRate(total);
    }

    @Override
    public float getRateDemandedByOutputs()
    {
        if (baseRecipe.isMissingRecipe) return 0F;

        float total = 0F;
        for (Item item : baseRecipe.getResults().keySet())
        {
            total = Math.max(total, getRequiredOutput(item) / baseRecipe.getResults().get(item));
        }
        return validateRecipeRate(total);
    }

    @Override
    public float getUnsatisfiedDemand(Item item)
    {
        if (baseRecipe.isMissingRecipe) return 0F;

        float itemRate = (desiredRate * baseRecipe.getIngredients().get(item)) - getTotalInput(item);
        return (float) Math.round(itemRate);
    }

    @Override
    public float getExcessOutput(Item item)
    {
        if (baseRecipe.isMissingRecipe) return 0F;

        float itemRate = (actualRate * baseRecipe.getResults().get(item)) - getUsedOutput(item);
        return (float) Math.round(itemRate);
    }

    @Override
    public float getTotalDemand(Item item)
    {
        if (baseRecipe.isMissingRecipe || !baseRecipe.getIngredients().containsKey(item)) return 0F;
        float itemRate = desiredRate * baseRecipe.getIngredients().get(item);
        return (float) Math.round(itemRate);
    }

    @Override
    public float getTotalOutput(Item item)
    {
        if (baseRecipe.isMissingRecipe || !baseRecipe.getResults().containsKey(item)) return 0F;
        float itemRate = actualRate * baseRecipe.getResults().get(item);
        return (float) Math.round(itemRate);
    }

    //If the graph is showing amounts rather than rates, round up all fractions (because it doesn't make sense to do
    // half a recipe, for example)
    private float validateRecipeRate(float amount)
    {
        if (graph.selectedAmountType == AmountType.FixedAmount)
        {
            return (float) Math.ceil(Math.round(
                    amount)); //Subtracting a very small number stops the amount from getting rounded up due to FP
            // errors. It's a bit hacky but it works for now.
        }
        else
        {
            return (float) Math.round(amount);
        }
    }

    public Map<MachinePermutation, Integer> getMinimumAssemblers()
    {
//        Map<MachinePermutation, Integer> results = new HashMap<>();
//
//        double requiredRate = getRateDemandedByOutputs();
//        if (requiredRate == Double.POSITIVE_INFINITY)
//        {
//            return results;
//        }
//        requiredRate = Math.round(requiredRate);
//
//        Assembler[] assemblers = (Assembler[]) DataCache.assemblers.values().stream()
//                                    .filter(a -> a.enabled && a.getCategories().contains(baseRecipe.category) &&
//                                                 a.maxIngredients >= baseRecipe.getIngredients().size()).toArray();
//        List<Assembler> allowedAssemblers = Arrays.asList(assemblers);
//
//        List<MachinePermutation> allowedPermutation = new ArrayList<>();
//
//        for (Assembler entity : allowedAssemblers)
//        {
//            allowedPermutation.addAll(entity.getAllPermutations());
//        }
//
//        allowedPermutation.sort((item1, item2) -> Double.compare(item1.getRate(baseRecipe.time),
// item2.getRate(baseRecipe.time)));
//
//        if (allowedPermutation.stream().findAny() != null)
//        {
//            double totalRateSoFar = 0;
//            while (totalRateSoFar < requiredRate)
//            {
//                double remainingRate = requiredRate - totalRateSoFar;
//
//                MachinePermutation defaultPermutation = allowedPermutation.stream()
//                                              .filter(p -> p.getRate(baseRecipe.time) <= remainingRate).findAny()
// .get();
//                MachinePermutation permutationToAdd = Iterables.getLast(allowedPermutation, defaultPermutation);
//
//                if (permutationToAdd != null)
//                {
//                    int numberToAdd;
//                    if (graph.oneAssemblerPerRecipe)
//                    {
//                        numberToAdd = (int) Math.ceil(remainingRate / permutationToAdd.getRate(baseRecipe.time));
//                    }
//                    else
//                    {
//                        numberToAdd = (int) Math.floor(remainingRate / permutationToAdd.getRate(baseRecipe.time));
//                    }
//                    results.put(permutationToAdd, numberToAdd);
//                }
//                else
//                {
//                    permutationToAdd = Iterables.getFirst(allowedPermutation, defaultPermutation);
//                    int amount = (int) Math.ceil(remainingRate / permutationToAdd.getRate(baseRecipe.time));
//                    if (results.containsKey(permutationToAdd))
//                    {
//                        results.put(permutationToAdd, results.get(permutationToAdd) + amount);
//                    }
//                    else
//                    {
//                        results.put(permutationToAdd, amount);
//                    }
//                }
//                totalRateSoFar = 0;
//                for (MachinePermutation val : results.keySet())
//                {
//                    totalRateSoFar += val.getRate(baseRecipe.time) * results.get(val);
//                }
//                totalRateSoFar = Math.round(totalRateSoFar);
//            }
//        }
//        return results;
        return null;
    }

    @Override
    public List<Item> getInputs()
    {
        return baseRecipe.getIngredients().keySet().stream().collect(Collectors.toList());
    }

    @Override
    public List<Item> getOutputs()
    {
        return baseRecipe.getResults().keySet().stream().collect(Collectors.toList());
    }

    public Recipe getBaseRecipe()
    {
        return baseRecipe;
    }

    @Override
    public String getDisplayName()
    {
        return baseRecipe.getLocalizedName();
    }

    @Override
    public String toString()
    {
        return String.format("Recipe Tree Node: %s", baseRecipe.getName());
    }
}