package mimer29or40.foremanfx.gui.controller;

import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class ControllerBase implements Initializable
{
    public abstract void init();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        if (Platform.isFxApplicationThread())
        {
            init();
        }
        else
        {
            Platform.runLater(this::init);
        }
    }
}
