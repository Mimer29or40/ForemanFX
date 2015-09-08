package mimer29or40.foremanfx.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

public class JsonHelper
{
    private static final JSONParser parser = new JSONParser();

    public static JSONObject parse(String json)
    {
        try
        {
            return (JSONObject) parser.parse(json);
        }
        catch (ParseException e)
        {
            System.out.println("JSONObject could not be parsed");
        }
        return null;
    }

    public static JSONObject parse(List<String> json)
    {
        return parse(ListUtil.listToString(json));
    }
}
