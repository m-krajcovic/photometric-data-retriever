package cz.muni.physics.sesame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 27/03/16
 */
public class SesameResult {
    private List<String> names = new ArrayList<>();
    private String jpos;
    private String jraddeg;
    private String jdedeg;

    public String getJdedeg() {
        return jdedeg;
    }

    public void setJdedeg(String jdedeg) {
        this.jdedeg = jdedeg;
    }

    public String getJraddeg() {
        return jraddeg;
    }

    public void setJraddeg(String jraddeg) {
        this.jraddeg = jraddeg;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getJpos() {
        return jpos;
    }

    public void setJpos(String jpos) {
        this.jpos = jpos;
    }
}
