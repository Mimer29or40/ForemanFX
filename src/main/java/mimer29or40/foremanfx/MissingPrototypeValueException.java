package mimer29or40.foremanfx;

import org.luaj.vm2.LuaTable;

public class MissingPrototypeValueException extends RuntimeException
{
    public LuaTable table;
    public String   key;

    public MissingPrototypeValueException(LuaTable table, String key, String message)
    {
        super(message);
        this.table = table;
        this.key = key;
    }

    public MissingPrototypeValueException(LuaTable table, String key)
    {
        this(table, key, "");
    }
}
