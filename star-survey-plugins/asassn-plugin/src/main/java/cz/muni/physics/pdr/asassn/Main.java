package cz.muni.physics.pdr.asassn;

import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {

    public static void main(String[] args) throws IOException {
//        args = new String[]{"15.18808", "26.80683"};
        URL url;
        if (args.length == 2) { // su to suradnice
            url = new URL(
                    MessageFormat.format("https://asas-sn.osu.edu/variables?utf8=%E2%9C%93&ra={0}&dec={1}&radius=0.5&mean_vmag_min=0.0&mean_vmag_max=20.0&amplitude_min=0.0&amplitude_max=10.0&period_min=0.0&period_max=1000.0&sllk_statistic_min=0.0&sllk_statistic_max=1.5&class_probability_min=0.0&class_probability_max=1.0&rfr_score_min=0.0&rfr_score_max=1.0&curve_type%5B%5D=EB&curve_type%5B%5D=EW&references%5B%5D=The+ASAS-SN+Catalog+of+Variable+Stars%3A+II&references%5B%5D=The+ASAS-SN+Catalog+of+Variable+Stars%3A+I&sort_type=lcid&sort_option=asc&commit=Search", args[0], args[1]));
        } else {
            return;
        }
        Document doc = Jsoup.connect(url.toString()).get();
        Element table = doc.getElementsByTag("table").first();
        Element anchor = table.getElementsByTag("a").first();
        String href = anchor.attr("href");

        Document curve = Jsoup.connect("https://asas-sn.osu.edu" + href).get();
        Elements anchors = curve.getElementsByTag("a");
        for (Element element : anchors) {
            if (element.text().equals("Download Data")) {
                String dataLink = element.attr("href");
                URL dataUrl = new URL("https://asas-sn.osu.edu" + dataLink);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(PluginUtils.copyUrlOpenStream(dataUrl, "ASASSN-"  + System.currentTimeMillis() + ".dat", 3)))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (!line.trim().startsWith("#")) {
                            String[] split = line.split("\\s+");
                            // 0 - hjd, 1 - cam, 2 - mag, 3 - err
                            String hjd = split[0];
                            String cam = split[1];
                            String mag = split[2];
                            String err = split[3];

                            if (!mag.startsWith(">")) {
                                System.out.println(hjd + "," + mag + "," + err + "," + cam);
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}
