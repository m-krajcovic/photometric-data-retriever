package cz.muni.physics.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 26/03/16
 */
public class PropUtils {

    private static final Properties props = new Properties();
    static {
        try {
            try (InputStream in = PropUtils.class.getResourceAsStream("/application.properties")) {
                props.load(in);
                props.putAll(System.getProperties());
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load application.properties");
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
