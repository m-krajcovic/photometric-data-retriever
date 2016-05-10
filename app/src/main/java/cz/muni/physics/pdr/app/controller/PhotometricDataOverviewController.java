package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.javafx.PhotometricChartDataFactory;
import cz.muni.physics.pdr.app.javafx.control.TitledTextFieldBox;
import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.model.converters.PhotometricDataModelConverter;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Executor;
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

    private final static Logger logger = LogManager.getLogger(PhotometricDataOverviewController.class);

    @Autowired
    private Executor executor;


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
                System.getProperty("user.home"),
                "pdr-export-" + coords,
                stage,
                new FileChooser.ExtensionFilter("Zip file (*.zip)", "*.zip"));
        if (zip != null)
            toZip(zip, coords + entryFormat);
        }
    }

    @FXML
    private void handleCloseMenuItem() {

    }

    public void setData(Map<StarSurveyModel, List<PhotometricDataModel>> data) {
        this.data = data;
        chart.getData().clear();
        for (Map.Entry<StarSurveyModel, List<PhotometricDataModel>> entry : data.entrySet()) {
            tabPane.getTabs().add(getTab(entry.getKey().getName(), entry.getValue()));
            saveMenu.getItems().add(getMenuItem(entry.getKey().getName(), entry.getValue()));
        }
        drawChart();
    }

    @FXML
    private void drawChart() {
        chart.getData().clear();
        PhotometricChartDataFactory dataFactory = PhotometricChartDataFactory.getInstance(stellarObject);
        dataFactory.setUpChart(chart, resources);
        chartProgressIndicator.setVisible(true);
        chartProgressIndicator.setDisable(false);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (Map.Entry<StarSurveyModel, List<PhotometricDataModel>> entry : data.entrySet()) {
                    XYChart.Series<Number, Number> series = dataFactory.getSeries(entry.getKey().getName(), entry.getValue());
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

    private Tab getTab(String name, List<PhotometricDataModel> models) {
        TableView<PhotometricDataModel> table;
        try {
            table = FXMLLoader.load(this.getClass().getResource("/view/PhotometricDataTable.fxml"), resources);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .fxml resource", e);
        }
        table.getItems().addAll(models);
        Tab tab = new Tab(String.format("%s (%d)", name, models.size()), table);
        tab.setClosable(false);
        return tab;
    }

    private MenuItem getMenuItem(String name, List<PhotometricDataModel> models) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(event -> {
            toFile(name, models);
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
                        ZipEntry e = new ZipEntry(MessageFormat.format("{0}{1}", entry.getKey().getName(), converter.getExtension()));
                        out.putNextEntry(e);
                        write(out, entry.getValue(), converter);
                        out.closeEntry();
                    }
                } catch (IOException e) {
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
                System.getProperty("user.home"),
                name + "-" + coords,
                stage,
                new FileChooser.ExtensionFilter("Csv file (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Ascii table (*.txt)", "*.txt"));
        if (file != null) {
            PhotometricDataModelConverter converter = PhotometricDataModelConverter.get(file.getName());
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    try (OutputStream out = new FileOutputStream(file)) {
                        write(out, models, converter);
                    } catch (IOException e) {
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
        this.epochTextField.getTextField().textProperty().bindBidirectional(stellarObject.epochProperty(), new NumberStringConverter("#.####"));
        this.periodTextField.getTextField().textProperty().bindBidirectional(stellarObject.periodProperty(), new NumberStringConverter("#.##########"));
    }


}
