package mimer29or40.foremanfx.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.ForemanFX;
import mimer29or40.foremanfx.Settings;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.Language;
import mimer29or40.foremanfx.util.Util;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerMain extends ControllerBase
{
    private Settings settings = ForemanFX.settings;
    private Settings userData = ForemanFX.userData;

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
    private TextField            filter;
    @FXML
    private ListView<Item>       itemSelector;
    private ObservableList<Item> unfilteredItemList;
    private FilteredList<Item>   filteredList;

    @FXML
    private Button buttonAddItem;

    @FXML
    private ScrollPane flowchart;

    public void init()
    {
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

        buttonExport.setOnAction((event) ->
                                 {
                                     WritableImage image = flowchart.snapshot(new SnapshotParameters(), null);
                                     File file = new File("./output.png");
                                     try
                                     {
                                         ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                                     }
                                     catch (IOException e)
                                     {
                                         Alert alert = new Alert(Alert.AlertType.WARNING);
                                         alert.setTitle("Warning");
                                         alert.setHeaderText(null);
                                         alert.setContentText("Could not Export the workspace!");
                                         alert.showAndWait();
                                     }
                                 });

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
        buttonReload.setOnAction((event) -> DataCache.loadAllData(null));

        setupLanguageSelect();
        languageSelect.setOnAction((event) -> settings.setProp("language",
                                                               languageSelect.getSelectionModel().getSelectedItem()
                                                                             .getName()));

        filter.textProperty().addListener((observable, oldValue, newValue) ->
                                                  filteredList.setPredicate(item ->
                                                                            {
                                                                                if (newValue == null || newValue
                                                                                        .isEmpty())
                                                                                { return true; }
                                                                                String filter = newValue.toLowerCase();
                                                                                return item.getLocalizedName()
                                                                                           .toLowerCase().contains(
                                                                                                filter);
                                                                            }));
        loadItemList();
    }

    private void loadLanguage(ResourceBundle bundle)
    {
        if (bundle == null)
        { bundle = ResourceBundle.getBundle("lang.bundle", new Locale("en", "US")); }

        productionHeader.setText(bundle.getString("setProduction"));
        buttonFixed.setText(bundle.getString("fixedAmount"));
        buttonRate.setText(bundle.getString("rate"));
    }

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

    private void loadItemList()
    {
        List<Item> itemList = DataCache.items.keySet().stream().map(DataCache.items::get).collect(Collectors.toList());

        Collections.sort(itemList, (item1, item2) -> item1.getLocalizedName().compareTo(item2.getLocalizedName()));

        unfilteredItemList = FXCollections.observableList(itemList);

        filteredList = new FilteredList<>(unfilteredItemList, p -> true);

        itemSelector.setItems(filteredList);

        itemSelector.setCellFactory((list) -> new ListCell<Item>()
        {
            @Override
            protected void updateItem(Item item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
                    setGraphic(null);
                    setText(null);
                }
                else
                {
                    setGraphic(new ImageView(item.getIcon()));
                    setText(item.getLocalizedName());
                }
            }
        });

//        itemSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
//                        {
//                            if (newValue != null)
//                                System.out.println("ListView Selection Changed (selected: " + newValue.getName() +
// ")");
//                        });
    }

    private void setupRateSelect()
    {
        rateSelect.setItems(FXCollections.observableArrayList("minute", "second"));

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

        languageSelectList.addAll(DataCache.languages.keySet().stream().map(DataCache.languages::get)
                                                     .collect(Collectors.toList()));

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
