package cz.muni.physics.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class DatabaseRecord {
    private StringProperty name;
    private StringProperty URL;
    private Plugin plugin;
    private StringProperty sesameAlias;

    public DatabaseRecord(String name, String URL, Plugin plugin, String sesameAlias) {
        this.name = new SimpleStringProperty(name);
        this.URL = new SimpleStringProperty(URL);
        this.plugin = plugin;
        this.sesameAlias = new SimpleStringProperty(sesameAlias);
    }

    public DatabaseRecord() {
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getURL() {
        return URL.get();
    }

    public StringProperty URLProperty() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL.set(URL);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getSesameAlias() {
        return sesameAlias.get();
    }

    public StringProperty sesameAliasProperty() {
        return sesameAlias;
    }

    public void setSesameAlias(String sesameAlias) {
        this.sesameAlias.set(sesameAlias);
    }
}
