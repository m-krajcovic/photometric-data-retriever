package cz.muni.physics.pdr.app.spring;

import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
@Component
@Scope("prototype")
public class SpringFXMLLoader {

    private final static Logger logger = LogManager.getLogger(SpringFXMLLoader.class);
    @Autowired
    private ApplicationContext context;
    private FXMLLoader fxmlLoader = new FXMLLoader();

    public SpringFXMLLoader() {
        fxmlLoader.setControllerFactory(aClass -> context.getBean(aClass));
    }

    public <T> T load(String url) {
        logger.debug("Loading fxml resource: {}", url);
        try {
            URL fxml = SpringFXMLLoader.class.getResource(url);
            fxmlLoader.setLocation(fxml);
            fxmlLoader.setResources(ResourceBundle.getBundle("i18n/bundle"));
            return fxmlLoader.load();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getMessage(), ioException);
        }
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public <T> T getController() {
        return fxmlLoader.getController();
    }
}
