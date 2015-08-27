package mimer29or40.foremanfx.model;

import java.util.List;

public class MachinePermutation
{
    public ProductionEntity entity;
    public List<Module>     modules;

    public double getRate(float timeDivisor)
    {
        float speed = entity.speed;
        for (Module module : modules)
        {
            speed += module.speedBonus * entity.speed;
        }
        if (timeDivisor == 0F)
        { timeDivisor = 1F; }
        return Math.round(1 / timeDivisor * speed);
    }

    public MachinePermutation(ProductionEntity entity, List<Module> modules)
    {
        this.entity = entity;
        this.modules = modules;
    }
}
