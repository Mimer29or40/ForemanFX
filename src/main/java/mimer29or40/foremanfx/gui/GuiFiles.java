package mimer29or40.foremanfx.gui;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class GuiFiles
{
    public static final String MAIN = "/fxml/main.fxml";

    private static final Set<String> values = new LinkedHashSet<>();

    static
    {
        values.add(MAIN);
    }

    public static Set<String> getValues()
    {
        return Collections.unmodifiableSet(values);
    }
}
