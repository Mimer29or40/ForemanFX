package mimer29or40.foremanfx.model;

import java.util.ArrayList;
import java.util.List;

public class Assembler extends ProductionEntity
{
    private List<String> categories;
    public  int          maxIngredients;
    private List<String> allowedEffects;

    public Assembler(String name)
    {
        this.name = name;
        this.enabled = true;
        categories = new ArrayList<>();
        allowedEffects = new ArrayList<>();
    }

    public String toString()
    {
        return "Assembler: " + name;
    }

    public List<String> getCategories()
    {
        return categories;
    }

    public void addCategories(String cat)
    {
        categories.add(cat);
    }

    public List<String> getAllowedEffects()
    {
        return allowedEffects;
    }

    public void addAllowedEffects(String effect)
    {
        allowedEffects.add(effect);
    }
}
