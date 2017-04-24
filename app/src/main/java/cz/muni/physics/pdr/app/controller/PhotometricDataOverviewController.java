package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.javafx.chart.PhotometricChartDataFactory;
import cz.muni.physics.pdr.app.javafx.control.TitledTextFieldBox;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.model.converters.PhotometricDataModelConverter;
import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 01/04/16
 */
@Component
@Scope("prototype")
public class PhotometricDataOverviewController extends StageController {

    private static final Logger logger = LogManager.getLogger(PhotometricDataOverviewController.class);


    @Autowired
    private Screens app;
    @Autowired
    private Executor executor;
    @Autowired
    private Preferences preferences;
    @Value("${last.save.path}")
    private String lastSavePath;
    @Autowired
    private File pluginsDir;

    @FXML
    private MenuItem exportOneFileMenuItem;
    @FXML
    private TitledTextFieldBox epochTextField;
    @FXML
    private TitledTextFieldBox periodTextField;
    @FXML
    private ResourceBundle resources;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem saveAllMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private Menu saveMenu;
    @FXML
    private TabPane tabPane;
    @FXML
    private ScatterChart<Number, Number> chart;
    @FXML
    private ProgressIndicator chartProgressIndicator;
    @FXML
    private Menu showOneMenu;
    @FXML
    private Menu openOutputFolderMenu;
    @FXML
    private Menu openOriginalFileMenu;

    private Map<StarSurveyModel, List<PhotometricDataModel>> data;
    private StellarObjectModel stellarObject;

    @FXML
    private void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);
        saveAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
        chart.setCacheHint(CacheHint.SPEED);


    }

    @FXML
    private void handleExportInOneFileMenuItem() {
        String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
        File file = FXMLUtils.showSaveFileChooser(resources.getString("choose.output.file"),
                lastSavePath,
                coords,
                stage,
                new FileChooser.ExtensionFilter("Csv file (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Ascii table (*.txt)", "*.txt"));
        if (file != null) {
            updateLastSavePath(file.getParent());
            PhotometricDataModelConverter converter = PhotometricDataModelConverter.get(file.getName());
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    try (OutputStream out = new FileOutputStream(file)) {
                        List<PhotometricDataModel> models =
                                data.values().stream().flatMap(List::stream).collect(Collectors.toList());
                        write(out, models, converter);
                    } catch (IOException e) {
                        logger.error(e);
                        errorAlert();
                    }
                    return null;
                }
            };
            Dialog dialog = FXMLUtils.getProgressDialog(stage, task);
            executor.execute(task);
            dialog.showAndWait();
        }
    }

    @FXML
    private void handleSaveAllMenuItem() {
        String entryFormat = ".csv";
        String[] choices = {"CSV file .csv", "Ascii table .txt"};
        ChoiceDialog<String> dialog = FXMLUtils.showOptionDialog(stage, Arrays.asList(choices), "Choose output format", "Choose output format",
                resources.getString("output.format"));
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            entryFormat = result.get();

            String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
            File zip = FXMLUtils.showSaveFileChooser(resources.getString("choose.output.file"),
                    lastSavePath,
                    "pdr-export-" + entryFormat.split(" ")[0] + "-" + coords,
                    stage,
                    new FileChooser.ExtensionFilter("Zip file (*.zip)", "*.zip"));
            if (zip != null) {
                updateLastSavePath(zip.getParent());
                toZip(zip, coords + entryFormat);
            }
        }
    }

    private void updateLastSavePath(String newPath) {
        lastSavePath = newPath;
        preferences.put("last.save.path", lastSavePath);
    }

    @FXML
    private void handleCloseMenuItem() {
        stage.close();
    }

    public void setData(Map<StarSurveyModel, List<PhotometricDataModel>> data) {
        Map<StarSurveyModel, List<PhotometricDataModel>> result = new HashMap<>();
        for (Map.Entry<StarSurveyModel, List<PhotometricDataModel>> entry : data.entrySet()) {
            Map<String, List<PhotometricDataModel>> byIds = entry.getValue().stream().collect(Collectors.groupingBy(PhotometricDataModel::getId));
            MenuItem item = getOpenOutputFolderMenuItem(entry.getKey().getPlugin().getName());
            if (item != null)
                openOutputFolderMenu.getItems().add(item);
            byIds.forEach((s, models) -> {
                if (!models.isEmpty()) {
                    String name = entry.getKey().getPlugin().getName() + (s.isEmpty() ? "" : ("-" + s));
                    StarSurveyModel survey = new StarSurveyModel(name, entry.getKey().getPlugin());
                    result.put(survey, models);
                    tabPane.getTabs().add(getTab(survey, models));
                    saveMenu.getItems().add(getSaveMenuItem(survey, models));
                    showOneMenu.getItems().add(getShowOneMenuItem(survey));
                    MenuItem fileMenuItem = getOpenOriginalFileMenuItem(survey.getPlugin().getName(), s);
                    if (fileMenuItem != null)
                        openOriginalFileMenu.getItems().add(fileMenuItem);
                }
            });
        }
        this.data = result;
        chart.getData().clear();
        handleRedraw();
    }

    private void showOneInChart(StarSurveyModel survey) {
        chart.getYAxis().setAutoRanging(false);
        chart.getData().stream().forEach(
                s -> {
                    double max = -Double.MAX_VALUE;
                    double min = Double.MAX_VALUE;
                    if (s.getName().equals(survey.getName())) {
                        for (XYChart.Data<Number, Number> d : s.getData()) {
                            double y = d.getYValue().doubleValue();
                            if (y > max) max = y;
                            if (y < min) min = y;
                            d.getNode().setVisible(true);
                        }
                        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
                        yAxis.setLowerBound(min);
                        yAxis.setUpperBound(max);
                    } else {
                        s.getData().forEach(d -> d.getNode().setVisible(false));
                    }
                }
        );
    }

    @FXML
    private void showAllInChart() {
        chart.getYAxis().setAutoRanging(true);
        chart.getData().stream().forEach(s -> s.getData().forEach(
                d -> d.getNode().setVisible(true)
        ));
    }

    private void drawChart(Map<StarSurveyModel, List<PhotometricDataModel>> data, boolean forceAll) {
        chart.getData().clear();
        PhotometricChartDataFactory dataFactory = PhotometricChartDataFactory.getInstance(stellarObject);
        dataFactory.setUpChart(chart, resources);
        chartProgressIndicator.setVisible(true);
        chartProgressIndicator.setDisable(false);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (Map.Entry<StarSurveyModel, List<PhotometricDataModel>> entry : data.entrySet()) {
                    XYChart.Series<Number, Number> series = dataFactory.getSeries(entry.getKey().getName(), entry.getValue(), forceAll);
                    Platform.runLater(() -> chart.getData().add(series));
                }
                return null;
            }

            @Override
            protected void succeeded() {
                chartProgressIndicator.setDisable(true);
                chartProgressIndicator.setVisible(false);
                chart.setDisable(false);
                chart.setOpacity(1);
            }
        };
        executor.execute(task);
    }

    @FXML
    private void handleRedraw() {
        drawChart(data, false);
    }

    @FXML
    private void handleFullRedraw() {
        drawChart(data, true);
    }

    private Tab getTab(StarSurveyModel model, List<PhotometricDataModel> models) {
        TableView<PhotometricDataModel> table;
        try {
            table = FXMLLoader.load(this.getClass().getResource("/view/PhotometricDataTable.fxml"), resources);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .fxml resource", e);
        }
        table.getItems().addAll(models);
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setContent(table);
        tab.setGraphic(new Label(String.format("%s (%d)", model.getName(), models.size())));
        tab.getGraphic().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        showOneInChart(model);
                    }
                }
            }
        });

        return tab;
    }

    private MenuItem getSaveMenuItem(StarSurveyModel model, List<PhotometricDataModel> models) {
        MenuItem menuItem = new MenuItem(model.getName());
        menuItem.setOnAction(event -> {
            toFile(model.getPlugin().getName(), models);
        });
        return menuItem;
    }

    private MenuItem getShowOneMenuItem(StarSurveyModel survey) {
        MenuItem menuItem = new MenuItem(survey.getName());
        menuItem.setOnAction(event -> {
            showOneInChart(survey);
        });
        return menuItem;
    }

    private void toZip(final File zip, String entrySuffix) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
                    PhotometricDataModelConverter converter = PhotometricDataModelConverter.get(entrySuffix);
                    for (Map.Entry<StarSurveyModel, List<PhotometricDataModel>> entry : data.entrySet()) {
                        ZipEntry e = new ZipEntry(entry.getKey().getName() + converter.getExtension());
                        out.putNextEntry(e);
                        write(out, entry.getValue(), converter);
                        out.closeEntry();
                    }
                } catch (IOException e) {
                    logger.error(e);
                    errorAlert();
                }
                return null;
            }
        };
        Dialog dialog = FXMLUtils.getProgressDialog(stage, task);
        executor.execute(task);
        dialog.showAndWait();
    }

    private void toFile(String name, List<PhotometricDataModel> models) {
        String coords = stellarObject.getRightAscension() + " " + stellarObject.getDeclination();
        File file = FXMLUtils.showSaveFileChooser(resources.getString("choose.output.file"),
                lastSavePath,
                name + "-" + coords,
                stage,
                new FileChooser.ExtensionFilter("Csv file (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Ascii table (*.txt)", "*.txt"));
        if (file != null) {
            updateLastSavePath(file.getParent());
            PhotometricDataModelConverter converter = PhotometricDataModelConverter.get(file.getName());
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    try (OutputStream out = new FileOutputStream(file)) {
                        write(out, models, converter);
                    } catch (IOException e) {
                        logger.error(e);
                        errorAlert();
                    }
                    return null;
                }
            };
            Dialog dialog = FXMLUtils.getProgressDialog(stage, task);
            executor.execute(task);
            dialog.showAndWait();
        }
    }

    private void write(OutputStream out, List<PhotometricDataModel> models, PhotometricDataModelConverter converter) throws IOException {
        byte[] byteArray = converter.toString(models).getBytes();
        out.write(byteArray, 0, byteArray.length);
    }

    private void errorAlert() {
        FXMLUtils.alert(resources.getString("photometricdata.error"), resources.getString("failed.export"), resources.getString("delete.reload.app"), Alert.AlertType.ERROR);
    }

    public StellarObjectModel getStellarObject() {
        return stellarObject;
    }

    public void setStellarObject(StellarObjectModel stellarObject) {
        this.stellarObject = stellarObject;
        this.epochTextField.getInnerTextField().textProperty().bindBidirectional(stellarObject.epochProperty(), new NumberStringConverter(Locale.ENGLISH, "#.####"));
        this.periodTextField.getInnerTextField().textProperty().bindBidirectional(stellarObject.periodProperty(), new NumberStringConverter(Locale.ENGLISH, "#.##########"));
    }


    @FXML
    private void showReportMenuItem() {
        app.showSearchReport(stage, stellarObject, data);
    }

    @FXML
    private void handleOpenPlugins() {
        try {
            Desktop.getDesktop().open(pluginsDir);
        } catch (IOException e) {
            logger.error(e);
            FXMLUtils.alert(resources.getString("failed"), null, resources.getString("open.plugins.failed"), Alert.AlertType.ERROR);
        }
    }

    private MenuItem getOpenOriginalFileMenuItem(String pluginName, String id) {
        File original = getOriginalFile(pluginName, id);
        if (original != null) {
            MenuItem menuItem = new MenuItem(pluginName + (id.isEmpty() ? "" : ("-" + id)));
            menuItem.setOnAction(event -> {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(original);
                    } else {
                        FXMLUtils.textInputDialog(pluginsDir.getAbsolutePath(), resources.getString("not.supported"), resources.getString("desktop.open.not.supported"), resources.getString("file.path"), false).showAndWait();
                    }
                } catch (IOException e) {
                    logger.error(e);
                    FXMLUtils.alert(resources.getString("failed"), null, resources.getString("open.plugins.failed"), Alert.AlertType.ERROR);
                }
            });
            return menuItem;
        }
        return null;
    }

    private MenuItem getOpenOutputFolderMenuItem(String pluginName) {
        File outputDir = getOutputDir(pluginName);
        if (outputDir.exists()) {
            MenuItem menuItem = new MenuItem(pluginName);
            menuItem.setOnAction(event -> {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(outputDir);
                    } else {
                        FXMLUtils.textInputDialog(pluginsDir.getAbsolutePath(), resources.getString("not.supported"), resources.getString("desktop.open.not.supported"), resources.getString("folder.path"), false).showAndWait();
                    }
                } catch (IOException e) {
                    logger.error(e);
                    FXMLUtils.alert(resources.getString("failed"), null, resources.getString("open.plugins.failed"), Alert.AlertType.ERROR);
                }
            });
            return menuItem;
        }
        return null;
    }

    private File getOriginalFile(String pluginName, String id) {
        File outputDir = getOutputDir(pluginName);
        if (outputDir.exists()) {
            File[] list = outputDir.listFiles((dir, name) -> {
                return name.startsWith(pluginName + (id.isEmpty() ? "" : ("-" + id)));
            });
            if (list.length == 0) {
                list = outputDir.listFiles((dir, name) -> name.startsWith(pluginName));
            }
            if (list.length > 0) {
                return getNewest(list);
            }
        }
        return null;
    }

    private File getOutputDir(String pluginName) {
        return new File(pluginsDir, pluginName + File.separator + "output");
    }

    private File getNewest(File[] files) {
        File newest = files[0];
        for (int i = 1; i < files.length; i++) {
            if (files[i].lastModified() > newest.lastModified()) {
                newest = files[i];
            }
        }
        return newest;
    }
}
