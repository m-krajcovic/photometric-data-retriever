package cz.muni.physics.pdr.app.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 22/04/16
 */
@Component
public class PreferencesHolder {

    private Preferences userPreferences = Preferences.userRoot().node(PreferencesHolder.class.getName());

    @Autowired
    private Environment environment;

    public String get(String key) {
        return userPreferences.get(key, environment.getProperty(key));
    }

    public void putInt(String key, int value) {
        userPreferences.putInt(key, value);
    }

    public void putLong(String key, long value) {
        userPreferences.putLong(key, value);
    }

    public void putBoolean(String key, boolean value) {
        userPreferences.putBoolean(key, value);
    }

    public void putFloat(String key, float value) {
        userPreferences.putFloat(key, value);
    }

    public void putDouble(String key, double value) {
        userPreferences.putDouble(key, value);
    }

    public void putByteArray(String key, byte[] value) {
        userPreferences.putByteArray(key, value);
    }

    public void remove(String key) {
        userPreferences.remove(key);
    }

    public String get(String key, String def) {
        return userPreferences.get(key, def);
    }

    public void put(String key, String value) {
        userPreferences.put(key, value);
    }

    public void clear() throws BackingStoreException {
        userPreferences.clear();
    }

    public int getInt(String key, int def) {
        return userPreferences.getInt(key, def);
    }

    public long getLong(String key, long def) {
        return userPreferences.getLong(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return userPreferences.getBoolean(key, def);
    }

    public float getFloat(String key, float def) {
        return userPreferences.getFloat(key, def);
    }

    public double getDouble(String key, double def) {
        return userPreferences.getDouble(key, def);
    }

    public byte[] getByteArray(String key, byte[] def) {
        return userPreferences.getByteArray(key, def);
    }
}
