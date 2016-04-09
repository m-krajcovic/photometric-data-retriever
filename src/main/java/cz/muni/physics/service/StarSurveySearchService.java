package cz.muni.physics.service;

import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.plugin.PluginManager;
import cz.muni.physics.plugin.StreamGobbler;
import cz.muni.physics.sesame.SesameResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

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
 * @since 06/04/16
 */
@Component
public class StarSurveySearchService extends Service<Map<StarSurvey, List<PhotometricData>>> {

    private final static Logger logger = LogManager.getLogger(StarSurveySearchService.class);

    @Autowired
    private PluginManager pluginManager;

    private SesameResult sesameResult;
    private List<StarSurvey> starSurveys;
    private ObservableMap<StarSurvey, Boolean> starSurveysMap = FXCollections.observableMap(new HashMap<>());

    @Override
    public void reset() {
        super.reset();
        starSurveysMap.clear();
    }

    @Override
    protected Task<Map<StarSurvey, List<PhotometricData>>> createTask() {
        return new Task<Map<StarSurvey, List<PhotometricData>>>() {
            @Override
            protected Map<StarSurvey, List<PhotometricData>> call() {
                Map<StarSurvey, List<PhotometricData>> resultMap = Collections.synchronizedMap(new HashMap<>());
                ExecutorService executor = Executors.newFixedThreadPool(6);
                for (StarSurvey starSurvey : starSurveys) {
                    List<PhotometricData> surveyData = new ArrayList<>();
                    if (starSurvey.getPlugin() == null) {
                        logger.debug("Plugin not found for Star Survey: ", starSurvey.getName());
                        updateStarSurveys(starSurvey, false);
                        continue;
                    }
                    Map<String, String> urlVars = getUrlVars(sesameResult, starSurvey);
                    urlVars.put("url",getUrl(starSurvey, urlVars));
                    Process process;
                    try {
                        process = pluginManager.run(starSurvey.getPlugin(), urlVars);
                    } catch (IOException e) {
                        logger.error("Failed to run {}", starSurvey.getPlugin().getMainFile());
                        updateStarSurveys(starSurvey, false);
                        continue;
                    }
                    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
                    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), line -> {
                        String[] split = line.split(",");
                        if (split.length >= 3 && NumberUtils.isParsable(split[0])
                                && NumberUtils.isParsable(split[1]) && NumberUtils.isParsable(split[2])) {
                            PhotometricData data = new PhotometricData(split[0], split[1], split[2]);
                            surveyData.add(data);
                        }
                    });
                    outputGobbler.setFinished(() -> {
                        resultMap.put(starSurvey, surveyData);
                        updateStarSurveys(starSurvey, true);
                    });
                    outputGobbler.setFailed(() -> updateStarSurveys(starSurvey, false));
                    executor.execute(errorGobbler);
                    executor.execute(outputGobbler);
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(300, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return resultMap;
            }
        };
    }

    private void updateStarSurveys(StarSurvey starSurvey, boolean succeeded) {
        Platform.runLater(() -> starSurveysMap.put(starSurvey, succeeded));
    }

    private String getUrl(StarSurvey record, Map<String, String> urlVars) {
        UriTemplate uriTemplate = new UriTemplate(record.getURL());
        URI uri = uriTemplate.expand(urlVars); // TODO check variables -> show me something, log at least
        return uri.toString();
    }

    private Map<String, String> getUrlVars(SesameResult sesameResult, StarSurvey record) {
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

    public SesameResult getSesameResult() {
        return sesameResult;
    }

    public void setSesameResult(SesameResult sesameResult) {
        this.sesameResult = sesameResult;
    }

    public List<StarSurvey> getStarSurveys() {
        return starSurveys;
    }

    public void setStarSurveys(List<StarSurvey> starSurveys) {
        this.starSurveys = starSurveys;
    }

    public ObservableMap<StarSurvey, Boolean> getStarSurveysMap() {
        return starSurveysMap;
    }

    public void setStarSurveysMap(ObservableMap<StarSurvey, Boolean> starSurveysMap) {
        this.starSurveysMap = starSurveysMap;
    }
}
