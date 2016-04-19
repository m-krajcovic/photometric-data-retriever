package cz.muni.physics.pdr.app.utils;

import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import sun.reflect.CallerSensitive;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class SpringFXMLLoader {

    private final static Logger logger = LogManager.getLogger(SpringFXMLLoader.class);
    @Autowired
    private ApplicationContext context;
    private FXMLLoader fxmlLoader = new FXMLLoader();

    public SpringFXMLLoader() {
        fxmlLoader.setControllerFactory(aClass -> {
            logger.debug("Loading {} bean from Application Context", aClass.getName());
            return context.getBean(aClass);
        });
    }

    public <T> T load(String url) {
        logger.debug("Loading fxml resource: {}", url);
        try {
            URL fxml = SpringFXMLLoader.class.getResource(url);
            fxmlLoader.setLocation(fxml);
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

    public void setController(Object controller) {
        fxmlLoader.setController(controller);
    }

    public URL getLocation() {
        return fxmlLoader.getLocation();
    }

    public void setLocation(URL location) {
        fxmlLoader.setLocation(location);
    }

    public <T> T getRoot() {
        return fxmlLoader.getRoot();
    }

    public void setRoot(Object root) {
        fxmlLoader.setRoot(root);
    }

    public ResourceBundle getResources() {
        return fxmlLoader.getResources();
    }

    public void setResources(ResourceBundle resources) {
        fxmlLoader.setResources(resources);
    }

    public Callback<Class<?>, Object> getControllerFactory() {
        return fxmlLoader.getControllerFactory();
    }

    public void setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
        fxmlLoader.setControllerFactory(controllerFactory);
    }

    @CallerSensitive
    public <T> T load() throws IOException {
        return fxmlLoader.load();
    }

    public Charset getCharset() {
        return fxmlLoader.getCharset();
    }

    public void setCharset(Charset charset) {
        fxmlLoader.setCharset(charset);
    }

    public ObservableMap<String, Object> getNamespace() {
        return fxmlLoader.getNamespace();
    }

    @CallerSensitive
    public ClassLoader getClassLoader() {
        return fxmlLoader.getClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        fxmlLoader.setClassLoader(classLoader);
    }

    public BuilderFactory getBuilderFactory() {
        return fxmlLoader.getBuilderFactory();
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        fxmlLoader.setBuilderFactory(builderFactory);
    }
}
