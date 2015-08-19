package mimer29or40.foremanfx;

public class ModDependency
{
    public String  modName     = "";
    public Version version     = new Version(0, 0, 0);
    public boolean optional    = false;
    public int     versionType = Equal;


    public String toString()
    {
        return String.format("Mod Name: %s Version: %s Optional: %s Version Type: %s", modName, version.toString(),
                             optional, versionType);
    }

    public static final int Equal              = 1;
    public static final int GreaterThan        = 2;
    public static final int GreaterThanOrEqual = 3;
}
