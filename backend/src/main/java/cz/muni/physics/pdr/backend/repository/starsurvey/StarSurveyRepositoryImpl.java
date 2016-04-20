package cz.muni.physics.pdr.backend.repository.starsurvey;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;
import cz.muni.physics.pdr.backend.repository.FileWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
@Component
public class StarSurveyRepositoryImpl implements StarSurveyRepository {

    private final static Logger logger = LogManager.getLogger(StarSurveyRepositoryImpl.class);

    private String starSurveysFilePath;
    private FileWatcher fileWatcher;
    private XStream xStream;
    private Map<String, StarSurvey> starSurveys;

    @Autowired
    public StarSurveyRepositoryImpl(XStream xStream,
                                    @Value("${user.home}${starsurveys.file.path}") String starSurveysFilePath) {
        this.xStream = xStream;
        this.starSurveysFilePath = starSurveysFilePath;
        this.fileWatcher = new FileWatcher(starSurveysFilePath);
    }


    @Override
    public void insert(StarSurvey entity)  {
        starSurveys.put(entity.getName(), new StarSurvey(entity));
        saveToFile();
    }

    @Override
    public void delete(StarSurvey entity)  {
        if (starSurveys.containsKey(entity.getName())) {
            starSurveys.remove(entity.getName());
            saveToFile();
        }
    }

    @Override
    public Collection<StarSurvey> getAll()  {
        checkAndLoadSurveys();
        List<StarSurvey> newList = new ArrayList<>(starSurveys.values().size());
        starSurveys.values().forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    @Override
    public StarSurvey getById(String s)  {
        checkAndLoadSurveys();
        return new StarSurvey(starSurveys.get(s));
    }

    @Override
    public StarSurvey searchFor(Predicate<StarSurvey> predicate)  {
        checkAndLoadSurveys();
        Optional<StarSurvey> optional = starSurveys.values().stream().filter(predicate).findFirst();
        if (optional.isPresent()) {
            return new StarSurvey(optional.get());
        }
        return null;
    }

    @Override
    public Collection<StarSurvey> searchForAll(Predicate<StarSurvey> predicate)  {
        checkAndLoadSurveys();
        List<StarSurvey> result = starSurveys.values().stream().filter(predicate).collect(Collectors.toList());
        List<StarSurvey> newList = new ArrayList<>(result.size());
        result.forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    private void checkAndLoadSurveys()  {
        if (starSurveys == null) {
            logger.debug("Surveys were not loaded yet. Loading surveys from {}", starSurveysFilePath);
            loadSurveys();
        } else if (fileWatcher.isFileUpdated()) {
            logger.debug("Outside change in star surveys xml file. Loading surveys from {}", starSurveysFilePath);
            loadSurveys();
        } else {
            logger.debug("No outside changes in star surveys. Using cached copy.");
        }
    }

    private synchronized void loadSurveys(int retryCount)  {
        if (starSurveys == null || fileWatcher.isFileUpdated()) {
            File starSurveysFile = new File(starSurveysFilePath);
            try (Reader reader = new FileReader(starSurveysFile)) {
                Map<String, StarSurvey> tempSurveys = new HashMap<>();
                List<StarSurvey> fromXML = ((List<StarSurvey>) xStream.fromXML(reader));
                for (StarSurvey starSurvey : fromXML) {
                    logger.debug("Loaded survey {} ", starSurvey.getName());
                    tempSurveys.put(starSurvey.getName(), starSurvey);
                }
                starSurveys = new HashMap<>(tempSurveys);
            } catch (IOException | XStreamException e) {
                if (starSurveys != null && retryCount <= 3) {
                    saveToFile();
                    loadSurveys(++retryCount);
                } else {
                    throw new ResourceAvailabilityException("Failed to load file", starSurveysFilePath, e);
                }
            }
        }
    }

    private synchronized void loadSurveys()  {
        loadSurveys(0);
    }

    private synchronized void saveToFile()  {
        List<StarSurvey> allEntities = new ArrayList<>(starSurveys.values());
        File starSurveysFile = new File(starSurveysFilePath);
        try (Writer writer = new FileWriter(starSurveysFile)) {
            xStream.toXML(allEntities, writer);
        } catch (IOException e) {
            throw new ResourceAvailabilityException("Failed to save to file", starSurveysFilePath, e);
        }
    }
}
