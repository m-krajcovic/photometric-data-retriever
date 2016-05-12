package cz.muni.physics.pdr.backend.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class StellarObject {
    private List<String> names = new ArrayList<>();
    private Map<String, String> ids = new HashMap<>();
    private double rightAscension;
    private double declination;
    private double epoch;
    private double period;
    private double distance;

    public double getDeclinationInHours() {
        return declination / 15;
    }

    public double getRightAscensionInHours() {
        return rightAscension / 15;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public Map<String, String> getIds() {
        return ids;
    }

    public void setIds(Map<String, String> ids) {
        this.ids = ids;
    }

    public double getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public double getDeclination() {
        return declination;
    }

    public void setDeclination(double declination) {
        this.declination = declination;
    }

    public double getEpoch() {
        return epoch;
    }

    public void setEpoch(double epoch) {
        this.epoch = epoch;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "StellarObject{" +
                "names=[ " + String.join(", ", names) +
                " ], rightAscension='" + rightAscension + '\'' +
                ", declination='" + declination + '\'' +
                ", epoch='" + epoch + '\'' +
                ", period='" + period + '\'' +
                ", distance= '" + distance + '\'' +
                '}';
    }

    public String toLines() {
        String output = "";
        output += String.join("\n", names);
        for (Map.Entry<String, String> id : ids.entrySet()) {
            output += id.getKey() + ":" + id.getValue() + "\n";
        }
        output += rightAscension + "\n"
                + declination + "\n"
                + period + "\n"
                + epoch + "\n";
        return output;
    }
}
