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
import java.util.List;
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

    @Value("${user.home}${app.data.dir.path}")
    private String dataDirPath;
    @Value("${starsurvey.file.name}")
    private String starSurveysFileName;

    @Autowired
    XStream xStream;

    List<StarSurvey> starSurveys;

    /*
     * TODO : file watcher -> load new on event
     */


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
        Collection<StarSurvey> allEntities = getAll();
        allEntities.add(entity);
        saveFile(allEntities);
    }

    @Override
    public void delete(StarSurvey entity) {
        Collection<StarSurvey> allEntities = getAll();
        allEntities.remove(entity);
        saveFile(allEntities);
    }

    @Override
    public Collection<StarSurvey> getAll() {
        if (starSurveys == null) {
            File starSurveysFile = new File(dataDirPath, starSurveysFileName);
            starSurveys = new ArrayList<>();
            try (Reader reader = new FileReader(starSurveysFile)) {
                starSurveys.addAll((List<StarSurvey>) xStream.fromXML(reader));
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
        List<StarSurvey> newList = new ArrayList<>(starSurveys);
        starSurveys.forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    @Override
    public StarSurvey getById(String s) {
        return searchFor(survey -> survey.getName().equalsIgnoreCase(s));
    }

    @Override
    public StarSurvey searchFor(Predicate<StarSurvey> predicate) {
        Optional<StarSurvey> optional = getAll().stream().filter(predicate).findFirst();
        if (optional.isPresent()) {
            return new StarSurvey(optional.get());
        }
        return null;
    }

    @Override
    public Collection<StarSurvey> searchForAll(Predicate<StarSurvey> predicate) {
        List<StarSurvey> result = starSurveys.stream().filter(predicate).collect(Collectors.toList());
        List<StarSurvey> newList = new ArrayList<>(result.size());
        result.forEach(starSurvey -> newList.add(new StarSurvey(starSurvey)));
        return newList;
    }

    private void saveFile(Collection<StarSurvey> allEntities) {
        File starSurveysFile = new File(dataDirPath, starSurveysFileName);
        try (Writer writer = new FileWriter(starSurveysFile)) {
            xStream.toXML(allEntities, writer);
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
    }
}
