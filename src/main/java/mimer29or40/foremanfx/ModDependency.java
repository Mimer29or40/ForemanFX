package mimer29or40.foremanfx;

public class ModDependency
{
    public String modName = "";
    public Version version;
    public boolean optional    = false;
    public int     versionType = Equal;

    public static final int Equal              = 1;
    public static final int GreaterThan        = 2;
    public static final int GreaterThanOrEqual = 3;
}
