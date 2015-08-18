package mimer29or40.foremanfx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Settings
{
    private static final String CONFIG = "/config.properties";

    private static Properties prop;
    private static File       file;

    public Settings()
    {
        prop = new Properties();

        URL url = ForemanFX.class.getResource(CONFIG);
        file = new File(url.getFile());

        load();
    }

    private void load()
    {
        try
        {
            InputStream stream = ForemanFX.class.getResourceAsStream(CONFIG);
            prop.load(stream);
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void save()
    {
        try
        {
            FileWriter writer = new FileWriter(file);
            prop.store(writer, "Config");
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
