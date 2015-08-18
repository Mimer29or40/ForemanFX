package mimer29or40.foremanfx.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    private ObservableList<String> rateSelectList = FXCollections.observableArrayList();

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
    private Button buttonModDir;
    @FXML
    private Button buttonReload;

    @FXML
    private Label              languageHeader;
    @FXML
    private ComboBox<Language> languageSelect;
    private ObservableList<Language> languageSelectList = FXCollections.observableArrayList();

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
        });
        buttonRate.setOnAction((event) -> {
            settings.setProp("productionType", "rate");
        });
        setupRateSelect();
        rateSelect.setOnAction((event) -> {
            settings.setProp("rateType", rateSelect.getSelectionModel().getSelectedItem());
        });

        checkDisplayAssembler.setOnAction((event) -> {
            settings.setProp("displayAssemblers", checkDisplayAssembler.isSelected());
        });
        checkOneAssembler.setOnAction((event) -> {
            settings.setProp("oneAssembler", checkOneAssembler.isSelected());
        });
        checkDisplayMiner.setOnAction((event) -> {
            settings.setProp("displayMiner", checkDisplayMiner.isSelected());
        });

        buttonFactorioDir.setOnAction((event) -> {
            File file = Util.directoryChooser("Select Factorio Directory");
            if (file != null)
            { settings.setProp("factorioDir", file.getPath()); }
        });
        buttonModDir.setOnAction((event) -> {
            File file = Util.directoryChooser("Select Mod Directory");
            if (file != null)
            { settings.setProp("modDir", file.getPath()); }
        });

        // TODO setup language dropdown
//        setupLanguageSelect();
//        languageSelect.setOnAction((event) -> {
//            settings.setProp("language", languageSelect.getSelectionModel().getSelectedItem().getNameFull()); });

        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filterValue = newValue;
        });
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
        { buttonRate.setSelected(true); }
        else
        { buttonFixed.setSelected(true); }
        rateSelect.setValue(settings.getProp("rateType"));

        checkDisplayAssembler.setSelected(Boolean.valueOf(settings.getProp("displayAssemblers")));
        checkOneAssembler.setSelected(Boolean.valueOf(settings.getProp("oneAssembler")));
        checkDisplayMiner.setSelected(Boolean.valueOf(settings.getProp("displayMiner")));

        // TODO setup language dropdown
//        String lang[] = settings.getProp("language").split(",");
//        languageSelect.setValue();
    }

    private void setupRateSelect()
    {
        rateSelectList.add("minute");
        rateSelectList.add("second");

        rateSelect.setItems(rateSelectList);

        // Define rendering of the list of values in ComboBox drop down.
        rateSelect.setCellFactory((comboBox) -> {
            return new ListCell<String>()
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
            };
        });
    }

    // TODO setup language dropdown
//    private void setupLanguageSelect()
//    {
//        languageSelectList.add(new Language("en", "English"));
//
//        languageSelect.setItems(languageSelectList);
//
//        // Define rendering of the list of values in ComboBox drop down.
//        languageSelect.setCellFactory((comboBox) -> {
//            return new ListCell<Language>()
//            {
//                @Override
//                protected void updateItem(Language lang, boolean empty)
//                {
//                    super.updateItem(lang, empty);
//
//                    if (lang == null || empty)
//                    {
//                        setText(null);
//                    }
//                    else
//                    {
//                        setText(lang.getName());
//                    }
//                }
//            };
//        });
//
//        languageSelect.setConverter(new StringConverter<Language>() {
//            @Override
//            public String toString(Language lang) {
//                if (lang == null) {
//                    return null;
//                } else {
//                    return lang.getName();
//                }
//            }
//
//            @Override
//            public Language fromString(String personString) {
//                return null; // No conversion fromString needed.
//            }
//        });
//    }
}
