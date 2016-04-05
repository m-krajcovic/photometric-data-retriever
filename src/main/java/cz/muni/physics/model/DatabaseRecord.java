package cz.muni.physics.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

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
    private Pattern sesamePattern;
    private Set<String> sesameVariables;

    public DatabaseRecord(String name, String URL, Plugin plugin, String sesameAlias) {
        this.name = new SimpleStringProperty(name);
        this.URL = new SimpleStringProperty(URL);
        this.plugin = plugin;
        this.sesameAlias = new SimpleStringProperty(sesameAlias);
        derivateSesameVariables();
    }

    public DatabaseRecord() {
    }

    private Set<String> findNamedGroups(){
        Set<String> namedGroups = new TreeSet<>();
        String groupName = "";
        char[] chars = getSesameAlias().toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == '(' && chars[i+1] == '?' && chars[i + 2] == '<') {
                i+=3;
                for (int j = i; j < chars.length; j++) {
                    if (chars[j] == '>') {
                        namedGroups.add(groupName);
                        groupName = "";
                        break;
                    }
                    groupName += chars[j];
                    i++;
                }
            }
        }
        return namedGroups;
    }

    private void derivateSesameVariables(){
        this.sesameVariables = findNamedGroups();
        sesamePattern = Pattern.compile(getSesameAlias());
    }

    public Pattern getSesamePattern() {
        return sesamePattern;
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
        derivateSesameVariables();
    }

    public Set<String> getSesameVariables() {
        return sesameVariables;
    }

    @Override
    public String toString() {
        return "DatabaseRecord{" +
                "name=" + name +
                ", URL=" + URL +
                ", plugin=" + plugin +
                ", sesameAlias=" + sesameAlias +
                '}';
    }
}
