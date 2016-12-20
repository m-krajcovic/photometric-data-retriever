package cz.muni.physics.pdr.macho;

import cz.muni.physics.pdr.java.PluginUtils;
import cz.muni.physics.pdr.vizier.VizierHtmlParser;
import cz.muni.physics.pdr.vizier.VizierService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Michal
 * @version 1.0
 * @since 8/19/2016
 */
public class Main {
    public static void main(String[] args) throws IOException {
        VizierService vizierService = new VizierService();
        VizierHtmlParser vizierHtmlParser = new VizierHtmlParser();
        if (args.length >= 1) {
            String query = args[0];
            Document parse = vizierService.getConnectionFromVizier("J/AJ/134/1963/EBs", VizierService.OutputType.HTML)
                    .data("LC", "LC")
                    .data("-out", "MACHO")
                    .data("-out", "LC")
                    .data("-c", query).method(Connection.Method.POST).execute().parse();
            List<Map<String, String>> results = vizierHtmlParser.getResults(parse);
            for (Map<String, String> result : results) {
                if (result.containsKey("LC")) {
                    String url = result.get("LC").trim();
                    if (!url.isEmpty()) {
                        Document document = Jsoup.connect(url).get();
                        Elements a = document.getElementsByAttributeValueStarting("href", "/viz-bin/nph-Plot/Vgraph/txt");
                        if (a != null) {
                            String href = a.first().attr("abs:href")
                                    .replace("Calibrated", "Instrumental")
                                    .replaceFirst("&P=[0-9\\.]*(&?)", "&P=0.0$1");
                            URL hrefUrl = new URL(href);
                            String filter = "R";
                            try (InputStream is = PluginUtils.copyUrlOpenStream(hrefUrl, "MACHO-"+result.get("MACHO") + "-" + filter + ".txt", 10);
                                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                                reader.readLine();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    line = line.trim();
                                    if (line.startsWith("#")) {
                                        filter = "B";
                                        continue;
                                    }
                                    String[] cols = line.split("\\s+");
                                    double jd;
                                    try {
                                        jd = Double.parseDouble(cols[0].trim()) + 2440000.5;
                                    } catch (Exception exc) {
                                        continue;
                                    }
                                    String mag = cols[1];
                                    String err = cols[2].trim();
                                    System.out.println(jd + "," + mag + "," + err + "," + result.get("MACHO") + "-" + filter);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
