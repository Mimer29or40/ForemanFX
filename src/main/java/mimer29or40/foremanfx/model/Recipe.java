package mimer29or40.foremanfx.model;

import mimer29or40.foremanfx.DataCache;

import java.awt.image.BufferedImage;
import java.util.Map;

public class Recipe
{
    private String           name;
    public  float            time;
    public  String           category;
    private Map<Item, Float> results;
    private Map<Item, Float> ingredients;
    public boolean isMissingRecipe = false;
    private BufferedImage icon;

    public Recipe(String name, float time, Map<Item, Float> ingredients, Map<Item, Float> results)
    {
        this.name = name;
        this.time = time;
        this.ingredients = ingredients;
        this.results = results;
    }

    public int getHashCode()
    {
        return name.hashCode();
    }

    public boolean equals(Object obj)
    {
        return obj instanceof Recipe && obj == this;
    }

    public String getName()
    {
        if (DataCache.localeFiles.containsKey("recipe-name") && DataCache.localeFiles.get("recipe-name").containsKey(
                name))
        {
            return DataCache.localeFiles.get("recipe-name").get(name);
        }
        else if (results.size() == 1)
        {
            return results.keySet().iterator().next().getName();
        }
        return name;
    }

    public Recipe setIcon(BufferedImage icon)
    {
        this.icon = icon;
        return this;
    }

    public BufferedImage getIcon()
    {
        if (icon != null)
        { return icon; }
        else if (results.size() == 1)
        { return results.keySet().iterator().next().getIcon(); }
        return DataCache.unknownIcon;
    }

    public Map<Item, Float> getResults()
    {
        return results;
    }

    public Map<Item, Float> getIngredients()
    {
        return ingredients;
    }
}
