package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.DatabaseRecord;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
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
                protected List<PhotometricData> call()  {
                    List<PhotometricData> result = new ArrayList<>();
                    SesameResult sesameResult = null;
                    try {
                        sesameResult = sesameClient.getData(searchTextField.getText());
                    } catch (XPathExpressionException | ResourceAccessException e) {
                        //e.printStackTrace();
                        cancel();
                        return null;
                    }

                    for (DatabaseRecord record : mainApp.getDbRecords()) {
                        if(record.getPlugin() == null){
                            logger.debug("Plugin not found for db record: ", record.getName());
                            continue;
                        }
                        Map<String, String> urlVars = new HashMap<>();
                        Platform.runLater(() -> progressLabel.setText("Searching in " + record.getName() + " database."));
                        Set<String> groupNames = record.getSesameVariables();
                        Pattern p = record.getSesamePattern();
                        if(!record.getSesameAlias().isEmpty()) {
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
                        try {
                            UriTemplate uriTemplate = new UriTemplate(record.getURL());
                            URI url = uriTemplate.expand(urlVars);
                            System.out.println(url);

                            // TODO StreamGubbler or whatever that was

                            Process process = Runtime.getRuntime().exec(record.getPlugin().getFullCommand(url.toString()));
                            InputStream is = process.getInputStream();
                            InputStream ise = process.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            InputStreamReader isre = new InputStreamReader(ise);
                            BufferedReader buff = new BufferedReader(isr);
                            BufferedReader buffe = new BufferedReader(isre);

                            String line;
                            while ((line = buff.readLine()) != null) {
                                String[] split = line.split(",");
                                if(split.length < 3 || !NumberUtils.isParsable(split[0])
                                        || !NumberUtils.isParsable(split[1]) || !NumberUtils.isParsable(split[2])) continue;
                                PhotometricData data = new PhotometricData(split[0], split[1], split[2]);
                                result.add(data);
                            }
                            while ((line = buffe.readLine()) != null) {
                                System.out.println(line);
                            }
                        } catch (IOException e) {
                            System.out.println("fuck this shit"); // TODO
                            e.printStackTrace();
                        }
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

            task.setOnCancelled(e ->{
                logger.error("This got cancelled due to reasons"); // TODO
                toggleElements(false);
            });

            new Thread(task).start();
        }
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
