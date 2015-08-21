package mimer29or40.foremanfx;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Item
{
    public static List<String> localeCategories = Arrays.asList("item-name", "fluid-name", "entity-name",
                                                                "equipment-name");

    private String          name;
    private HashSet<Recipe> recipes;
    private BufferedImage   icon;
    public boolean isMissingIcon = false;

    private Item()
    {
        name = "";
    }

    public Item(String name, BufferedImage icon)
    {
        this.name = name;
        this.icon = icon;
        this.recipes = new HashSet<>();
    }

    public Item(String name)
    {
        this(name, null);
    }

    public int getHashCode()
    {
        return this.name.hashCode();
    }

    public boolean equals(Object obj)
    {
        return obj instanceof Recipe && obj == this;
    }

    public String getName()
    {
        for (String category : localeCategories)
        {
            if (DataCache.localeFiles.get(category).containsKey(name))
            {
                return DataCache.localeFiles.get(category).get(name);
            }
        }
        return name;
    }

    public HashSet<Recipe> getRecipes()
    {
        return recipes;
    }

    public BufferedImage getIcon()
    {
        return icon;
    }

    public void addRecipe(Recipe recipe)
    {
        recipes.add(recipe);
    }
}
