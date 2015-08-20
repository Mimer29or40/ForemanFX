package mimer29or40.foremanfx.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.ForemanFX;
import mimer29or40.foremanfx.Language;
import mimer29or40.foremanfx.Settings;
import mimer29or40.foremanfx.util.Util;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable
{
    private ResourceBundle bundle;

    private Settings settings    = ForemanFX.settings;
    private Settings userData    = ForemanFX.userData;
    private String   filterValue = "";

    @FXML
    private VBox main;

    @FXML
    private Label            productionHeader;
    @FXML
    private RadioButton      buttonFixed;
    @FXML
    private RadioButton      buttonRate;
    @FXML
    private ComboBox<String> rateSelect;

    @FXML
    private Button buttonAutoComplete;
    @FXML
    private Button buttonClear;
    @FXML
    private Button buttonEnableDisable;

    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonLoad;
    @FXML
    private Button buttonExport;

    @FXML
    private Label    assemblerHeader;
    @FXML
    private CheckBox checkDisplayAssembler;
    @FXML
    private CheckBox checkOneAssembler;
    @FXML
    private CheckBox checkDisplayMiner;

    @FXML
    private Button buttonFactorioDir;
    @FXML
    private Button buttonOpenDir;
    @FXML
    private Button buttonReload;

    @FXML
    private Label              languageHeader;
    @FXML
    private ComboBox<Language> languageSelect;

    @FXML
    private TextField filter;
    @FXML
    private ListView  itemSelector;
    @FXML
    private Button    buttonAddItem;

    @FXML
    private ScrollPane flowchart;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        bundle = resources;

        loadConfigValues();

        buttonFixed.setOnAction((event) -> {
            settings.setProp("productionType", "fixed");
            rateSelect.setDisable(true);
        });
        buttonRate.setOnAction((event) -> {
            settings.setProp("productionType", "rate");
            rateSelect.setDisable(false);
        });
        setupRateSelect();
        rateSelect.setOnAction((event) -> settings.setProp("rateType",
                                                           rateSelect.getSelectionModel().getSelectedItem()));

        checkDisplayAssembler.setOnAction((event) -> settings.setProp("displayAssemblers",
                                                                      checkDisplayAssembler.isSelected()));
        checkOneAssembler.setOnAction((event) -> settings.setProp("oneAssembler", checkOneAssembler.isSelected()));
        checkDisplayMiner.setOnAction((event) -> settings.setProp("displayMiner", checkDisplayMiner.isSelected()));

        buttonFactorioDir.setOnAction((event) -> {
            File file = Util.directoryChooser("Select Factorio Directory");
            if (file != null)
            { settings.setProp("factorioDir", file.getPath()); }
        });
//        buttonOpenDir.setOnAction((event) -> { TODO get this to work
//            try
//            {
//                Desktop.getDesktop().open(new File(settings.getProp("programDir")));
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//            File file = Util.directoryChooser("Select Mod Directory");
//            if (file != null)
//            { settings.setProp("modDir", file.getPath()); }
//        });

        setupLanguageSelect();
        languageSelect.setOnAction((event) -> settings.setProp("language",
                                                               languageSelect.getSelectionModel().getSelectedItem()
                                                                             .getName()));

        filter.textProperty().addListener((observable, oldValue, newValue) -> filterValue = newValue);
    }

//    private void loadLanguage(ResourceBundle bundle)
//    {
//        if (bundle == null)
//            bundle = ResourceBundle.getBundle("lang.bundle", new Locale("en","US"));
//
//        productionHeader.setText(bundle.getString("setProduction"));
//        buttonFixed.setText(bundle.getString("fixedAmount"));
//        buttonRate.setText(bundle.getString("rate"));
//    }

    private void loadConfigValues()
    {
        if (settings.getProp("productionType").equals("rate"))
        {
            buttonRate.setSelected(true);
            rateSelect.setDisable(false);
        }
        else
        {
            buttonFixed.setSelected(true);
            rateSelect.setDisable(true);
        }
        rateSelect.setValue(settings.getProp("rateType"));

        checkDisplayAssembler.setSelected(Boolean.valueOf(settings.getProp("displayAssemblers")));
        checkOneAssembler.setSelected(Boolean.valueOf(settings.getProp("oneAssembler")));
        checkDisplayMiner.setSelected(Boolean.valueOf(settings.getProp("displayMiner")));

        languageSelect.setValue(DataCache.languages.get(settings.getProp("language")));
    }

    private void setupRateSelect()
    {
        ObservableList<String> rateSelectList = FXCollections.observableArrayList();

        rateSelectList.add("minute");
        rateSelectList.add("second");

        rateSelect.setItems(rateSelectList);

        // Define rendering of the list of values in ComboBox drop down.
        rateSelect.setCellFactory((comboBox) -> new ListCell<String>()
        {
            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
                    setText(null);
                }
                else
                {
                    setText(item);
                }
            }
        });
    }

    private void setupLanguageSelect()
    {
        ObservableList<Language> languageSelectList = FXCollections.observableArrayList();

        for (String key : DataCache.languages.keySet())
        { languageSelectList.add(DataCache.languages.get(key)); }

        languageSelect.setItems(languageSelectList);

        // Define rendering of the list of values in ComboBox drop down.
        languageSelect.setCellFactory((comboBox) -> new ListCell<Language>()
        {
            @Override
            protected void updateItem(Language lang, boolean empty)
            {
                super.updateItem(lang, empty);

                if (lang == null || empty)
                {
                    setText(null);
                }
                else
                {
                    setText(lang.getName());
                }
            }
        });

        languageSelect.setConverter(new StringConverter<Language>()
        {
            @Override
            public String toString(Language lang)
            {
                if (lang == null)
                {
                    return null;
                }
                else
                {
                    return lang.getName();
                }
            }

            @Override
            public Language fromString(String langString)
            {
                return null;
//                return new Language(langString);
            }
        });
    }
}
