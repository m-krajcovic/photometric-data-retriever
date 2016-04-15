package cz.muni.physics.pdr.backend.repository.starsurvey;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.pdr.backend.entity.StarSurvey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private String dataDirPath;
    private String starSurveysFileName;
    private XStream xStream;
    private Map<String, StarSurvey> starSurveys;

    /*
     * TODO : file watcher -> load new on event
     */

    @Autowired
    public StarSurveyRepositoryImpl(XStream xStream,
                                    @Value("${starsurvey.file.name}") String starSurveysFileName,
                                    @Value("${user.home}${app.data.dir.path}") String dataDirPath) {
        this.xStream = xStream;
        this.starSurveysFileName = starSurveysFileName;
        this.dataDirPath = dataDirPath;
    }


    @PostConstruct
    private void initialize() { // todo something here
        File dir = new File(dataDirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File starSurveysFile = new File(dataDirPath, starSurveysFileName);
        if (!starSurveysFile.exists()) {
            try (InputStream inputStream = StarSurveyRepositoryImpl.class.getResourceAsStream("/" + starSurveysFileName);
                 OutputStream outputStream = new FileOutputStream(starSurveysFile)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
    }

    @Override
    public void insert(StarSurvey entity) {
        starSurveys.put(entity.getName(), new StarSurvey(entity));
        saveToFile(starSurveys.values());
    }

    @Override
    public void delete(StarSurvey entity) {
        if (starSurveys.containsKey(entity.getName())) {
            starSurveys.remove(entity.getName());
            saveToFile(starSurveys.values());
        }
    }

    @Override
    public Collection<StarSurvey> getAll() {
        loadSurveys(false);
        List<StarSurvey> newList = new ArrayList<>(starSurveys.values().size());
        starSurveys.values().forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    @Override
    public StarSurvey getById(String s) {
        loadSurveys(false);
        return new StarSurvey(starSurveys.get(s));
    }

    @Override
    public StarSurvey searchFor(Predicate<StarSurvey> predicate) {
        loadSurveys(false);
        Optional<StarSurvey> optional = starSurveys.values().stream().filter(predicate).findFirst();
        if (optional.isPresent()) {
            return new StarSurvey(optional.get());
        }
        return null;
    }

    @Override
    public Collection<StarSurvey> searchForAll(Predicate<StarSurvey> predicate) {
        loadSurveys(false);
        List<StarSurvey> result = starSurveys.values().stream().filter(predicate).collect(Collectors.toList());
        List<StarSurvey> newList = new ArrayList<>(result.size());
        result.forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    private synchronized void loadSurveys(boolean force) {
        if (starSurveys == null || force) {
            File starSurveysFile = new File(dataDirPath, starSurveysFileName);
            try (Reader reader = new FileReader(starSurveysFile)) {
                Map<String, StarSurvey> tempSurveys = new HashMap<>();
                List<StarSurvey> fromXML = ((List<StarSurvey>) xStream.fromXML(reader));
                for (StarSurvey starSurvey : fromXML) {
                    tempSurveys.put(starSurvey.getName(), starSurvey);
                }
                starSurveys = new HashMap<>(tempSurveys);
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
    }

    private synchronized void saveToFile(Collection<StarSurvey> allEntities) {
        File starSurveysFile = new File(dataDirPath, starSurveysFileName);
        try (Writer writer = new FileWriter(starSurveysFile)) {
            xStream.toXML(allEntities, writer);
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
    }
}
