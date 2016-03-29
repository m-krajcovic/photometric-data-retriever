package cz.muni.physics.java;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 25/03/16
 */
public class PluginDescription {
    private String name;
    private String URL;
    private String aliasPattern;

    public PluginDescription(String name, String URL, String aliasPattern) {
        this.name = name;
        this.URL = URL;
        this.aliasPattern = aliasPattern;
    }

    public String getAliasPattern() {
        return aliasPattern;
    }

    public void setAliasPattern(String aliasPattern) {
        this.aliasPattern = aliasPattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
