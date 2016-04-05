package cz.muni.physics.utils;

import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SpringFxmlLoader {

    private final static Logger logger = LogManager.getLogger(SpringFxmlLoader.class);
    private final static SpringFxmlLoader instance = new SpringFxmlLoader();

//    private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

    private SpringFxmlLoader(){}

    private FXMLLoader lastLoader;

    public Object load(String url) {
        logger.debug("Loading resource: " + url);
        try {
            URL fxml = SpringFxmlLoader.class.getResource(url);
            lastLoader = new FXMLLoader();
            lastLoader.setLocation(fxml);
            lastLoader.setControllerFactory(aClass -> {
                logger.debug("Getting bean: " + aClass.getName());
                return ApplicationContextHolder.getBean(aClass);
            });
            return lastLoader.load();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public static SpringFxmlLoader getInstance() {
        return instance;
    }

    public FXMLLoader getLastLoader() {
        return lastLoader;
    }

}
