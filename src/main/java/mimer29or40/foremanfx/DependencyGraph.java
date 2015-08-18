package mimer29or40.foremanfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DependencyGraph
{
    private List<Mod> mods;
    int[][] adjacencyMatrix;

    public DependencyGraph(List<Mod> mods)
    {
        this.mods = mods;
    }

    public void disableUnsatisfiedMods()
    {
        boolean changeMade = true;
        while (changeMade)
        {
            changeMade = false;
            for (Mod mod : mods)
            {
                for (ModDependency dep : mod.parsedDependencies)
                {
                    if (!dependencySatisfied(dep) && mod.enabled)
                    {
                        changeMade = true;
                        mod.enabled = false;
                    }
                }
            }
        }
    }

    public List<Mod> sortMods()
    {
        updateAdjacency();

        List<Mod> L = new ArrayList<>();
        HashSet<Mod> S = new HashSet<>();

        for (int i = 0; i < mods.size(); i++)
        {
            boolean dependency = false;
            for (int j = 0; j < mods.size(); j++)
            {
                if (adjacencyMatrix[j][i] == 1)
                {
                    dependency = true;
                    break;
                }
            }
            if (!dependency)
            {
                S.add(mods.get(i));
            }
        }

        while (!S.isEmpty())
        {
            Mod n = S.iterator().next();
            S.remove(n);

            L.add(n);
            int nIndex = mods.indexOf(n);

            for (int m = 0; m < mods.size(); m++)
            {
                if (adjacencyMatrix[nIndex][m] == 1)
                {
                    adjacencyMatrix[nIndex][m] = 0;

                    boolean incoming = false;
                    for (int i = 0; i < mods.size(); i++)
                    {
                        if (adjacencyMatrix[i][m] == 1)
                        {
                            incoming = true;
                            break;
                        }
                    }
                    if (!incoming)
                    {
                        S.add(mods.get(m));
                    }
                }
            }
        }

        //Should be no edges (dependencies) left by here

        L.sort(Collections.reverseOrder());
        return L;
    }

    public void updateAdjacency()
    {
        adjacencyMatrix = new int[mods.size()][mods.size()];

        for (int i = 0; i < mods.size(); i++)
        {
            for (int j = 0; j < mods.size(); j++)
            {
                if (mods.get(i).dependsOn(mods.get(j), false))
                {
                    adjacencyMatrix[i][j] = 1;
                }
                else
                {
                    adjacencyMatrix[i][j] = 0;
                }
            }
        }
    }

    public boolean dependencySatisfied(ModDependency dep)
    {
        if (dep.optional)
        { return true; }
        for (Mod mod : mods)
        {
            if (mod.enabled)
            { return true; }
        }
        return false;
    }
}
