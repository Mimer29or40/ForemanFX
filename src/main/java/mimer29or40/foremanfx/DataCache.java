package mimer29or40.foremanfx;

import mimer29or40.foremanfx.model.*;
import mimer29or40.foremanfx.util.JsonHelper;
import mimer29or40.foremanfx.util.Logger;
import mimer29or40.foremanfx.util.LuaHelper;
import mimer29or40.foremanfx.util.Util;
import org.json.simple.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
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

    public static Map<String, Item>   items   = new HashMap<>();
    public static Map<String, Recipe> recipes = new HashMap<>();
//    public static Map<String, Assembler> assemblers = new HashMap<>();
//    public static Map<String, Miner> miners = new HashMap<>();
//    public static Map<String, Resource> resources = new HashMap<>();
//    public static Map<String, Module> modules = new HashMap<>();
//    public static Map<String, Inserter> inserters = new HashMap<>();

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

        Logger.info("Extracting data....");

        LuaTable dataTable = LuaHelper.getTable(globals, "data");
        LuaTable rawTable = LuaHelper.getTable(dataTable, "raw");

        Logger.info("Loading Items...");
        for (String type : new String[]{"item", "fluid", "capsule", "module", "ammo", "gun", "armor",
                                        "blueprint", "deconstruction-item", "mining-tool", "repair-tool", "tool"})
        {
            interpretItems(rawTable, type);
        }
        Logger.info(items.size() + " Items Loaded");

        Logger.info("Loading Recipes...");
        LuaTable recipeTable = LuaHelper.getTable(rawTable, "recipe");
        if (recipeTable != LuaValue.NIL)
        {
            for (LuaValue key : recipeTable.keys())
            {
                interpretLuaRecipe(key.toString(), LuaHelper.getTable(recipeTable, key));
            }
        }
        Logger.info(recipes.size() + " Recipes Loaded");

        Logger.info("Loading Assembling Machines...");
        LuaTable assemblerTable = LuaHelper.getTable(rawTable, "assembling-machine");
        if (assemblerTable != LuaValue.NIL)
        {
            for (LuaValue key : assemblerTable.keys())
            {
                interpretAssemblingMachine(key.toString(), LuaHelper.getTable(assemblerTable, key));
            }
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
        items.clear();
        recipes.clear();
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
                LuaTable itemTables = LuaHelper.getTable(rawTable, key);

                for (LuaValue key2 : itemTables.keys())
                {
                    LuaTable item = LuaHelper.getTable(itemTables, key2);

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
                                            localeFiles.put(currentIniSection, new HashMap<String, String>());
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
            Logger.error("Could not load UnknownIcon.png " + e.getMessage());
        }
    }

    private static BufferedImage loadImage(String fileName)
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

            if (!Util.isNullOrWhitespace(fullPath))
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
            return ImageIO.read(file);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static void interpretLuaItem(String name, LuaTable item)
    {
        String iconString = LuaHelper.getString(item, "icon", true);
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
            float time = LuaHelper.getFloat(recipe, "energy_required", true, 0.5F);
            Map<Item, Float> ingredients = extractIngredientsFromLuaRecipe(recipe);
            Map<Item, Float> results = extractResultsFromLuaRecipe(recipe);

            Recipe newRecipe = new Recipe(name, time == 0.0F ? defaultRecipeTime : time, ingredients, results);

            newRecipe.category = LuaHelper.getString(recipe, "category", true, "crafting");

            String iconFile = LuaHelper.getString(recipe, "icon", true);
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

    private static void interpretAssemblingMachine(String name, LuaTable assemblers)
    {
        Logger.debug(name);
    }

    private static Map<Item, Float> extractResultsFromLuaRecipe(LuaTable recipe)
    {
        Map<Item, Float> results = new HashMap<>();
        if (recipe.get(LuaValue.valueOf("result")) != LuaValue.NIL)
        {
            String resultName = LuaHelper.getString(recipe, "result", false);
            float resultCount = LuaHelper.getFloat(recipe, "result_count", true);
            if (resultCount == 0F)
            { resultCount = 1F; }
            results.put(findOrCreateUnknownItem(resultName), resultCount);

        }
        else if (recipe.get(LuaValue.valueOf("results")) != LuaValue.NIL)
        {
            LuaTable resultsTable = (LuaTable) recipe.get(LuaValue.valueOf("results"));
            for (LuaValue key : resultsTable.keys())
            {
                LuaTable result = LuaHelper.getTable(resultsTable, key);
                Item newResult = LuaHelper.getValue(result, "name") != LuaValue.NIL ?
                                 findOrCreateUnknownItem(LuaHelper.getString(result, "name")) :
                                 findOrCreateUnknownItem(LuaHelper.getString(result, 1));
                float amount = LuaHelper.getValue(result, "amount") != LuaValue.NIL ?
                               LuaHelper.getFloat(result, "amount") :
                               LuaHelper.getFloat(result, 2);
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
        LuaTable ingredientsTable = LuaHelper.getTable(recipe, "ingredients");
        for (LuaValue key : ingredientsTable.keys())
        {
            LuaTable ingredientTable = LuaHelper.getTable(ingredientsTable, key);

            String name = LuaHelper.getValue(ingredientTable, "name") != LuaValue.NIL ?
                   LuaHelper.getString(ingredientTable, "name") :
                   ingredientTable.get(1).checkjstring();
            float amount = LuaHelper.getValue(ingredientTable, "amount") != LuaValue.NIL ?
                     LuaHelper.getValue(ingredientTable, "amount").tofloat() :
                     ingredientTable.get(2).tofloat();

            Item ingredient = findOrCreateUnknownItem(name);

            if (!ingredients.containsKey(ingredient))
                ingredients.put(ingredient, amount);
            else
            { ingredients.put(ingredient, ingredients.get(ingredient) + amount); }
        }
        return ingredients;
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
                writer.println("  Name: " + item.getName());
                writer.println("  Missing Icon: " + item.isMissingIcon);
                for (Recipe recipe : item.getRecipes())
                {
                    writer.println("    Recipe: " + recipe.getName());
                }
                writer.println("");
            }
            writer.println("Recipes:");
            for (String key : recipes.keySet())
            {
                Recipe recipe = recipes.get(key);
                writer.println("  Name: " + recipe.getName());
                writer.println("  Time: " + recipe.time);
                writer.println("  Category: " + recipe.category);
                writer.println("  Missing Recipe: " + recipe.isMissingRecipe);
                writer.println("  Ingredients: ");
                for (Item item : recipe.getIngredients().keySet())
                {
                    writer.println("    Item: " + item.getName() + " x" + recipe.getIngredients().get(item));
                }
                writer.println("  Results: ");
                for (Item item : recipe.getResults().keySet())
                {
                    writer.println("    Item: " + item.getName() + " x" + recipe.getResults().get(item));
                }
                writer.println(" ");
            }
            writer.println("Default Recipe Time: " + defaultRecipeTime);
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
        }
    }
}
