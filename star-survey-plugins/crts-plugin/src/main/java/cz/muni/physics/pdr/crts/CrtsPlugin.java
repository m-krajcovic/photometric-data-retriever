package cz.muni.physics.pdr.crts;

import au.com.bytecode.opencsv.CSVReader;
import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.java.Plugin;
import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
 * @since 25/03/16
 */
public class CrtsPlugin implements Plugin {

    @Override
    public List<PhotometricData> getDataFromUrl(String url) {
        List<PhotometricData> result = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        Element downloadAnchor = doc.getElementsMatchingOwnText("download").first();
        URL csvUrl;
        try {
            csvUrl = new URL(downloadAnchor.attr("abs:href"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return result;
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(PluginUtils.copyUrlOpenStream(csvUrl, "CRTS-" + System.currentTimeMillis() + ".csv", 3)));
             CSVReader reader = new CSVReader(in)) {
            try {
                String[] nextLine = reader.readNext();

                if (nextLine == null) return null;
                while ((nextLine = reader.readNext()) != null) {
                    Double julianDate = Double.parseDouble(nextLine[5]) + 2400000.5;
                    PhotometricData data = new PhotometricData(julianDate.toString(), nextLine[1], nextLine[2]);
                    result.add(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
