package mimer29or40.foremanfx.model;

import mimer29or40.foremanfx.util.Util;

import java.awt.image.BufferedImage;

public abstract class ProductionEntity
{
    protected String        name;
    public    String        localizedName;
    public    boolean       enabled;
    public    BufferedImage icon;
    public    int           moduleSlots;
    public    float         speed;

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

//    public List<MachinePermutation> getAllPermutations() TODO find out how this works
//    {
//        yield return new MachinePermutation(this, new List<Module>());
//
//        Module[] currentModules = new Module[ModuleSlots];
//
//        if (ModuleSlots <= 0)
//        {
//            yield break;
//        }
//
//        foreach (Module module in DataCache.Modules.Values.Where(m => m.Enabled))
//        {
//            for (int i = 0; i < ModuleSlots; i++)
//            {
//                currentModules[i] = module;
//                yield return new MachinePermutation(this, currentModules);
//            }
//        }
//    }
}
