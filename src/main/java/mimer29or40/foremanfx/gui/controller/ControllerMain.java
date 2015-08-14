package mimer29or40.foremanfx.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable/*extends Controller*/
{
    private ResourceBundle bundle;

    @FXML
    private Button buttonClear;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        bundle = resources;
    }
}
