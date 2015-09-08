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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import mimer29or40.foremanfx.DataCache;
import mimer29or40.foremanfx.ForemanFX;
import mimer29or40.foremanfx.Settings;
import mimer29or40.foremanfx.gui.graph.ProductionGraph;
import mimer29or40.foremanfx.gui.graph.ProductionGraphViewer;
import mimer29or40.foremanfx.gui.graph.element.NodeElement;
import mimer29or40.foremanfx.gui.node.ConsumerNode;
import mimer29or40.foremanfx.model.Item;
import mimer29or40.foremanfx.model.Language;
import mimer29or40.foremanfx.util.FileUtil;

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

    private ProductionGraph       graph;
    private ProductionGraphViewer canvas;

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

        canvas = new ProductionGraphViewer();
        graph = new ProductionGraph();

        buttonFixed.setOnAction(event ->
                                {
                                    settings.setProp("productionType", "fixed");
                                    rateSelect.setDisable(true);
                                    checkDisplayAssembler.setDisable(true);
                                    checkDisplayMiner.setDisable((true));
                                });
        buttonRate.setOnAction(event ->
                               {
                                   settings.setProp("productionType", "rate");
                                   rateSelect.setDisable(false);
                                   checkDisplayAssembler.setDisable(false);
                                   checkDisplayMiner.setDisable((false));
                               });
        setupRateSelect();
        rateSelect.setOnAction(event -> settings.setProp("rateType", rateSelect.getSelectionModel().getSelectedItem()));

        buttonExport.setOnAction(event ->
                                 {
                                     WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
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

        checkDisplayAssembler.setOnAction(event -> settings.setProp("displayAssemblers", checkDisplayAssembler.isSelected()));
        checkOneAssembler.setOnAction(event -> settings.setProp("oneAssembler", checkOneAssembler.isSelected()));
        checkDisplayMiner.setOnAction(event -> settings.setProp("displayMiner", checkDisplayMiner.isSelected()));

        buttonFactorioDir.setOnAction(event ->
                                      {
                                          File file = FileUtil.directoryChooser("Select Factorio Directory");
                                          if (file != null)
                                          { settings.setProp("factorioDir", file.getPath()); }
                                      });
//        buttonOpenDir.setOnAction((event) -> TODO get this to work
//            {
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
        buttonReload.setOnAction(event -> DataCache.loadAllData(null));

        setupLanguageSelect();
        languageSelect.setOnAction(event ->
                                           settings.setProp("language",
                                                            languageSelect.getSelectionModel().getSelectedItem()
                                                                          .getLocalName()));

        filter.textProperty().addListener((observable, oldValue, newValue) ->
                                                  filteredList.setPredicate(item ->
                                                                            {
                                                                                if (newValue == null || newValue.isEmpty())
                                                                                { return true; }
                                                                                String filter = newValue.toLowerCase();
                                                                                return item.getLocalizedName().toLowerCase().contains(filter);
                                                                            }));
        loadItemList();

        buttonAddItem.setOnAction(event ->
                                  {
                                      Item selectedItem = itemSelector.getSelectionModel().selectedItemProperty().get();
                                      if (selectedItem != null)
                                      { createNode(selectedItem); }
                                  });

        // TODO Context Menus for selecting assemblers

        Item item = DataCache.items.get("speed-module-2");
//        Recipe recipe = DataCache.recipes.get("copper-cable");
//        Recipe recipe1 = DataCache.recipes.get("speed-module");
//        Recipe recipe2 = DataCache.recipes.get("speed-module-2");
//        Recipe recipe3 = DataCache.recipes.get("speed-module-3");

//        NodeElement element2 = new NodeElement(RecipeNode.create(recipe, graph), canvas);
//        NodeElement element5 = new NodeElement(RecipeNode.create(recipe1, graph), canvas);
//        NodeElement element6 = new NodeElement(RecipeNode.create(recipe2, graph), canvas);
//        NodeElement element7 = new NodeElement(RecipeNode.create(recipe3, graph), canvas);
//        NodeElement element3 = new NodeElement(SupplyNode.create(item, graph), canvas);
        NodeElement element4 = new NodeElement(ConsumerNode.create(item, graph), canvas);

//        DraggedLinkElement element8 = new DraggedLinkElement(canvas, element3, LinkType.Output, item);
//        element8.setX(50);
//        element8.setY(50);

        flowchart.setContent(canvas);
        canvas.drawElement();

        flowchart.addEventFilter(MouseEvent.MOUSE_PRESSED, canvas.canvasEventHandler.getOnMousePressedEventHandler());
        flowchart.addEventFilter(MouseEvent.MOUSE_DRAGGED, canvas.canvasEventHandler.getOnMouseDraggedEventHandler());
        flowchart.addEventFilter(ScrollEvent.ANY, canvas.canvasEventHandler.getOnScrollEventHandler());
//        flowchart.setOnMousePressed(canvas.canvasEventHandler.getOnMousePressedEventHandler());
//        flowchart.setOnMouseDragged(canvas.canvasEventHandler.getOnMouseDraggedEventHandler());
//        flowchart.setOnScroll(canvas.canvasEventHandler.getOnScrollEventHandler());
//        flowchart.addEventHandler(MouseEvent.MOUSE_PRESSED, canvas.canvasEventHandler.getOnMousePressedEventHandler());
//        flowchart.addEventHandler(MouseEvent.MOUSE_DRAGGED, canvas.canvasEventHandler.getOnMouseDraggedEventHandler());
//        flowchart.addEventHandler(ScrollEvent.ANY, canvas.canvasEventHandler.getOnScrollEventHandler());

    }

    private void createNode(Item selectedItem)
    {
        // TODO Menu goes here
        Item item = DataCache.items.get(selectedItem.getName());
//        final ContextMenu contextMenu = new ContextMenu();
//        contextMenu.setOnShowing(e -> System.out.println("showing"));
//        contextMenu.setOnShown(e -> System.out.println("shown"));
//
//        MenuItem item1 = new MenuItem("About");
//        item1.setOnAction(e -> System.out.println("About"));
//        MenuItem item2 = new MenuItem("Preferences");
//        item2.setOnAction(e -> System.out.println("Preferences"));
//        contextMenu.getItems().addAll(item1, item2);
//
//        final TextField textField = new TextField("Type Something");
//        textField.setContextMenu(contextMenu);
//        textField.setOnAction(e -> contextMenu.show(textField, Side.BOTTOM, 0, 0));
//        canvas.getChildren().add(textField);
        NodeElement element = new NodeElement(ConsumerNode.create(item, graph), canvas);
        element.setupElements();
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
            checkDisplayAssembler.setDisable(false);
            checkDisplayMiner.setDisable((false));
        }
        else
        {
            buttonFixed.setSelected(true);
            rateSelect.setDisable(true);
            checkDisplayAssembler.setDisable(true);
            checkDisplayMiner.setDisable((true));
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

        languageSelectList.addAll(DataCache.languages.keySet().stream().map(DataCache.languages::get).collect(Collectors.toList()));

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
                    setText(lang.getLocalName());
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
                    return lang.getLocalName();
                }
            }

            @Override
            public Language fromString(String langString)
            {
                return null;
            }
        });
    }
}
