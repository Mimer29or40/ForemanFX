package mimer29or40.foremanfx.util;

import mimer29or40.foremanfx.model.MissingPrototypeValueException;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class LuaUtil
{
    public static String getType(LuaValue value)
    {
        if (value.isstring())
        { return "string"; }
        else if (value.istable())
        { return "table"; }
        else if (value.isboolean())
        { return "boolean"; }
        else if (value.isfunction())
        { return "function"; }
        else if (value.isint())
        { return "int"; }
        else if (value.islong())
        { return "long"; }
        else if (value.isnumber())
        { return "number"; }
        else if (value.isnil())
        { return "nil"; }
        return "type not found";
    }

    public static void printDebugTable(LuaTable table, String prefix)
    {
        for (LuaValue key : table.keys())
        {
            LuaValue value = table.get(key);
            if (getType(value).equals("table"))
            {
                printDebugTable((LuaTable) value, prefix + " " + key.checkjstring());
            }
            else
            {
                Logger.debug(prefix + " " + key.checkjstring() + " " + table.get(key));
            }
        }
    }

    public static boolean isNull(LuaValue value)
    {
        return value == LuaValue.NIL;
    }

    public static boolean isNull(LuaTable table, LuaValue key)
    {
        return isNull(table.get(key));
    }

    public static boolean isNull(LuaTable table, String key)
    {
        return isNull(table.get(LuaValue.valueOf(key)));
    }

    public static boolean isNull(LuaTable table, int key)
    {
        return isNull(table.get(key));
    }

    public static LuaTable getTable(LuaValue value, LuaValue key, boolean canBeMissing)
    {
        LuaValue table = value.get(key);
        if (getType(table).equals("table"))
        { return (LuaTable) table; }
        if (canBeMissing)
        { return LuaTable.tableOf(); }
        Logger.error("LuaValue at key '%s' is not a table. Found %s", key.checkjstring(), getType(table));
        return LuaTable.tableOf();
    }

    public static LuaTable getTable(LuaValue value, String key, boolean canBeMissing)
    {
        return getTable(value, LuaValue.valueOf(key), canBeMissing);
    }

    public static LuaTable getTable(LuaValue value, LuaValue key)
    {
        return getTable(value, key, false);
    }

    public static LuaTable getTable(LuaValue value, String key)
    {
        return getTable(value, LuaValue.valueOf(key), false);
    }

    public static LuaValue getValue(LuaTable table, LuaValue key)
    {
        return table.get(key);
    }

    public static LuaValue getValue(LuaTable table, String key)
    {
        return getValue(table, LuaValue.valueOf(key));
    }

    public static float getFloat(LuaTable table, LuaValue key, boolean canBeMissing, float defaultValue)
    {
        LuaValue value = table.get(key);
        if (value == LuaValue.NIL)
        {
            if (canBeMissing)
            { return defaultValue; }
            throw new MissingPrototypeValueException(table, key.toString(), "Key is Missing");
        }
//        if (!getType(value).equals("number"))
//            throw new MissingPrototypeValueException(table, key.toString(), "Expected Float,
// got ('" + getType(value) + "')");
        return value.tofloat();
    }

    public static float getFloat(LuaTable table, String key, boolean canBeMissing, float defaultValue)
    {
        return getFloat(table, LuaValue.valueOf(key), canBeMissing, defaultValue);
    }

    public static float getFloat(LuaTable table, String key, boolean canBeMissing)
    {
        return getFloat(table, LuaValue.valueOf(key), canBeMissing, 0F);
    }

    public static float getFloat(LuaTable table, LuaValue key)
    {
        return getFloat(table, key, false, 0F);
    }

    public static float getFloat(LuaTable table, String key)
    {
        return getFloat(table, LuaValue.valueOf(key), false, 0F);
    }

    public static float getFloat(LuaTable table, int key)
    {
        return getFloat(table, LuaValue.valueOf(key), false, 0F);
    }

    public static int getInt(LuaTable table, LuaValue key, boolean canBeMissing, int defaultValue)
    {
        LuaValue value = table.get(key);
        if (value == LuaValue.NIL)
        {
            if (canBeMissing)
            { return defaultValue; }
            throw new MissingPrototypeValueException(table, key.toString(), "Key is Missing");
        }
//        if (!getType(value).equals("int"))
//        {
//            throw new MissingPrototypeValueException(table, key.toString(), "Expected Int, got ('" + getType(
//                    value) + "')");
//        }
        return value.toint();
    }

    public static int getInt(LuaTable table, String key, boolean canBeMissing, int defaultValue)
    {
        return getInt(table, LuaValue.valueOf(key), canBeMissing, defaultValue);
    }

    public static int getInt(LuaTable table, String key, boolean canBeMissing)
    {
        return getInt(table, key, canBeMissing, 0);
    }

    public static int getInt(LuaTable table, LuaValue key)
    {
        return getInt(table, key, false, 0);
    }

    public static int getInt(LuaTable table, String key)
    {
        return getInt(table, LuaValue.valueOf(key), false, 0);
    }

    public static int getInt(LuaTable table, int key)
    {
        return getInt(table, LuaValue.valueOf(key), false, 0);
    }

    public static String getString(LuaTable table, LuaValue key, boolean canBeMissing, String defaultValue)
    {
        LuaValue value = table.get(key);
        if (value == LuaValue.NIL)
        {
            if (canBeMissing)
            { return defaultValue; }
            throw new MissingPrototypeValueException(table, key.toString(), "Key is Missing");
        }
        if (!getType(value).equals("string"))
        {
            throw new MissingPrototypeValueException(table, key.toString(), "Expected String, got ('" + getType(
                    value) + "')");
        }
        return value.checkjstring();
    }

    public static String getString(LuaTable table, String key, boolean canBeMissing, String defaultValue)
    {
        return getString(table, LuaValue.valueOf(key), canBeMissing, defaultValue);
    }

    public static String getString(LuaTable table, String key, boolean canBeMissing)
    {
        return getString(table, LuaValue.valueOf(key), canBeMissing, "");
    }

    public static String getString(LuaTable table, LuaValue key)
    {
        return getString(table, key, false, "");
    }

    public static String getString(LuaTable table, String key)
    {
        return getString(table, LuaValue.valueOf(key), false, "");
    }

    public static String getString(LuaTable table, int key)
    {
        return getString(table, LuaValue.valueOf(key), false, "");
    }
}
