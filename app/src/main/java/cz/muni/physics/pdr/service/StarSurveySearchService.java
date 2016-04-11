package cz.muni.physics.pdr.service;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.model.StarSurvey;
import cz.muni.physics.pdr.nameresolver.NameResolverResult;
import cz.muni.physics.pdr.plugin.PluginManager;
import cz.muni.physics.pdr.plugin.StreamGobbler;
import cz.muni.physics.pdr.utils.ParameterUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TODO this class needs huge refactoring
 *
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
@Component
public class StarSurveySearchService extends Service<Map<StarSurvey, List<PhotometricData>>> {

    private final static Logger logger = LogManager.getLogger(StarSurveySearchService.class);

    @Autowired
    private PluginManager pluginManager;

    private NameResolverResult nameResolverResult;
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
                    Map<String, String> params = ParameterUtils.resolveParametersForSurvey(starSurvey, nameResolverResult);
                    if(!pluginManager.preparePlugin(starSurvey.getPlugin(), params)){
                        logger.debug("Plugin {} is not able to start due to missing parameters", starSurvey.getPlugin().getMainFile());
                        updateStarSurveys(starSurvey, false);
                        continue;
                    }
                    Process process;
                    try {
                        process = pluginManager.run(starSurvey.getPlugin(), params);
                    } catch (IOException e) {
                        logger.error("Failed to run {}", starSurvey.getPlugin().getMainFile(), e);
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
                        if (!surveyData.isEmpty()) {
                            resultMap.put(starSurvey, surveyData);
                            updateStarSurveys(starSurvey, true);
                        } else {
                            updateStarSurveys(starSurvey, false);
                        }
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

    public NameResolverResult getNameResolverResult() {
        return nameResolverResult;
    }

    public void setNameResolverResult(NameResolverResult nameResolverResult) {
        this.nameResolverResult = nameResolverResult;
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
