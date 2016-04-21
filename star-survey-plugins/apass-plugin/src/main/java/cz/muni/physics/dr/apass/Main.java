package cz.muni.physics.dr.apass;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.readData(args);
    }

    private void readData(String... args) throws IOException {
        URL url;
        if (args.length == 1) { // je to url
            url = new URL(args[0]);
        } else if (args.length == 2) { // su to suradnice
            url = new URL(
                    MessageFormat.format("http://tombstone.physics.mcmaster.ca/APASS/conesearch_offset.php?radeg={0}&decdeg={1}&raddeg=0.005", args[0], args[1]));
        } else {
            return;
        }
        Document doc = Jsoup.connect(url.toString()).get();
        Element text = doc.getElementsByTag("font").first();
        String[] lines = text.html().split("<br>");
        for (String line : lines) {
            if (line.startsWith("#")) continue;
            String[] csv = line.split("#")[0].split(",");
            if (csv.length == 3) {
                csv[0] = Double.toString(Double.parseDouble(csv[0]) + 2400000.5);
                System.out.println(String.join(",", csv));
            }
        }
    }
}
