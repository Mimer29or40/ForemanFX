package mimer29or40.foremanfx;

import mimer29or40.foremanfx.util.JsonHelper;
import mimer29or40.foremanfx.util.Logger;
import mimer29or40.foremanfx.util.Util;
import org.json.simple.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ConstantConditions")
public class DataCache
{
    private static String dataPath = ForemanFX.settings.getProp("factorioDir") + "/data";
    private static String modPath  = ForemanFX.settings.getProp("modDir");
    private static File   dataFile = new File(dataPath);
    private static File   modFile  = new File(modPath);

    public static List<Mod>             mods      = new ArrayList<>();
    public static Map<String, Language> languages = new TreeMap<>();

    //    public static Map<String, Item> Items = new HashMap<>();
//    public static Map<String, Recipe> Recipes = new HashMap<>();
//    public static Map<String, Assembler> Assemblers = new HashMap<>();
//    public static Map<String, Miner> Miners = new HashMap<>();
//    public static Map<String, Resource> Resources = new HashMap<>();
//    public static Map<String, Module> Modules = new HashMap<>();
//    public static Map<String, Inserter> Inserters = new HashMap<>();

    private static final float defaultRecipeTime = 0.5f;
    public static BufferedImage unknownIcon;
    //    private static Map<BufferedImage, Color>        colourCache = new HashMap<>();
    public static Map<String, Map<String, String>> localeFiles = new HashMap<>();

    public static Map<String, Exception> failedFiles           = new HashMap<>();
    public static Map<String, Exception> failedPathDirectories = new HashMap<>();

//    public static Map<String, byte[]> zipHashes = new HashMap<>();

    public static void loadAllData(List<String> enabledMods)
    {
        Logger.info("Loading Program Data...");
        clear();

        Globals globals = JsePlatform.debugGlobals();

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
            Logger.error(
                    "Error loading dataloader.lua. This file is required to load any values from the prototypes. " +
                    "Message: '" + e.getMessage() + "'");
            failedFiles.put(dataLoaderFile, e);
        }

        globals.load("function module(modname,...)\n" +
                     "\tend\n" +
                     "\t\n" +
                     "\trequire \"util\"\n" +
                     "\tutil = {}\n" +
                     "\tutil.table = {}\n" +
                     "\tutil.table.deepcopy = table.deepcopy\n" +
                     "\tutil.multiplystripes = multiplystripes").call();

        Pattern regex = Pattern.compile("require *\\(?(\"|')(?<module>[^\\.\"']+)(\"|')\\)?");
        // TODO see if "." is needed

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

        loadUnknownIcon();

        loadAllLanguage();
        loadLocaleFiles();

        Logger.info("Finished Loading Data...");

        reportErrors();

        debugData();
    }

    private static void loadAllLanguage()
    {
        Logger.info("Loading Languages...");
        File dirList = new File(dataFile, "core/locale");
        if (dirList.exists())
        {
            for (File dir : dirList.listFiles())
            {
                File info = new File(dir, "info.json");

                JSONObject object = JsonHelper.parse(Util.readFile(info));

                Language newLang = new Language(dir.getName(), (String) object.get("language-name"));

                languages.put(newLang.getName(), newLang);
            }
        }
        Logger.info(languages.size() + " Languages Loaded");
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
//        inserters.clear();
//        colourCache.clear();
        languages.clear();
        localeFiles.clear();
        failedFiles.clear();
        failedPathDirectories.clear();
    }

    private static void reportErrors()
    {
        if (!failedPathDirectories.isEmpty())
        {
            Logger.error("There were errors setting the lua path variable for the following directories:");
            for (String dir : failedPathDirectories.keySet())
            { Logger.error(String.format(" %s (%s)", dir, failedPathDirectories.get(dir).getMessage())); }
        }
        if (!failedFiles.isEmpty())
        {
            Logger.error("The following files could not be loaded due to errors:");
            for (String dir : failedFiles.keySet())
            { Logger.error(String.format(" %s (%s)", dir, failedFiles.get(dir).getMessage())); }
        }
        if (failedPathDirectories.isEmpty() && failedFiles.isEmpty())
        { Logger.info("No errors reported"); }
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
        Logger.info("Finding Mods...");
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
//        else
//        {
//            Map<String, String> splitModStrings = new HashMap<>();
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
//        mods = modGraph.sortMods(); TODO get this working
        Logger.info(mods.size() + " Mods Loaded");
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
        JSONObject object = JsonHelper.parse(json);
        try
        {

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
            Logger.info("Added Mod: " + newMod.name);
        }
        catch (Exception e)
        {
            Logger.error("There was a problem loading mod: " + object.get("name"));
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
                            newDependency.versionType = 1;// newDependency.Equal;
                            break;
                        case ">":
                            newDependency.versionType = 2;// newDependency.GreaterThan;
                            break;
                        case ">=":
                            newDependency.versionType = 3;// newDependency.GreaterThanOrEqual;
                            break;
                    }
                    token++;

                    newDependency.version = new Version(split[token]);
//                    token++;
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

    private static void loadLocaleFiles()
    {
        Logger.info("Loading Locales");
        String locale = "en";
        for (Mod mod : mods)
        {
            if (mod.enabled)
            {
                File localeDir = new File(mod.dir, "/locale/" + locale);
                if (localeDir.exists())
                {
                    for (File file : localeDir.listFiles())
                    {
                        if (file.getName().endsWith(".cfg"))
                        {
                            try
                            {
                                BufferedReader reader = new BufferedReader(new FileReader(file));

                                String line;
                                String currentIniSection = "none";
                                while ((line = reader.readLine()) != null)
                                {
                                    if (line.startsWith("[") && line.endsWith("]"))
                                    {
                                        currentIniSection = line.substring(1, line.length() - 1);
                                    }
                                    else
                                    {
                                        if (!localeFiles.containsKey(currentIniSection))
                                        {
                                            localeFiles.put(currentIniSection, new HashMap<>());
                                        }
                                        String[] split = line.split("=");
                                        if (split.length == 2)
                                        {
                                            localeFiles.get(currentIniSection).put(split[0], split[1]);
                                        }
                                    }
                                }
                                reader.close();
                            }
                            catch (Exception e)
                            {
                                failedFiles.put(file.getPath(), e);
                            }
                        }
                    }
                }
            }
        }
        Logger.info(localeFiles.size() + " Locales Loaded");
    }

    private static void loadUnknownIcon()
    {
        try
        {
            File file = new File(ForemanFX.class.getClassLoader().getResource("UnknownIcon.png").getFile());
            unknownIcon = ImageIO.read(file);
        }
        catch (Exception e)
        {
            Logger.error("Could not load UnknownIcon.png");
        }
    }

    private static BufferedImage loadImage(String fileName)
    {
        String fullPath;
        File file = new File(fileName);
        if (file.exists())
        {
            fullPath = file.getPath();
        }
//        else
//        {
//            string[] splitPath = fileName.Split('/');
//            splitPath[0] = splitPath[0].Trim('_');
//            fullPath = Mods.FirstOrDefault(m => m.Name == splitPath[0]).dir;
//
//            if (!String.IsNullOrEmpty(fullPath))
//            {
//                for (int i = 1; i < splitPath.Count(); i++)
//                Skip the first split section because it's the mod name, not a directory
//                {
//                    fullPath = Path.Combine(fullPath, splitPath[i]);
//                }
//            }
//        }
        BufferedImage image;
        try
        {
            image = ImageIO.read(file);
        }
        catch (Exception e)
        {
            Logger.error("Error loading icon " + fileName);
            return null;
        }
        return image;
    }

    private static void debugData()
    {
        // This will print out all of the data from the active lists to a log file.

        try
        {
            File log = new File("./data.log");
            if (!log.exists())
            { log.createNewFile(); }

//            BufferedWriter writer = new BufferedWriter(new FileWriter(log.getAbsoluteFile()));
            PrintWriter writer = new PrintWriter(log);

            writer.println("dataPath = " + dataPath);
            writer.println("modPath = " + modPath);
            writer.println("dataFile = " + dataFile);
            writer.println("modFile = " + modFile);
            writer.println("Mods:");
            for (Mod mod : mods)
            {
                writer.println(" Name: " + mod.name);
                writer.println(" Title: " + mod.title);
                writer.println(" Description: " + mod.description);
                writer.println(" Author: " + mod.author);
                writer.println(" Directory: " + mod.dir);
                writer.println(" Version: " + mod.parsedVersion.toString());
                writer.println(" Enabled: " + mod.enabled);
                writer.println(" Dependencies:");
                for (ModDependency dep : mod.parsedDependencies)
                { writer.println("  " + dep.toString()); }
                writer.println("");
            }
            writer.println("Languages:");
            for (String key : languages.keySet())
            {
                writer.println(" Name: " + key + "    " + languages.get(key).toString());
            }
            writer.println("Default Recipe Time: " + defaultRecipeTime);
            writer.println("Locales:");
            for (String key : localeFiles.keySet())
            {
                writer.println(" Ini Section: " + key);
                for (String key2 : localeFiles.get(key).keySet())
                {
                    writer.println("  " + key2 + " = " + localeFiles.get(key).get(key2));
                }
            }
            writer.println("Failed Files:");
            for (String key : failedFiles.keySet())
            {
                writer.println(" File: " + key);
                writer.println(" Exception: " + failedFiles.get(key).getMessage());
            }
            writer.println("Failed Path Directories:");
            for (String key : failedPathDirectories.keySet())
            {
                writer.println(" File: " + key);
                writer.println(" Exception: " + failedPathDirectories.get(key).getMessage());
            }
            writer.close();
        }
        catch (Exception e)
        {
            Logger.error("Something when wrong...");
        }
    }
}
