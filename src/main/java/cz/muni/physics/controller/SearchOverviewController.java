package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.plugin.StreamGobbler;
import cz.muni.physics.sesame.SesameClient;
import cz.muni.physics.sesame.SesameResult;
import cz.muni.physics.utils.FXMLUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriTemplate;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class SearchOverviewController {

    private final static Logger logger = LogManager.getLogger(SearchOverviewController.class);

    private MainApp mainApp;

    @Autowired
    private SesameClient sesameClient;

    @FXML
    private SplitMenuButton searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    @FXML
    private void handleSearchButtonAction() {
        toggleElements(true);
        Stage primaryStage = (Stage) searchButton.getScene().getWindow();
        if (searchTextField.getText().isEmpty()) {
            logger.debug("Text field is empty");
            FXMLUtil.showTooltip("Text field is empty", primaryStage, searchTextField);
        } else {
            logger.debug("Handling search by name '{}'", searchTextField.getText());
            Task<List<PhotometricData>> task = new Task<List<PhotometricData>>() {
                @Override
                protected List<PhotometricData> call() {
                    List<PhotometricData> result = Collections.synchronizedList(new ArrayList<>());
                    SesameResult sesameResult;
                    try {
                        sesameResult = sesameClient.getData(searchTextField.getText());
                    } catch (XPathExpressionException | ResourceAccessException e) {
                        //e.printStackTrace();
                        cancel();
                        return null; // TODO may be in more tasks -> one for getting sesame -> second for searching stuff n shit :) yeah.
                    }

                    ExecutorService executor = Executors.newFixedThreadPool(6);

                    for (DatabaseRecord record : mainApp.getDbRecords()) {
                        if (record.getPlugin() == null) {
                            logger.debug("Plugin not found for db record: ", record.getName());
                            continue;
                        }

                        Map<String, String> urlVars = getUrlVars(sesameResult, record);
                        String url = getUrl(record, urlVars);

                        Process process;
                        try {
                            process = Runtime.getRuntime().exec(record.getPlugin().getFullCommand(url));
                        } catch (IOException e) {
                            // TODO handling in controller task
//                            e.printStackTrace();
                            continue;
                        }

                        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());

                        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), line -> {
                            String[] split = line.split(",");
                            if (split.length >= 3 && NumberUtils.isParsable(split[0])
                                    && NumberUtils.isParsable(split[1]) && NumberUtils.isParsable(split[2])) {
                                PhotometricData data = new PhotometricData(split[0], split[1], split[2]);
                                result.add(data);
                            }
                        });
                        outputGobbler.setFinished(() -> {
                            Platform.runLater(() -> progressLabel.setText("Finished searching in " + record.getName() + " database."));
                        });

                        executor.execute(errorGobbler);
                        executor.execute(outputGobbler);

                    }
                    executor.shutdown();
                    try {
                        executor.awaitTermination(120, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            };

            task.setOnSucceeded(e -> {
                logger.debug("Succeeded");
                List<PhotometricData> data = task.getValue();
                if (data.size() == 0) {
                    FXMLUtil.showTooltip("No results found", primaryStage, searchTextField);
                } else {
                    mainApp.showPhotometricDataOverview(data);
                }
                toggleElements(false);
            });

            task.setOnFailed(e -> {
                logger.debug("Failed"); // TODO
                toggleElements(false);
            });

            task.setOnCancelled(e -> {
                logger.error("This got cancelled due to reasons"); // TODO
                toggleElements(false);
            });

            new Thread(task).start();
        }
    }

    private String getUrl(DatabaseRecord record, Map<String, String> urlVars) {
        UriTemplate uriTemplate = new UriTemplate(record.getURL());
        URI uri = uriTemplate.expand(urlVars);
        return uri.toString();
    }

    private Map<String, String> getUrlVars(SesameResult sesameResult, DatabaseRecord record) {
        Map<String, String> urlVars = new HashMap<>();

        Set<String> groupNames = record.getSesameVariables();
        Pattern p = record.getSesamePattern();
        if (!record.getSesameAlias().isEmpty()) {
            for (String name : sesameResult.getNames()) {
                Matcher m = p.matcher(name);
                if (m.matches()) {
                    for (String group : groupNames) {
                        urlVars.put(group, m.group(group));
                    }
                }
            }
        }
        urlVars.put("ra", sesameResult.getJraddeg());
        urlVars.put("dec", sesameResult.getJdedeg());
        return urlVars;
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
        progressLabel.setVisible(disabled);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
