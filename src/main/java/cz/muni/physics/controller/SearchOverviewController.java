package cz.muni.physics.controller;

import cz.muni.physics.MainApp;
import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.plugin.java.JavaPluginManager;
import cz.muni.physics.sesame.SesameClientImpl;
import cz.muni.physics.utils.FXMLUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 20/03/16
 */
public class SearchOverviewController {

    private final static Logger logger = Logger.getLogger(SearchOverviewController.class);

    private MainApp mainApp;

    @Autowired
    private SesameClientImpl sesameClient;

    @Autowired
    private JavaPluginManager javaPluginManager;

    @FXML
    private SplitMenuButton searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ProgressIndicator searchProgressIndicator;
    @FXML
    private Label progressLabel;

    public SearchOverviewController() {

    }

    @FXML
    private void handleSearchButtonAction() {
        toggleElements(true);
        Stage primaryStage = (Stage) searchButton.getScene().getWindow();
        if (searchTextField.getText().isEmpty()) {
            logger.debug("Text field is empty");
            FXMLUtil.showTooltip("Text field is empty", primaryStage, searchTextField);
        } else {
            logger.debug("Handling search by name '" + searchTextField.getText() + "'.");
            Task<List<PhotometricData>> task = new Task<List<PhotometricData>>() {
                @Override
                protected List<PhotometricData> call() throws Exception {
                    //List<PhotometricData> data = new ArrayList<>();
//                    javaPluginManager.getPlugins().forEach(p -> {
//                        System.out.println(p.getPluginDescription().getName());
//                        p.getDataFromUrl("http://en.wikipedia.org/");
//                        //data.addAll(p.getDataFromUrl("http://en.wikipedia.org/"));
//                    });
                    List<PhotometricData> result = new ArrayList<>();
                    List<String> names = sesameClient.getData(searchTextField.getText()).getNames();


                    DatabaseRecord selected = mainApp.getDbRecords().get(0);
                    Pattern p = Pattern.compile(selected.getSesameAlias());

                    List<String> matches = new ArrayList<>();

                    for(String name : names){
                        Matcher m = p.matcher(name);
                        if(m.matches()){
                            matches.add(m.group(1));
                        }
                    }

                    try {
                        String objId = matches.get(0);
                        String url = MessageFormat.format(selected.getURL(), objId);
                        Process process = Runtime.getRuntime().exec(selected.getPlugin().getFullCommand(url));
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader buff = new BufferedReader(isr);

                        String line;
                        while ((line = buff.readLine()) != null){
                            String[] split = line.split(",");
                            PhotometricData data = new PhotometricData(split[0], split[1], split[2]);
                            result.add(data);
                        }
                    } catch (IOException e) {
                        System.out.println("fuck this shit");
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
                logger.debug("Failed");
                toggleElements(false);
            });

            new Thread(task).start();
        }
    }

    private void toggleElements(boolean disabled) {
        searchButton.setDisable(disabled);
        searchTextField.setDisable(disabled);
        searchProgressIndicator.setVisible(disabled);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
