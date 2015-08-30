package mimer29or40.foremanfx.model;

import javafx.scene.image.Image;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.util.Util;

public class Module
{
    public  boolean enabled;
    public  float   speedBonus;
    private String  name;
    public  String  localizedName;

    public Module(String name, float speedBonus)
    {
        this.speedBonus = speedBonus;
        this.name = name;
        this.enabled = true;
    }

    public String getLocalizedName()
    {
        if (!Util.isNullOrWhitespace(localizedName))
        { return localizedName; }
        return name;
    }

    public String getName()
    {
        return name;
    }

    public Image getIcon()
    {
        return DataCache.items.containsKey(name) ? DataCache.items.get(name).getIcon() : DataCache.unknownIcon;
    }

    public String toString()
    {
        return "Module: " + name;
    }
}
