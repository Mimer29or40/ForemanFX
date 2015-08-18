package mimer29or40.foremanfx;

import mimer29or40.foremanfx.util.Util;

public class Language
{
    private String name;
    private String localName;

    public Language(String name, String localName)
    {
        this.name = name;
        this.localName = localName;
    }

    public Language(String name)
    {
        this(name, "");
    }

    public String getName()
    {
        if (!Util.isNullOrWhitespace(localName))
        { return localName; }
        return name;
    }

    public String getNameFull()
    {
        return name + "," + localName;
    }
}
