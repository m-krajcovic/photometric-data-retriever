package cz.muni.physics.service;

import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.nameresolver.NameResolverResult;
import cz.muni.physics.plugin.PluginManager;
import cz.muni.physics.plugin.StreamGobbler;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    Map<String, String> params = getParams(nameResolverResult, starSurvey);
                    params = resolveValueParams(starSurvey, params);
                    params = resolveUrl(starSurvey, params);
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

    private Map<String, String> resolveValueParams(StarSurvey starSurvey, Map<String, String> params) {
        starSurvey.getValueParameters().forEach((key, value) -> {
            Set<String> stringParameters = findParameters(value);
            if (stringParameters.stream().allMatch(params::containsKey)) {
                params.put(key, value);
            }
        });
        return params;
    }

    private void updateStarSurveys(StarSurvey starSurvey, boolean succeeded) {
        Platform.runLater(() -> starSurveysMap.put(starSurvey, succeeded));
    }

    private Map<String, String> resolveUrl(StarSurvey record, Map<String, String> params) {
        for (String url : record.getUrls()) {
            UriTemplate uriTemplate = new UriTemplate(url);
            if (uriTemplate.getVariableNames().stream().allMatch(params::containsKey)) {
                params.put("url", uriTemplate.expand(params).toString());
                break;
            }
        }
        return params;
    }

    private Map<String, String> getParams(NameResolverResult nameResolverResult, StarSurvey survey) {
        Map<String, String> urlVars = new HashMap<>();
        for (Pattern pattern : survey.getRegexPatterns()) {
            Set<String> groups = findNamedGroups(pattern);
            Matcher m = pattern.matcher(nameResolverResult.toLines());
            if (m.find()) {
                for (String group : groups) {
                    urlVars.put(group, m.group(group));
                }
            }
        }
        urlVars.put("ra", nameResolverResult.getJraddeg());
        urlVars.put("dec", nameResolverResult.getJdedeg());
        return urlVars;
    }

    private Set<String> findParameters(String str) {
        Set<String> params = new HashSet<>();
        String param = "";
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == '$' && chars[i + 1] == '{') {
                i += 2;
                for (int j = i; j < chars.length; j++) {
                    if (chars[j] == '}') {
                        params.add(param);
                        param = "";
                        break;
                    }
                    param += chars[j];
                    i++;
                }
            }
        }
        return params;
    }

    private Set<String> findNamedGroups(Pattern p) {
        Set<String> namedGroups = new HashSet<>();
        String groupName = "";
        char[] chars = p.pattern().toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == '(' && chars[i + 1] == '?' && chars[i + 2] == '<') {
                i += 3;
                for (int j = i; j < chars.length; j++) {
                    if (chars[j] == '>') {
                        namedGroups.add(groupName);
                        groupName = "";
                        break;
                    }
                    groupName += chars[j];
                    i++;
                }
            }
        }
        return namedGroups;
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
