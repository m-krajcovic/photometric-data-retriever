package cz.muni.physics.pdr.backend.entity;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class StellarObjectName {
    private String value;

    public StellarObjectName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StellarObjectName stellarObjectName = (StellarObjectName) o;

        return getValue() != null ? getValue().equals(stellarObjectName.getValue()) : stellarObjectName.getValue() == null;

    }

    @Override
    public int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }
}
