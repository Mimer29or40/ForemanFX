package mimer29or40.foremanfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mimer29or40.foremanfx.gui.GuiFiles;

public class ForemanFX extends Application
{
    private Stage factorioDir;
    private Stage modDir;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource(GuiFiles.MAIN));
            primaryStage.setTitle("Foreman FX");
            primaryStage.setScene(new Scene(root, 1170, 440));
            primaryStage.setMinWidth(1170);
            primaryStage.setMinHeight(440);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
