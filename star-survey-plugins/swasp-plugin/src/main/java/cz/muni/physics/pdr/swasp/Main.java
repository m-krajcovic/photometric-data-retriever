package cz.muni.physics.pdr.swasp;

import au.com.bytecode.opencsv.CSVReader;
import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.java.Plugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 12/04/16
 */
public class Main {
    public static void main(String[] args) {
        URL u;
        try {
            u = new URL(args[0]); // je to url
        } catch (MalformedURLException e) {
            List<PhotometricData> result = new ArrayList<>();
            try {
                BufferedReader in;
                try {
                    u = new URL("http://wasp.cerit-sc.cz/json?type=CSV&object=" + URLEncoder.encode(args[0], "UTF-8"));
                } catch (MalformedURLException e1) {
                    System.err.println("wat");
                    return;
                }
                in = new BufferedReader(new InputStreamReader(u.openStream()));
                CSVReader reader = new CSVReader(in);
                String[] nextLine = reader.readNext();
                if (nextLine == null) return;
                while ((nextLine = reader.readNext()) != null) {
                    PhotometricData data = new PhotometricData(nextLine[0], nextLine[2], nextLine[3]);
                    result.add(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
            result.forEach(d -> System.out.println(d.getJulianDate() + "," + d.getMagnitude() + "," + d.getError()));
            return;
        }


        class SwaspPlugin implements Plugin {

            @Override
            public List<PhotometricData> getDataFromUrl(String url) {
                List<PhotometricData> result = new ArrayList<>();
                URL u = null;
                try {
                    u = new URL(url);
                } catch (MalformedURLException e) {
                    return result;
                }
                String query = u.getQuery();
                Map<String, String> parameters = new HashMap<>();
                String[] params = query.split("&");
                for (String param : params) {
                    String[] split = param.split("=");
                    parameters.put(split[0], split[1]);
                }
                if (!parameters.containsKey("object")) {
                    Document doc;
                    try {
                        doc = Jsoup.connect(url).get();
                    } catch (IOException e) {
                        return result;
                    }
                    for (Element a : doc.getElementsByTag("a")) {
                        if (a.ownText().equals("CSV")) {
                            try {
                                u = new URL("http://wasp.cerit-sc.cz" + a.attr("href"));
                            } catch (MalformedURLException e) {
                                return result;
                            }
                        }
                    }
                }

                BufferedReader in;
                try {
                    in = new BufferedReader(new InputStreamReader(u.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return result;
                }
                CSVReader reader = new CSVReader(in);
                try {
                    String[] nextLine = reader.readNext();
                    if (nextLine == null) return result;
                    while ((nextLine = reader.readNext()) != null) {
                        PhotometricData data = new PhotometricData(nextLine[0], nextLine[2], nextLine[3]);
                        result.add(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }


        String url = args[0];
        Plugin plugin = new SwaspPlugin();
        plugin.getDataFromUrl(url).forEach(d -> System.out.println(d.getJulianDate() + "," + d.getMagnitude() + "," + d.getError()));
        System.err.println("Finished swasp");
    }
}
