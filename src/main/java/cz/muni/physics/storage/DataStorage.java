package cz.muni.physics.storage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.muni.physics.model.StarSurvey;
import cz.muni.physics.storage.converter.StarSurveyConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO this is horrible, make it better somehow?
 * @author Michal Krajčovič
 * @version 1.0
 * @since 06/04/16
 */
public class DataStorage {
    private final static Logger logger = LogManager.getLogger(DataStorage.class);

    private static final File datadir = new File(System.getProperty("user.home"), ".pdr");
    private static final File starSurveysFile = new File(datadir, "star_surveys.xml");
    private static final File pluginsDir = new File(datadir, "plugins");

    static {
        if (!datadir.exists()) {
            datadir.mkdir();
        }
        if (!starSurveysFile.exists()){
            try {
                System.out.println("creating file");
                starSurveysFile.createNewFile();
                InputStream inputStream = DataStorage.class.getResourceAsStream("/star_surveys.xml");
                OutputStream outputStream =
                        new FileOutputStream(starSurveysFile);
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveStarSurveys(List<StarSurvey> records) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new StarSurveyConverter());
        try (Writer writer = new FileWriter(starSurveysFile)){
            xStream.toXML(records, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<StarSurvey> loadStarSurveys(){
        List<StarSurvey> result = new ArrayList<>();
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new StarSurveyConverter());
        try (Reader reader = new FileReader(starSurveysFile)){
            result.addAll((List<StarSurvey>) xStream.fromXML(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static File getDatadir() {
        return datadir;
    }

    public static File getStarSurveysFile() {
        return starSurveysFile;
    }

    public static File getPluginsDir() {
        return pluginsDir;
    }
}
