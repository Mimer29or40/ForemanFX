package mimer29or40.foremanfx;

import mimer29or40.foremanfx.util.Logger;

import java.io.*;
import java.util.Properties;

public class Settings
{
    private Properties prop;
    private File       file;
    private String     name;

    public Settings(String name)
    {
        this.name = name;
        prop = new Properties();
        file = new File(".", name);
        createFileIfNeeded();
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
            Logger.error("Error occurred while loading config: " + name + " " + e.getMessage());
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
            Logger.error("Error occurred while saving config: " + name + " " + e.getMessage());
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

    private void createFileIfNeeded()
    {
        try
        {
            if (!file.exists())
            {
                file.createNewFile();

                InputStream input = ForemanFX.class.getResourceAsStream("/config/" + name);
                OutputStream output = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0)
                { output.write(buf, 0, bytesRead); }

                input.close();
                output.close();
            }
        }
        catch (IOException e)
        {
            Logger.error("Could not create config: " + name + " " + e.getMessage());
        }
    }
}
