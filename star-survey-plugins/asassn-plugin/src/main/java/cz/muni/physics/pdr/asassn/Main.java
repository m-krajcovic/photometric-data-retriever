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
//        https://asas-sn.osu.edu/variables?utf8=%E2%9C%93&ra=93.058&dec=-69.766&radius=0.5&meanmag_min=0&meanmag_max=20&amplitude_min=0&amplitude_max=10&period_min=0&period_max=1000&curve_type%5B%5D=CWA&curve_type%5B%5D=CWB&curve_type%5B%5D=DCEP&curve_type%5B%5D=DCEPS&curve_type%5B%5D=DSCT&curve_type%5B%5D=EA&curve_type%5B%5D=EA%7CEB&curve_type%5B%5D=EW&curve_type%5B%5D=GCAS&curve_type%5B%5D=HADS&curve_type%5B%5D=L&curve_type%5B%5D=L%3A&curve_type%5B%5D=M&curve_type%5B%5D=RRAB&curve_type%5B%5D=RRC&curve_type%5B%5D=RRD&curve_type%5B%5D=SR&curve_type%5B%5D=SRS&curve_type%5B%5D=VAR&curve_type%5B%5D=YSO&curve_type%5B%5D=RVA&curve_type%5B%5D=ROT&curve_type%5B%5D=AHB1&curve_type%5B%5D=RRAB%2FBL&curve_type%5B%5D=RRC%2FBL&sort_type=lcnumber&sort_option=asc&commit=Search
//        args = new String[]{"93.058", "-69.766"};
        URL url;
        if (args.length == 2) { // su to suradnice
            url = new URL(
                    MessageFormat.format("https://asas-sn.osu.edu/variables?utf8=%E2%9C%93&ra={0}&dec={1}&radius=0.5&meanmag_min=0&meanmag_max=20&amplitude_min=0&amplitude_max=10&period_min=0&period_max=1000&curve_type%5B%5D=CWA&curve_type%5B%5D=CWB&curve_type%5B%5D=DCEP&curve_type%5B%5D=DCEPS&curve_type%5B%5D=DSCT&curve_type%5B%5D=EA&curve_type%5B%5D=EA%7CEB&curve_type%5B%5D=EW&curve_type%5B%5D=GCAS&curve_type%5B%5D=HADS&curve_type%5B%5D=L&curve_type%5B%5D=L%3A&curve_type%5B%5D=M&curve_type%5B%5D=RRAB&curve_type%5B%5D=RRC&curve_type%5B%5D=RRD&curve_type%5B%5D=SR&curve_type%5B%5D=SRS&curve_type%5B%5D=VAR&curve_type%5B%5D=YSO&curve_type%5B%5D=RVA&curve_type%5B%5D=ROT&curve_type%5B%5D=AHB1&curve_type%5B%5D=RRAB%2FBL&curve_type%5B%5D=RRC%2FBL&sort_type=lcnumber&sort_option=asc&commit=Search", args[0], args[1]));
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
