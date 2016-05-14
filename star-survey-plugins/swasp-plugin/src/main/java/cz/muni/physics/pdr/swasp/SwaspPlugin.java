package cz.muni.physics.pdr.swasp;

import au.com.bytecode.opencsv.CSVReader;
import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.java.Plugin;
import cz.muni.physics.pdr.java.PluginUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class SwaspPlugin implements Plugin {

    @Override
    public List<PhotometricData> getDataFromUrl(String url) {
        List<PhotometricData> result = new ArrayList<>();
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return result;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(PluginUtils.copyUrlOpenStream(u,"SWASP-" + PluginUtils.getQueryMap(u.getQuery()).getOrDefault("object", "-" + System.currentTimeMillis()) + ".csv", 3)));
             CSVReader reader = new CSVReader(in)) {
            String[] nextLine = reader.readNext();
            if (nextLine == null) return result;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 4) {
                    PhotometricData data = new PhotometricData(nextLine[0], nextLine[2], nextLine[3], nextLine[1]);
                    result.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
