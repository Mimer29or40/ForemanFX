package mimer29or40.foremanfx.model;

import javafx.scene.image.Image;
import mimer29or40.foremanfx.DataCache;

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
        if (DataCache.localeFiles.containsKey("item-name") && DataCache.localeFiles.get("item-name").containsKey(name))
        {
            return DataCache.localeFiles.get("item-name").get(name);
        }
        return name;
    }

    public String getName()
    {
        return name;
    }

    public Image getIcon()
    {
        return DataCache.items.containsKey(name) ? DataCache.items.get(name).getIcon() : null;
    }

    public String toString()
    {
        return "Module: " + name;
    }
}
