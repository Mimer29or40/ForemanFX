package mimer29or40.foremanfx.model;

import javafx.scene.image.Image;
import mimer29or40.foremanfx.DataCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ProductionEntity
{
    protected String  name;
    public    boolean enabled;
    public    Image   icon;
    public    int     moduleSlots;
    public    float   speed;

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

    public float getRate(float timeDivisor)
    {
        if (timeDivisor == 0F)
        { timeDivisor = 1F; }
        return 1 / timeDivisor * speed;
    }

    public List<MachinePermutation> getAllPermutations()
    {
        List<MachinePermutation> permutations = new ArrayList<>();

        if (moduleSlots <= 0)
        {
            permutations.add(new MachinePermutation(this, DataCache.modules.get("none")));
            return permutations;
        }

        for (Module module : DataCache.modules.values())
        {
            if (module.enabled)
            {
                Module[] currentModules = new Module[moduleSlots];
                for (int i = 0; i < moduleSlots; i++)
                {
                    currentModules[i] = module;
                }
                permutations.add(new MachinePermutation(this, currentModules));
            }
        }
        return permutations;
    }
}
