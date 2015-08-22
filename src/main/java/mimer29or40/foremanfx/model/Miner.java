package mimer29or40.foremanfx.model;

import java.util.ArrayList;
import java.util.List;

public class Miner extends ProductionEntity
{
    public List<String> resourceCategories;
    public float        miningPower;

    public Miner(String name)
    {
        this.name = name;
        resourceCategories = new ArrayList<>();
        enabled = true;
    }

    public float getRate(Resource resource)
    {
        return (miningPower - resource.hardness) * speed / resource.time;
    }
}
