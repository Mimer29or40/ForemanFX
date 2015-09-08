package mimer29or40.foremanfx;

import javafx.scene.image.Image;
import mimer29or40.foremanfx.model.*;
import mimer29or40.foremanfx.util.*;
import org.json.simple.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

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

    public static Map<String, Item>      items      = new HashMap<>();
    public static Map<String, Recipe>    recipes    = new HashMap<>();
    public static Map<String, Assembler> assemblers = new HashMap<>();
    public static Map<String, Miner>     miners     = new HashMap<>();
    public static Map<String, Resource>  resources  = new HashMap<>();
    public static Map<String, Module>    modules    = new HashMap<>();
//    public static Map<String, Inserter> inserters = new HashMap<>();

    private static final float defaultRecipeTime = 0.5f;
    public static Image unknownIcon;
    //    private static Map<Image, Color>        colourCache = new HashMap<>();
    public static Map<String, Map<String, String>> localeFiles = new TreeMap<>();

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
            Logger.info("Running Lua...");
            globals.loadfile(dataLoaderFile).call();
        }
        catch (Exception e)
        {
            Logger.error("Error loading dataloader.lua. This is required to load any values from the prototypes. '" +
                         e.getMessage() + "'");
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

//        Pattern regex = Pattern.compile("require *\\(?(\"|')(?<module>[^\\.\"']+)(\"|')\\)?");
        Pattern regex = Pattern.compile("require *\\(?(\"|')(?<module>[^\"']+)(\"|')\\)?");
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
                            String fileContents = ListUtil.listToString(FileUtil.readFile(dataFile));
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

        Logger.info("Extracting data....");

        LuaTable dataTable = LuaUtil.getTable(globals, "data");
        LuaTable rawTable = LuaUtil.getTable(dataTable, "raw");
        debugLuaData(rawTable, "rawTable");

        Logger.info("Loading Items...");
        for (String type : new String[]{"item", "fluid", "capsule", "module", "ammo", "gun", "armor",
                                        "blueprint", "deconstruction-item", "mining-tool", "repair-tool", "tool"})
        {
            interpretItems(rawTable, type);
        }
        Logger.info(items.size() + " Items Loaded");

        Logger.info("Loading Recipes...");
        LuaTable recipeTable = LuaUtil.getTable(rawTable, "recipe");
        if (!LuaUtil.isNull(recipeTable))
        {
            for (LuaValue key : recipeTable.keys())
            {
                interpretLuaRecipe(key.toString(), LuaUtil.getTable(recipeTable, key));
            }
        }
        Logger.info(recipes.size() + " Recipes Loaded");

        Logger.info("Loading Assembling Machines...");
        LuaTable assemblerTable = LuaUtil.getTable(rawTable, "assembling-machine");
        if (!LuaUtil.isNull(assemblerTable))
        {
            for (LuaValue key : assemblerTable.keys())
            {
                interpretAssemblingMachine(key.toString(), LuaUtil.getTable(assemblerTable, key));
            }
        }
        Logger.info("Loading Furnaces...");
        LuaTable furnaceTable = LuaUtil.getTable(rawTable, "furnace");
        if (!LuaUtil.isNull(furnaceTable))
        {
            for (LuaValue key : furnaceTable.keys())
            {
                interpretFurnaces(key.toString(), LuaUtil.getTable(furnaceTable, key));
            }
        }
        Logger.info(assemblers.size() + " Assembling Machines Loaded");

        Logger.info("Loading Miners...");
        LuaTable minerTable = LuaUtil.getTable(rawTable, "mining-drill");
        if (!LuaUtil.isNull(minerTable))
        {
            for (LuaValue key : minerTable.keys())
            {
                interpretMiner(key.toString(), LuaUtil.getTable(minerTable, key));
            }
        }
        Logger.info(miners.size() + " Miners Loaded");

        Logger.info("Loading Resources...");
        LuaTable resourceTable = LuaUtil.getTable(rawTable, "resource");
        if (!LuaUtil.isNull(resourceTable))
        {
            for (LuaValue key : resourceTable.keys())
            {
                interpretResource(key.toString(), LuaUtil.getTable(resourceTable, key));
            }
        }
        Logger.info(resources.size() + " Resources Loaded");

        Logger.info("Loading Modules...");
        LuaTable moduleTable = LuaUtil.getTable(rawTable, "module");
        modules.put("none", new Module("None", 0));
        if (!LuaUtil.isNull(moduleTable))
        {
            for (LuaValue key : moduleTable.keys())
            {
                interpretModule(key.toString(), LuaUtil.getTable(moduleTable, key));
            }
        }
        Logger.info(modules.size() + " Modules Loaded");

        // TODO inserter
        // TODO transport-belt

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

                JSONObject object = JsonHelper.parse(FileUtil.readFile(info));

                Language newLang = new Language(dir.getName(), (String) object.get("language-name"));

                languages.put(newLang.getLocalName(), newLang);
            }
        }
        Logger.info(languages.size() + " Languages Loaded");
    }

    public static void clear()
    {
        mods.clear();
        items.clear();
        recipes.clear();
        assemblers.clear();
        miners.clear();
        resources.clear();
        modules.clear();
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

        Map<String, Boolean> enabledModsFromFile = new HashMap<>();
        File modListFile = new File(modFile, "mod-list.json");
        if (modListFile.exists())
        {
            List<String> json = FileUtil.readFile(modListFile);
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
//        else TODO read userData
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
        List<String> json = FileUtil.readFile(info);
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
            Logger.error("There was a problem loading mod: " + object.get("name") + " " + e.getMessage());
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

    private static void interpretItems(LuaTable rawTable, String typeName)
    {
        for (LuaValue key : rawTable.keys())
        {
            if (key.tojstring().equals(typeName))
            {
                LuaTable itemTables = LuaUtil.getTable(rawTable, key);

                for (LuaValue key2 : itemTables.keys())
                {
                    LuaTable item = LuaUtil.getTable(itemTables, key2);

                    interpretLuaItem(key2.tojstring(), item);
                }
            }
        }
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
//            File file = new File(ForemanFX.class.getClassLoader().getResource("UnknownIcon.png").getFile());
//            unknownIcon = ImageIO.read(file);
            unknownIcon = new Image(ForemanFX.class.getClassLoader().getResource("UnknownIcon.png").openStream());
        }
        catch (Exception e)
        {
            Logger.error("Could not load UnknownIcon.png " + e.getMessage());
        }
    }

    private static Image loadImage(String fileName)
    {
        String fullPath = "";
        File file = new File(fileName);
        if (!file.exists())
        {
            String[] splitPath = fileName.split("/");
            splitPath[0] = splitPath[0].replace("_", "");

            for (Mod mod : mods)
            {
                if (mod.name.equals(splitPath[0]))
                {
                    fullPath = mod.dir;
                }
            }

            if (!StringUtil.isNullOrWhitespace(fullPath))
            {
                for (int i = 1; i < splitPath.length; i++)
                {
                    fullPath = fullPath + "/" + splitPath[i];
                }
            }
            file = new File(fullPath);
        }
        try
        {
//            return ImageIO.read(file);
            return new Image(file.toURI().toURL().openStream());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static void interpretLuaItem(String name, LuaTable item)
    {
        String iconString = LuaUtil.getString(item, "icon", true);
        Item newItem = new Item(name, loadImage(iconString));

        if (!items.containsKey(name))
        { items.put(name, newItem); }
    }

    private static Item findOrCreateUnknownItem(String itemName)
    {
        Item newItem;
        if (!items.containsKey(itemName))
        {
            items.put(itemName, newItem = new Item(itemName));
        }
        else
        {
            newItem = items.get(itemName);
        }
        return newItem;
    }

    private static void interpretLuaRecipe(String name, LuaTable recipe)
    {
        try
        {
            float time = LuaUtil.getFloat(recipe, "energy_required", true, 0.5F);
            Map<Item, Float> ingredients = extractIngredientsFromLuaRecipe(recipe);
            Map<Item, Float> results = extractResultsFromLuaRecipe(recipe);

            Recipe newRecipe = new Recipe(name, time == 0.0F ? defaultRecipeTime : time, ingredients, results);

            newRecipe.category = LuaUtil.getString(recipe, "category", true, "crafting");

            String iconFile = LuaUtil.getString(recipe, "icon", true);
            if (iconFile != null)
            { newRecipe.setIcon(loadImage(iconFile)); }
            for (Item result : results.keySet())
            { result.addRecipe(newRecipe); }
            recipes.put(newRecipe.getName(), newRecipe);
        }
        catch (MissingPrototypeValueException e)
        {
            Logger.error(String.format(
                    "Error reading value '%s' from recipe prototype '%s'. Returned error message: '%s'",
                    e.key, name, e.getMessage()));
        }
    }

    private static void interpretAssemblingMachine(String name, LuaTable assembler)
    {
        try
        {
            Assembler newAssembler = new Assembler(name);

            newAssembler.icon = loadImage(LuaUtil.getString(assembler, "icon", true));
            newAssembler.maxIngredients = LuaUtil.getInt(assembler, "ingredient_count");
            newAssembler.moduleSlots = LuaUtil.getInt(assembler, "module_slots", true, 0);
            if (newAssembler.moduleSlots == 0)
            {
                LuaTable moduleTable = LuaUtil.getTable(assembler, "module_specification", true);
                if (!LuaUtil.isNull(moduleTable))
                { newAssembler.moduleSlots = LuaUtil.getInt(moduleTable, "module_slots", true, 0); }
            }
            newAssembler.speed = LuaUtil.getFloat(assembler, "crafting_speed");
            LuaTable effects = LuaUtil.getTable(assembler, "allowed_effects", true);
            if (!LuaUtil.isNull(effects))
            {
                for (LuaValue key : effects.keys())
                {
                    newAssembler.addAllowedEffects(LuaUtil.getString(effects, key));
                }
            }
            LuaTable categories = LuaUtil.getTable(assembler, "crafting_categories", true);
            for (LuaValue key : categories.keys())
            {
                newAssembler.addCategories(LuaUtil.getString(categories, key));
            }
//            for (String s : enabledAssemblers) TODO find settings
//            {
//                if (s.split("|")[0].equals(name))
//                {
//                    newAssembler.enabled = (s.split("|")[1].equals("True"));
//                }
//            }
            assemblers.put(name, newAssembler);
        }
        catch (MissingPrototypeValueException e)
        {
            Logger.error(String.format(
                    "Error reading value '%s' from assembler prototype '%s'. Returned error message: '%s'",
                    e.key, name, e.getMessage()));
        }
    }

    private static void interpretFurnaces(String name, LuaTable furnace)
    {
        try
        {
            Assembler newFurnace = new Assembler(name);

            newFurnace.icon = loadImage(LuaUtil.getString(furnace, "icon", true));
            newFurnace.maxIngredients = 1;
            newFurnace.moduleSlots = LuaUtil.getInt(furnace, "module_slots", true, 0);
            if (newFurnace.moduleSlots == 0)
            {
                LuaTable moduleTable = LuaUtil.getTable(furnace, "module_specification", true);
                if (!LuaUtil.isNull(moduleTable))
                { newFurnace.moduleSlots = LuaUtil.getInt(moduleTable, "module_slots", true, 0); }
            }
            newFurnace.speed = LuaUtil.getFloat(furnace, "crafting_speed");
            LuaTable categories = LuaUtil.getTable(furnace, "crafting_categories", true);
            for (LuaValue key : categories.keys())
            {
                newFurnace.addCategories(LuaUtil.getString(categories, key));
            }
//            for (String s : enabledAssemblers) TODO find settings
//            {
//                if (s.split("|")[0].equals(name))
//                {
//                    newAssembler.enabled = (s.split("|")[1].equals("True"));
//                }
//            }
            assemblers.put(name, newFurnace);
        }
        catch (MissingPrototypeValueException e)
        {
            Logger.error(String.format(
                    "Error reading value '%s' from furnace prototype '%s'. Returned error message: '%s'",
                    e.key, name, e.getMessage()));
        }
    }

    private static void interpretMiner(String name, LuaTable miner)
    {
        try
        {
            Miner newMiner = new Miner(name);
            newMiner.icon = loadImage(LuaUtil.getString(miner, "icon", true));
            newMiner.miningPower = LuaUtil.getFloat(miner, "mining_power");
            newMiner.speed = LuaUtil.getFloat(miner, "mining_speed");
            newMiner.moduleSlots = LuaUtil.getInt(miner, "module_slots", true, 0);
            if (newMiner.moduleSlots == 0)
            {
                LuaTable moduleTable = LuaUtil.getTable(miner, "module_specification", true);
                if (!LuaUtil.isNull(moduleTable))
                { newMiner.moduleSlots = LuaUtil.getInt(moduleTable, "module_slots", true, 0); }
            }
            LuaTable categories = LuaUtil.getTable(miner, "crafting_categories", true);
            for (LuaValue key : categories.keys())
            {
                newMiner.resourceCategories.add(LuaUtil.getString(categories, key));
            }
//            for (String s : enabledAssemblers) TODO find settings
//            {
//                if (s.split("|")[0].equals(name))
//                {
//                    newAssembler.enabled = (s.split("|")[1].equals("True"));
//                }
//            }
            miners.put(name, newMiner);
        }
        catch (MissingPrototypeValueException e)
        {
            Logger.error(String.format(
                    "Error reading value '%s' from miner prototype '%s'. Returned error message: '%s'",
                    e.key, name, e.getMessage()));
        }
    }

    private static void interpretResource(String name, LuaTable resource)
    {
        try
        {
            LuaTable minableTable = LuaUtil.getTable(resource, "minable");
            if (LuaUtil.isNull(minableTable))
            {
                return; // Cant obtain resource
            }
            Resource newResource = new Resource(name);
            newResource.category = LuaUtil.getString(resource, "category", true, "basic-solid");
            newResource.hardness = LuaUtil.getFloat(minableTable, "hardness");
            newResource.time = LuaUtil.getFloat(minableTable, "mining_time");

            if (!LuaUtil.isNull(minableTable, "result"))
            {
                newResource.result = LuaUtil.getString(minableTable, "result");
            }
            else
            {
                try
                {
                    LuaTable resultsTable = LuaUtil.getTable(minableTable, "results");
                    newResource.result = LuaUtil.getString((LuaTable) resultsTable.get(1), "name");
                }
                catch (Exception e)
                {
                    throw new MissingPrototypeValueException(minableTable, "results", e.getMessage());
                }
            }
            resources.put(name, newResource);
        }
        catch (MissingPrototypeValueException e)
        {
            Logger.error(String.format(
                    "Error reading value '%s' from resource prototype '%s'. Returned error message: '%s'",
                    e.key, name, e.getMessage()));
        }
    }

    private static void interpretModule(String name, LuaTable module)
    {
        try
        {
            float speedBonus = 0F;

            LuaTable effectTable = LuaUtil.getTable(module, "effect");
            LuaTable speedTable = LuaUtil.getTable(effectTable, "speed", true);
            if (!LuaUtil.isNull(speedTable))
            {
                speedBonus = LuaUtil.getFloat(speedTable, "bonus", true, -1F);
            }
            if (LuaUtil.isNull(speedTable) || speedBonus <= 0)
            { return; }

            Module newModule = new Module(name, speedBonus);

//            for (String s : enabledAssemblers) TODO find settings
//            {
//                if (s.split("|")[0].equals(name))
//                {
//                    newAssembler.enabled = (s.split("|")[1].equals("True"));
//                }
//            }
            modules.put(name, newModule);
        }
        catch (MissingPrototypeValueException e)
        {
            Logger.error(String.format(
                    "Error reading value '%s' from module prototype '%s'. Returned error message: '%s'",
                    e.key, name, e.getMessage()));
        }
    }

    private static Map<Item, Float> extractResultsFromLuaRecipe(LuaTable recipe)
    {
        Map<Item, Float> results = new HashMap<>();
        if (!LuaUtil.isNull(recipe, "result"))
        {
            String resultName = LuaUtil.getString(recipe, "result", false);
            float resultCount = LuaUtil.getFloat(recipe, "result_count", true);
            if (resultCount == 0F)
            { resultCount = 1F; }
            results.put(findOrCreateUnknownItem(resultName), resultCount);

        }
        else if (!LuaUtil.isNull(recipe, "results"))
        {
            LuaTable resultsTable = (LuaTable) recipe.get(LuaValue.valueOf("results"));
            for (LuaValue key : resultsTable.keys())
            {
                LuaTable result = LuaUtil.getTable(resultsTable, key);
                Item newResult = !LuaUtil.isNull(result, "name") ?
                                 findOrCreateUnknownItem(LuaUtil.getString(result, "name")) :
                                 findOrCreateUnknownItem(LuaUtil.getString(result, 1));
                float amount = !LuaUtil.isNull(result, "amount") ?
                               LuaUtil.getFloat(result, "amount") :
                               LuaUtil.getFloat(result, 2);
                if (results.containsKey(newResult))
                { results.put(newResult, results.get(newResult) + amount); }
                else
                { results.put(newResult, amount); }
            }
        }
        return results;
    }

    private static Map<Item, Float> extractIngredientsFromLuaRecipe(LuaTable recipe)
    {
        Map<Item, Float> ingredients = new HashMap<>();
        LuaTable ingredientsTable = LuaUtil.getTable(recipe, "ingredients");
        for (LuaValue key : ingredientsTable.keys())
        {
            LuaTable ingredientTable = LuaUtil.getTable(ingredientsTable, key);

            String name = !LuaUtil.isNull(ingredientTable, "name") ?
                          LuaUtil.getString(ingredientTable, "name") :
                          ingredientTable.get(1).checkjstring();
            float amount = !LuaUtil.isNull(ingredientTable, "amount") ?
                           LuaUtil.getValue(ingredientTable, "amount").tofloat() :
                           ingredientTable.get(2).tofloat();

            Item ingredient = findOrCreateUnknownItem(name);

            if (!ingredients.containsKey(ingredient))
            { ingredients.put(ingredient, amount); }
            else
            { ingredients.put(ingredient, ingredients.get(ingredient) + amount); }
        }
        return ingredients;
    }

    private static void debugLuaData(LuaTable table, String tableName)
    {
        // This will print out all of the rawData to a log file.
        try
        {
            File log = new File("./" + tableName + ".log");
            if (!log.exists())
            { log.createNewFile(); }

            PrintWriter writer = new PrintWriter(log);

            writeLines(table, writer, tableName);

            writer.close();
        }
        catch (Exception e)
        {
            Logger.error("Something when wrong... " + e.getMessage());
        }
    }

    private static void writeLines(LuaTable table, PrintWriter writer, String prefix)
    {
        for (LuaValue key : table.keys())
        {
            LuaValue value = table.get(key);
            if (LuaUtil.getType(value).equals("table"))
            {
                writeLines((LuaTable) value, writer, prefix + " " + key.checkjstring());
            }
            else
            {
                writer.println(prefix + " " + key.checkjstring() + " " + table.get(key));
            }
        }
    }

    private static void debugData()
    {
        // This will print out all of the data from the active lists to a log file.
        try
        {
            File log = new File("./data.log");
            if (!log.exists())
            { log.createNewFile(); }

            PrintWriter writer = new PrintWriter(log);

            writer.println("dataPath = " + dataPath);
            writer.println("modPath = " + modPath);
            writer.println("dataFile = " + dataFile);
            writer.println("modFile = " + modFile);
            writer.println("Default Recipe Time: " + defaultRecipeTime);
            writer.println("Mods:");
            for (Mod mod : mods)
            {
                writer.println("  Name: " + mod.name);
                writer.println("  Title: " + mod.title);
                writer.println("  Description: " + mod.description);
                writer.println("  Author: " + mod.author);
                writer.println("  Directory: " + mod.dir);
                writer.println("  Version: " + mod.parsedVersion.toString());
                writer.println("  Enabled: " + mod.enabled);
                writer.println("  Dependencies:");
                for (ModDependency dep : mod.parsedDependencies)
                { writer.println("    " + dep.toString()); }
                writer.println("");
            }
            writer.println("Languages:");
            for (String key : languages.keySet())
            {
                writer.println("  Name: " + key + "  " + languages.get(key).toString());
            }
            writer.println("Items:");
            for (String key : items.keySet())
            {
                Item item = items.get(key);
                writer.println("  Name: " + item.getLocalizedName());
                writer.println("  Unlocalized: " + item.getName());
                writer.println("  Missing Item: " + item.isMissingItem);
                for (Recipe recipe : item.getRecipes())
                {
                    writer.println("    Recipe: " + recipe.getLocalizedName());
                }
                writer.println("");
            }
            writer.println("Recipes:");
            for (Recipe recipe : recipes.values())
            {
                writer.println("  Name: " + recipe.getLocalizedName());
                writer.println("  Unlocalized: " + recipe.getName());
                writer.println("  Time: " + recipe.time);
                writer.println("  Category: " + recipe.category);
                writer.println("  Missing Recipe: " + recipe.isMissingRecipe);
                writer.println("  Ingredients: ");
                for (Item item : recipe.getIngredients().keySet())
                {
                    writer.println("    Item: " + item.getLocalizedName() + " x" + recipe.getIngredients().get(item));
                }
                writer.println("  Results: ");
                for (Item item : recipe.getResults().keySet())
                {
                    writer.println("    Item: " + item.getLocalizedName() + " x" + recipe.getResults().get(item));
                }
                writer.println(" ");
            }
            writer.println("Assemblers:");
            for (Assembler assembler : assemblers.values())
            {
                writer.println("  Name: " + assembler.getLocalizedName());
                writer.println("  Unlocalized: " + assembler.getName());
                writer.println("  Categories:");
                for (String category : assembler.getCategories())
                {
                    writer.println("    " + category);
                }
                writer.println("  Allowed Effects:");
                for (String effect : assembler.getAllowedEffects())
                {
                    writer.println("    " + effect);
                }
                writer.println("  Permutations:");
                for (MachinePermutation permutation : assembler.getAllPermutations())
                {
                    writer.println("    Rate: " + permutation.getRate(60));
                    writer.println("      Name: " + permutation.modules.get(0).getLocalizedName() +
                                   " Speed Bonus: " + permutation.modules.get(0).speedBonus);
                }
                writer.println("  Max Ingredients: " + assembler.maxIngredients);
                writer.println("  Module Slots: " + assembler.moduleSlots);
                writer.println("  Speed: " + assembler.speed);
                writer.println("");
            }
            writer.println("Miners:");
            for (Miner miner : miners.values())
            {
                writer.println("  Name: " + miner.getLocalizedName());
                writer.println("  Unlocalized: " + miner.getName());
                writer.println("  Mining Power: " + miner.miningPower);
                writer.println("  Module Slots: " + miner.moduleSlots);
                writer.println("  Speed: " + miner.speed);
                writer.println("");
            }
            writer.println("Resources:");
            for (Resource resource : resources.values())
            {
                writer.println("  Name: " + resource.name);
                writer.println("  Results: " + resource.result);
                writer.println("  Hardness: " + resource.hardness);
                writer.println("  Category: " + resource.category);
                writer.println("  Time: " + resource.time);
                writer.println("");
            }
            writer.println("Modules:");
            for (Module module : modules.values())
            {
                writer.println("  Name: " + module.getLocalizedName());
                writer.println("  Unlocalized: " + module.getName());
                writer.println("  Speed Bonus: " + module.speedBonus);
                writer.println("");
            }
            writer.println("Locales:");
            for (String key : localeFiles.keySet())
            {
                writer.println("  Ini Section: " + key);
                for (String key2 : localeFiles.get(key).keySet())
                {
                    writer.println("    " + key2 + " = " + localeFiles.get(key).get(key2));
                }
            }
            writer.println("Failed Files:");
            for (String key : failedFiles.keySet())
            {
                writer.println("  File: " + key);
                writer.println("  Exception: " + failedFiles.get(key).getMessage());
            }
            writer.println("Failed Path Directories:");
            for (String key : failedPathDirectories.keySet())
            {
                writer.println("  File: " + key);
                writer.println("  Exception: " + failedPathDirectories.get(key).getMessage());
            }

            writer.close();
        }
        catch (Exception e)
        {
            Logger.error("Something when wrong... " + e.getMessage());
            e.printStackTrace();
        }
    }
}
