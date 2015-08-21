package mimer29or40.foremanfx.model;

import java.util.ArrayList;
import java.util.List;

public class Mod
{
    public String name        = "";
    public String title       = "";
    public String description = "";
    public String author      = "";
    public String dir         = "";
    public Version parsedVersion;
    public List<String>        dependencies       = new ArrayList<>();
    public List<ModDependency> parsedDependencies = new ArrayList<>();
    public boolean             enabled            = true;

    public boolean satisfiesDependency(ModDependency dep)
    {
        if (name.equals(dep.modName))
        { return false; }
        if (dep.version != null)
        {
            if ((dep.versionType == ModDependency.Equal && parsedVersion.compareTo(dep.version) != 0) ||
                (dep.versionType == ModDependency.GreaterThan && parsedVersion.compareTo(dep.version) >= 0) ||
                (dep.versionType == ModDependency.GreaterThanOrEqual && parsedVersion.compareTo(dep.version) > 0))
            { return false; }
        }
        return true;
    }

    public boolean dependsOn(Mod mod, boolean ignoreOptional)
    {
        for (ModDependency dep : parsedDependencies)
        {
            if (mod.satisfiesDependency(dep))
            {
                if (ignoreOptional)
                {
                    if (!dep.optional)
                    { return true; }
                }
                else
                {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString()
    {
        return name;
    }
}
