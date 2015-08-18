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

    private Stage mainStage;

    public static Settings settings;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        settings = new Settings();

        this.mainStage = primaryStage;
        try
        {
            FXMLLoader loader = new FXMLLoader();
//            loader.setResources(ResourceBundle.getBundle("lang.bundle", new Locale("en", "US")));
            Parent root = loader.load(getClass().getResource(GuiFiles.MAIN).openStream());
            primaryStage.setTitle("Foreman FX");
            primaryStage.setScene(new Scene(root, 1170, 440));
            primaryStage.setMinWidth(1170);
            primaryStage.setMinHeight(440);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
