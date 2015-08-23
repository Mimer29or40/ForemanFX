package mimer29or40.foremanfx;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Data
{
    /* Format
    {
        "ProductionType": 1,
        "RateType": 1,
        "OneAssemblerPerRecipe": false,
        "Nodes":
        [
            {
                "NodeType": "Consumer",
                "RecipeName": "heavy-oil",
                "RateType": 0,
                "ActualRate": 0.0
            },
            {
                "NodeType": "Supplier",
                "RecipeName": "iron-ore",
                "RateType": 0,
                "ActualRate": 0.0
            },
            {
                "NodeType": "Recipe",
                "RecipeName": "copper-plate",
                "RateType": 0,
                "ActualRate": 0.0
            }
        ],
        "NodeLinks":
        [
            {
                "Supplier": 2,
                "Consumer": 0,
                "Item": "smart-chest"
            },
            {
                "Supplier": 3,
                "Consumer": 0,
                "Item": "advanced-circuit"
            },
            {
                "Supplier": 4,
                "Consumer": 2,
                "Item": "steel-chest"
            }
        ],
        "EnabledAssemblers";
        [
            "assembling-machine-1",
            "assembling-machine-2",
            "assembling-machine-3",
            "chemical-plant",
            "oil-refinery",
            "stone-furnace",
            "steel-furnace",
            "electric-furnace"
        ],
        "EnabledMiners":
        [
            "basic-mining-drill",
            "burner-mining-drill",
            "pumpjack"
        ],
        "EnabledModules":
        [
            "speed-module",
            "speed-module-2",
            "speed-module-3"
        ],
        "EnabledMods":
        [
            "scenario-pack",
            "core",
            "base"
        ],
        "ElementLocations":
        [
            "707,490",
            "617,200",
            "733,780"
        ]
    }
     */
    public static void save()
    {
        FileChooser save = new FileChooser();
        save.setTitle("Save Flowchart");
        save.setInitialFileName("Flowchart.json");
        File saveFile = save.showSaveDialog(null);

        JSONObject json = new JSONObject();
        json.put("ProductionType", ForemanFX.settings.getProp("productionType"));
        json.put("RateType", ForemanFX.settings.getProp("rateType"));
        json.put("OneAssemblerPerRecipe", ForemanFX.settings.getProp("oneAssembler"));
        JSONArray nodes = new JSONArray();
//        for (ProductionNode node : )
//        nodes.add(index,element);
        json.put("Nodes", nodes);
        JSONArray enabledAssemblers = new JSONArray();
        json.put("EnabledAssemblers", enabledAssemblers);
        JSONArray enabledMiners = new JSONArray();
        json.put("EnabledMiners", enabledMiners);
        JSONArray enabledModules = new JSONArray();
        json.put("EnabledModules", enabledModules);
        JSONArray enabledMods = new JSONArray();
        json.put("EnabledMods", enabledMods);
        JSONArray elementLocations = new JSONArray();
        json.put("ElementLocations", elementLocations);

        try
        {
            StringWriter jsonWriter = new StringWriter();
            json.writeJSONString(jsonWriter);

            if (!saveFile.exists())
            { saveFile.createNewFile(); }

            PrintWriter writer = new PrintWriter(saveFile);

            writer.println(jsonWriter.toString());

            writer.close();
        }
        catch (IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Could not Save Flowchart!");
            alert.showAndWait();
        }
    }

    public static String load()
    {
//        fileChooser.getExtensionFilters().addAll(
//                new ExtensionFilter("Text Files", "*.txt"),
//                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
//                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
//                new ExtensionFilter("All Files", "*.*"));
        return null;
    }
}
