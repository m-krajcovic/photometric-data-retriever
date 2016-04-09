package cz.muni.physics.storage;

import com.thoughtworks.xstream.XStream;
import cz.muni.physics.model.StarSurvey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO this is horrible, make it better somehow?
 *
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class DataStorage {
    private final static Logger logger = LogManager.getLogger(DataStorage.class);

    private File dataDir = new File(System.getProperty("user.home"), ".pdr");
    private File starSurveysFile = new File(dataDir, "star_surveys.xml");
    private File pluginsDir = new File(dataDir, "plugins");

    @Autowired
    private XStream xStream;

    public DataStorage(){
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        if (!starSurveysFile.exists()) {
            try (InputStream inputStream = DataStorage.class.getResourceAsStream("/star_surveys.xml");
                 OutputStream outputStream = new FileOutputStream(starSurveysFile)) {
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
    }

    public void saveStarSurveys(List<StarSurvey> records) {
        try (Writer writer = new FileWriter(starSurveysFile)) {
            xStream.toXML(records, writer);
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
    }

    public List<StarSurvey> loadStarSurveys() {
        List<StarSurvey> result = new ArrayList<>();
        try (Reader reader = new FileReader(starSurveysFile)) {
            result.addAll((List<StarSurvey>) xStream.fromXML(reader));
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
        return result;
    }

    public File getDataDir() {
        return dataDir;
    }

    public File getStarSurveysFile() {
        return starSurveysFile;
    }

    public File getPluginsDir() {
        return pluginsDir;
    }
}
