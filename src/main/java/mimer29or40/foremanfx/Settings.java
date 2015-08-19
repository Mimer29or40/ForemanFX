package mimer29or40.foremanfx;

import mimer29or40.foremanfx.util.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings
{
    private Properties prop;
    private File       file;

    public Settings(String config)
    {
        prop = new Properties();
        file = new File(ForemanFX.class.getResource("/config/" + config).getFile());
        load();
    }

    private void load()
    {
        try
        {
            prop.load(new FileReader(file));
        }
        catch (Exception e)
        {
            Logger.error("Error occurred while loading config file: '" + file.getName() + "'");
        }
    }

    private void save()
    {
        try
        {
            prop.store(new FileWriter(file), "DO NOT EDIT THIS HERE");
        }
        catch (IOException e)
        {
            Logger.error("Error occurred while saving config file: '" + file.getName() + "'");
        }
    }

    public void setProp(String key, Object value)
    {
        prop.setProperty(key, value.toString());
        save();
    }

    public String getProp(String key)
    {
        return prop.getProperty(key);
    }
}
