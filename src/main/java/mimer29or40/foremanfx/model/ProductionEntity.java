package mimer29or40.foremanfx.model;

import javafx.scene.image.Image;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ProductionEntity
{
    protected String  name;
    public    String  localizedName;
    public    boolean enabled;
    public    Image   icon;
    public    int     moduleSlots;
    public    float   speed;

    public String getName()
    {
        if (!Util.isNullOrWhitespace(localizedName))
        { return localizedName; }
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
        Module[] currentModules = new Module[moduleSlots];

        if (moduleSlots <= 0)
        {
            return null;
        }

        DataCache.modules.values().stream().filter(m -> m.enabled).forEach(module ->
                                                                           {
                                                                               for (int i = 0; i < moduleSlots; i++)
                                                                               {
                                                                                   currentModules[i] = module;
                                                                               }
                                                                               permutations.add(new MachinePermutation(
                                                                                       this, Arrays.asList(
                                                                                       currentModules)));
                                                                           });
        return permutations;
    }
}
