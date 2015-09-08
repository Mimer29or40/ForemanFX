package mimer29or40.foremanfx.model;

import mimer29or40.foremanfx.util.StringUtil;

public class Language
{
    private String name;
    private String localName;

    public Language(String name, String localName)
    {
        this.name = name;
        this.localName = localName;
    }

    public String getLocalName()
    {
        if (!StringUtil.isNullOrWhitespace(localName))
        { return localName; }
        return name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name + "_" + localName;
    }
}
