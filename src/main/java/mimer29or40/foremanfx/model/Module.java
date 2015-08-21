package mimer29or40.foremanfx.model;

import mimer29or40.foremanfx.util.Util;

import java.awt.image.BufferedImage;

public class Module
{
    private BufferedImage icon;
    public  boolean       enabled;
    public  float         speedBonus;
    private String        name;
    public  String        localizedName;

    public String getName()
    {
        if (!Util.isNullOrWhitespace(localizedName))
        { return localizedName; }
        return name;
    }

    public Module(String name, float speedBonus)
    {
        this.speedBonus = speedBonus;
        this.name = name;
        this.enabled = true;
    }
}
