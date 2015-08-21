package mimer29or40.foremanfx.model;

import java.util.List;

public class MachinePermutation
{
    public ProductionEntity assembler;
    public List<Module>     modules;

    public double getRate(float timeDivisor)
    {
        float speed = assembler.speed;
        for (Module module : modules)
        {
            speed += module.speedBonus * assembler.speed;
        }
        if (timeDivisor == 0F)
        { timeDivisor = 1F; }
        return Math.round(1 / timeDivisor * speed);
    }

    public MachinePermutation(ProductionEntity assembler, List<Module> modules)
    {
        this.assembler = assembler;
        this.modules = modules;
    }
}
