package cz.muni.physics.model;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 10/04/16
 */
public abstract class Parameter {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
