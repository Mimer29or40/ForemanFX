package mimer29or40.foremanfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mimer29or40.foremanfx.gui.GuiFiles;
import mimer29or40.foremanfx.util.Util;

import java.io.File;

public class ForemanFX extends Application
{
    public static Stage mainStage;

    public static Settings settings;
    public static Settings userData;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        settings = new Settings("config.properties");
        userData = new Settings("userData.properties");

        while (settings.getProp("factorioDir").isEmpty())
        {
            File file = Util.directoryChooser("Select Factorio Directory");
            if (file != null)
            { settings.setProp("factorioDir", file.getPath()); }
        }

        if (settings.getProp("modDir").isEmpty())
        {
            String modDir = settings.getProp("factorioDir") + "/mods";
            settings.setProp("modDir", modDir);
        }
        settings.setProp("programDir", new File("").getAbsolutePath());

        DataCache.loadAllData(null);


        this.mainStage = primaryStage;

        try
        {
            FXMLLoader loader = new FXMLLoader();
//            loader.setResources(ResourceBundle.getBundle("lang.bundle", new Locale("en", "US")));
            Parent root = loader.load(getClass().getResource(GuiFiles.MAIN).openStream());
            primaryStage.setTitle("Foreman FX");
            Scene scene = new Scene(root, 1170, 440);
            scene.getStylesheets().add("css/styleSheet.css");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1170);
            primaryStage.setMinHeight(440);
            primaryStage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void launchMain()
    {

    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
