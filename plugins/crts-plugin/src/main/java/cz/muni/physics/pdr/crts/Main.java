package cz.muni.physics.pdr.crts;

import cz.muni.physics.pdr.java.Plugin;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class Main {
    public static void main(String[] args) {
        String url = args[0];
        Plugin plugin = new CrtsPlugin();
        plugin.getDataFromUrl(url).forEach(d -> System.out.println(d.getJulianDate() + "," + d.getMagnitude() + "," + d.getError()));
    }
}
