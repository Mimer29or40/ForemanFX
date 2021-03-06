package mimer29or40.foremanfx.model;

import javafx.scene.image.Image;
import mimer29or40.foremanfx.DataCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item
{
    private String       name;
    private List<Recipe> recipes;
    private Image        icon;
    public boolean isMissingItem = false;

    public Item(String name, Image icon)
    {
        this.name = name;
        this.icon = icon;
        this.recipes = new ArrayList<>();
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

    public String getLocalizedName()
    {
        List<String> localeCategories = Arrays.asList("item-name", "fluid-name", "entity-name", "equipment-name");
        for (String category : localeCategories)
        {
            if (DataCache.localeFiles.containsKey(category) && DataCache.localeFiles.get(category).containsKey(name))
            {
                return DataCache.localeFiles.get(category).get(name);
            }
        }
        return name;
    }

    public String getName()
    {
        return name;
    }

    public List<Recipe> getRecipes()
    {
        return recipes;
    }

    public Image getIcon()
    {
        return icon != null ? icon : DataCache.unknownIcon;
    }

    public void addRecipe(Recipe recipe)
    {
        recipes.add(recipe);
    }
}
