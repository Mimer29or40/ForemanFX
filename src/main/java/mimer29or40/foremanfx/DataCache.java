package mimer29or40.foremanfx;

import mimer29or40.foremanfx.util.JsonHelper;
import mimer29or40.foremanfx.util.Util;
import org.json.simple.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCache
{
    private static String dataPath = ForemanFX.settings.getProp("factorioDir") + "/data";
    private static String modPath  = ForemanFX.settings.getProp("modDir");
    private static File   dataFile = new File(dataPath);
    private static File   modFile  = new File(modPath);

    public static List<Mod>      mods      = new ArrayList<>();
    public static List<Language> languages = new ArrayList<>();

    //    public static Map<String, Item> Items = new HashMap<String, Item>();
//    public static Map<String, Recipe> Recipes = new HashMap<String, Recipe>();
//    public static Map<String, Assembler> Assemblers = new HashMap<String, Assembler>();
//    public static Map<String, Miner> Miners = new HashMap<String, Miner>();
//    public static Map<String, Resource> Resources = new HashMap<String, Resource>();
//    public static Map<String, Module> Modules = new HashMap<String, Module>();
//    public static Map<String, Inserter> Inserters = new HashMap<String, Inserter>();
//
    private static final float                  defaultRecipeTime     = 0.5f;
    //    private static Map<Bitmap, Color> colourCache = new HashMap<Bitmap, Color>();
//    public static Bitmap UnknownIcon;
//    public static Map<String, Map<String, String>> LocaleFiles = new HashMap<String, HashMap<String, String>>();
//
    public static        Map<String, Exception> failedFiles           = new HashMap<String, Exception>();
    public static        Map<String, Exception> failedPathDirectories = new HashMap<String, Exception>();
//
//    public static Map<String, byte[]> zipHashes = new HashMap<string, byte[]>();

    public static void loadAllData(List<String> enabledMods)
    {
        clear();

        Globals globals = JsePlatform.debugGlobals();
        LuaValue mainChunk = globals.checktable();

        findAllMods(enabledMods);

        for (Mod mod : mods)
        {
            if (mod.enabled)
            {
                addLuaPackagePath(globals, mod.dir);
            }
        }
        addLuaPackagePath(globals, dataPath + "/core/lualib");

        String dataLoaderFile = dataPath + "/core/lualib/dataloader.lua";
        try
        {
            globals.loadfile(dataLoaderFile).call();
        }
        catch (Exception e)
        {
            failedFiles.put(dataLoaderFile, e);
            System.out.println(String.format(
                    "Error loading dataloader.lua. This file is required to load any values from the prototypes. " +
                    "Message: '%s'", e.getMessage()));
        }

        globals.load("function module(modname,...)\n" +
                     "\tend\n" +
                     "\t\n" +
                     "\trequire \"util\"\n" +
                     "\tutil = {}\n" +
                     "\tutil.table = {}\n" +
                     "\tutil.table.deepcopy = table.deepcopy\n" +
                     "\tutil.multiplystripes = multiplystripes").call();

        Pattern regex = Pattern.compile(
                "require *\\(?(\"|')(?<modulename>[^\\.\"']+)(\"|')\\)?"); // TODO see if "." is needed

        for (String fileName : new String[]{"data.lua", "data-update.lua", "data-final-fixes.lua"})
        {
            for (Mod mod : mods)
            {
                if (mod.enabled)
                {
                    String dataString = mod.dir + "/" + fileName;
                    File dataFile = new File(dataString);

                    if (dataFile.exists())
                    {
                        try
                        {
                            String fileContents = Util.listToString(Util.readFile(dataFile));
                            Matcher matcher = regex.matcher(fileContents);

                            while (matcher.find())
                            {
                                String s = matcher.group();
                                globals.load(s).call();
                            }
                            LuaValue chunk = globals.loadfile(dataString);
                            chunk.call();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            failedFiles.put(dataFile.getPath(), e);
                        }
                    }
                }
            }
        }

        for (String type : new String[]{"item", "fluid", "capsule", "module", "ammo", "gun", "armor",
                                        "blueprint", "deconstruction-item", "mining-tool", "repair-tool", "tool"})
        {
            interpretItems(globals, type);
        }
    }

    public static void clear()
    {
        mods.clear();
//        items.clear();
//        recipes.clear();
//        assemblers.clear();
//        miners.clear();
//        resources.clear();
//        modules.clear();
//        colourCache.clear();
//        localeFiles.clear();
        failedFiles.clear();
        failedPathDirectories.clear();
//        inserters.clear();
        languages.clear();
    }

    private static void addLuaPackagePath(Globals globals, String dir)
    {
        try
        {
            String command = String.format("package.path = package.path .. ';%s/?.lua'", dir);
            globals.load(command).call();
        }
        catch (Exception e)
        {
            failedPathDirectories.put(dir, e);
        }
    }

    private static void findAllMods(List<String> enabledMods)
    {
        if (dataFile.exists())
        {
            for (File file : dataFile.listFiles())
            {
                if (file.isDirectory())
                { readModInfoFile(file); }
            }
        }
        if (modFile.exists())
        {
            for (File file : modFile.listFiles())
            {
                if (file.isDirectory())
                { readModInfoFile(file); }
//                else if (file.getName().contains(".zip")); TODO make .zips work
//                {
//                    readModInfoZip(file);
//                }
            }
        }

        Map<String, Boolean> enabledModsFromFile = new HashMap<String, Boolean>();
        File modListFile = new File(modFile, "mod-list.json");
        if (modListFile.exists())
        {
            List<String> json = Util.readFile(modListFile);
            JSONObject object = JsonHelper.parse(json);
            List<JSONObject> mods = (List<JSONObject>) object.get("mods");
            for (JSONObject obj : mods)
            {
                String name = (String) obj.get("name");
                boolean enabled = Boolean.valueOf((String) obj.get("enabled"));
                enabledModsFromFile.put(name, enabled);
            }
        }
        if (enabledMods != null)
        {
            for (Mod mod : mods)
            {
                mod.enabled = enabledMods.contains(mod.name);
            }
        }
//        else TODO figure out what this does
//        {
//            Dictionary<String, String> splitModStrings = new Dictionary<string,string>();
//            foreach (String s in Properties.Settings.Default.EnabledMods)
//            {
//                var split = s.Split('|');
//                splitModStrings.Add(split[0], split[1]);
//            }
//            foreach (Mod mod in Mods)
//            {
//                if (splitModStrings.ContainsKey(mod.Name))
//                {
//                    mod.Enabled = (splitModStrings[mod.Name] == "True");
//                }
//                else if (enabledModsFromFile.ContainsKey(mod.Name))
//                {
//                    mod.Enabled = enabledModsFromFile[mod.Name];
//                }
//                else
//                {
//                    mod.Enabled = true;
//                }
//            }
//        }
        DependencyGraph modGraph = new DependencyGraph(mods);
        modGraph.disableUnsatisfiedMods();
//        mods = modGraph.sortMods();
    }

    private static void readModInfoFile(File dir)
    {
        File info = new File(dir, "info.json");
        if (!info.exists())
        { return; }
        List<String> json = Util.readFile(info);
        readModInfo(json, dir);
    }

    private static void readModInfo(List<String> json, File dir)
    {
        try
        {
            JSONObject object = JsonHelper.parse(json);

            Mod newMod = new Mod();
            newMod.name = (String) object.get("name");
            newMod.title = (String) object.get("title");
            newMod.description = (String) object.get("description");
            newMod.author = (String) object.get("author");
            newMod.dir = dir.getPath();
            newMod.parsedVersion = (object.get("version")) != null ?
                                   new Version((String) object.get("version")) : new Version(0, 0, 0);
            newMod.dependencies = (List<String>) object.get("dependencies");
            parseModDependencies(newMod);

            mods.add(newMod);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void parseModDependencies(Mod mod)
    {
        if (mod.name.equals("base"))
        { mod.dependencies.add("core"); }
        if (mod.dependencies != null)
        {
            for (String depString : mod.dependencies)
            {
                int token = 0;

                ModDependency newDependency = new ModDependency();

                String[] split = depString.split(" ");

                if (split[token].equals("?"))
                {
                    newDependency.optional = true;
                    token++;
                }
                newDependency.modName = split[token];
                token++;

                if (split.length == token + 2)
                {
                    switch (split[token])
                    {
                        case "=":
                            newDependency.versionType = newDependency.Equal;
                            break;
                        case ">":
                            newDependency.versionType = newDependency.GreaterThan;
                            break;
                        case ">=":
                            newDependency.versionType = newDependency.GreaterThanOrEqual;
                            break;
                    }
                    token++;

                    newDependency.version = new Version(split[token]);
                    token++;
                }
                mod.parsedDependencies.add(newDependency);
            }
        }
    }

    private static void interpretItems(Globals globals, String typeName)
    {
//        LuaTable itemTable = (LuaTable) globals.checktable(); TODO make lua work
//        System.out.println(itemTable.tojstring());
    }
}
