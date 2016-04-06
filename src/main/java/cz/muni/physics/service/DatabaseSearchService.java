package cz.muni.physics.service;

import cz.muni.physics.java.PhotometricData;
import cz.muni.physics.model.DatabaseRecord;
import cz.muni.physics.plugin.StreamGobbler;
import cz.muni.physics.sesame.SesameResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.math.NumberUtils;
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
public class DatabaseSearchService extends Service<List<PhotometricData>> {

    private SesameResult sesameResult;
    private List<DatabaseRecord> databaseRecords;


    @Override
    protected Task<List<PhotometricData>> createTask() {
        return new Task<List<PhotometricData>>() {
            @Override
            protected List<PhotometricData> call() {
                List<PhotometricData> result = Collections.synchronizedList(new ArrayList<>());

                ExecutorService executor = Executors.newFixedThreadPool(6);

                for (DatabaseRecord record : databaseRecords) {
                    if (record.getPlugin() == null) {
//                        logger.debug("Plugin not found for db record: ", record.getName());
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
//                    outputGobbler.setFinished(() ->
//                            Platform.runLater(() -> progressLabel.setText("Finished searching in " + record.getName() + " database.")));

                    executor.execute(errorGobbler);
                    executor.execute(outputGobbler);

                }
                executor.shutdown();
                try {
                    executor.awaitTermination(240, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return result;
            }
        };
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

    public SesameResult getSesameResult() {
        return sesameResult;
    }

    public void setSesameResult(SesameResult sesameResult) {
        this.sesameResult = sesameResult;
    }

    public List<DatabaseRecord> getDatabaseRecords() {
        return databaseRecords;
    }

    public void setDatabaseRecords(List<DatabaseRecord> databaseRecords) {
        this.databaseRecords = databaseRecords;
    }
}
