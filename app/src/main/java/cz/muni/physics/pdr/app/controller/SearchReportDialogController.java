package cz.muni.physics.pdr.app.controller;

import cz.muni.physics.pdr.app.model.PhotometricDataModel;
import cz.muni.physics.pdr.app.model.StarSurveyModel;
import cz.muni.physics.pdr.app.model.StellarObjectModel;
import cz.muni.physics.pdr.app.utils.FXMLUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.prefs.Preferences;

/**
 * Created by Michal on 5/13/2016.
 */
@Component
public class SearchReportDialogController extends StageController {

    private static final Logger logger = LogManager.getLogger(SearchReportDialogController.class);

    @Autowired
    private Executor executor;
    @Autowired
    private Preferences preferences;
    @Value("${last.save.path}")
    private String lastSavePath;

    @FXML
    private ResourceBundle resources;
    @FXML
    private TextArea textArea;

    private StringProperty text = new SimpleStringProperty("");


    @FXML
    private void initialize() {
        textArea.setFont(new Font("Courier New", 12));
        text.bindBidirectional(textArea.textProperty());
    }

    @FXML
    private void handleExport() {
        File file = FXMLUtils.showSaveFileChooser(resources.getString("choose.output.file"),
                lastSavePath,
                "PDR report",
                stage,
                new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt"));
        if (file != null) {
            updateLastSavePath(file.getParent());
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    try (OutputStream out = new FileOutputStream(file)) {
                        byte[] byteArray = text.get().getBytes();
                        out.write(byteArray, 0, byteArray.length);
                    } catch (IOException e) {
                        logger.error(e);
                    }
                    return null;
                }
            };
            Dialog dialog = FXMLUtils.getProgressDialog(stage, task);
            executor.execute(task);
            dialog.showAndWait();
        }
    }

    private void updateLastSavePath(String newPath) {
        lastSavePath = newPath;
        preferences.put("last.save.path", lastSavePath);
    }

    public void setData(StellarObjectModel stellarObject, Map<StarSurveyModel, List<PhotometricDataModel>> data) {
        String output = new Date().toString() + " - PDR Search report" + System.lineSeparator() + stellarObject.getName() + " (" + stellarObject.getRightAscension() + " " + stellarObject.getDeclination() + ")" + System.lineSeparator();
        for (Map.Entry<StarSurveyModel, List<PhotometricDataModel>> entry : data.entrySet()) {
            output += entry.getKey().getName();
            output += StringUtils.repeat('.', 15 - entry.getKey().getName().length());
            output += entry.getValue().size() + System.lineSeparator();
        }
        text.set(output);
    }
}
