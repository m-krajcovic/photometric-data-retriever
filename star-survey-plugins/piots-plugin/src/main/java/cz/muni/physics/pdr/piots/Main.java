package cz.muni.physics.pdr.piots;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String rah;
        String decdeg;
        if (args.length == 2) {
            rah = args[0];
            decdeg = args[1];
        } else {
            return;
        }

        String searchUrl = "http://grb2.pi.ncbj.gov.pl/pi/db/public/2006_2009/pi/starsAround.php?db=Pi%20of%20the%20Sky&ra=" + rah + "&dec=" + decdeg + "&rad=0.01";

        for (Element a : Jsoup.connect(searchUrl).get().getElementsByTag("a")) {
            String starId = a.text();

            Document doc = Jsoup.connect("http://grb2.pi.ncbj.gov.pl/pi/db/public/2006_2009/pi/starView.php?&starId=" + starId).get();
            String hjdCurveMin = doc.select("input[name=hjdCurveMin]").first().val();
            String hjdCUrveMax = doc.select("input[name=hjdCUrveMax]").first().val();
            String mCurveMax = doc.select("input[name=mCurveMax]").first().val();
            String mCurveMin = doc.select("input[name=mCurveMin]").first().val();


            String output =
                    Jsoup.connect("http://grb2.pi.ncbj.gov.pl/pi/db/public/2006_2009/pi/lightCurveData.php")
                            .data("starId", starId)
                            .data("hjdCurveMin", hjdCurveMin)
                            .data("hjdCurveMax", hjdCUrveMax)
                            .data("mCurveMin", mCurveMin)
                            .data("mCurveMax", mCurveMax)
                            .data("period", "")
                            .data("merge", "")
                            .data("grade", "")
                            .data("field", "")
                            .data("borderDistance", "")
                            .data("custom", "1")
                            .data("nativeField", "")
                            .data("skipBigStar", "true")
                            .data("skipShutter", "true")
                            .data("skipHotPixel", "true")
                            .data("skipStrongBackground", "true")
                            .data("skipU_", "true")
                            .data("skip0", "true")
                            .data("skip1", "")
                            .data("clamping0", "true")
                            .data("clamping1", "")
                            .data("type", "CSVfull").method(Connection.Method.GET).execute().body();

            List<String> result = Arrays.stream(output.split("\n")).filter(s -> !s.isEmpty() && !s.startsWith("#") && !s.startsWith("$")).collect(Collectors.toList());

            for (String s : result) {
                String[] splits = s.split(";");
                double jd = Double.parseDouble(splits[0]) + 2453250;
                String mag = splits[1].trim();
                String error = "0";
                System.out.println(Double.toString(jd) + "," + mag + "," + error);
            }
        }
    }
}
