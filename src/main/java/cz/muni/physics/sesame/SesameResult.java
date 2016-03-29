package cz.muni.physics.sesame;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 27/03/16
 */
public class SesameResult {
    private List<String> names;
    private String J20000;
    private String B19500;

    public String getB19500() {
        return B19500;
    }

    public void setB19500(String b19500) {
        B19500 = b19500;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getJ20000() {
        return J20000;
    }

    public void setJ20000(String j20000) {
        J20000 = j20000;
    }
}
