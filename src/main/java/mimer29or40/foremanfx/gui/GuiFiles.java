package mimer29or40.foremanfx.gui;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class GuiFiles
{
    public static final String MAIN = "/fxml/main.fxml";

    public static final String MAIN_CSS = "/css/main.css";

    private static final Set<String> values     = new LinkedHashSet<>();
    private static final Set<String> values_css = new LinkedHashSet<>();

    static
    {
        values.add(MAIN);

        values_css.add(MAIN_CSS);
    }

    public static Set<String> getValues()
    {
        return Collections.unmodifiableSet(values);
    }

    public static Set<String> getValuesCss()
    {
        return Collections.unmodifiableSet(values_css);
    }
}
