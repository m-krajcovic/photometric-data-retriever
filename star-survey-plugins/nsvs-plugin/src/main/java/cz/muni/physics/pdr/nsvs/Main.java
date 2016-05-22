package cz.muni.physics.pdr.nsvs;

import cz.muni.physics.pdr.java.PhotometricData;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 31/03/16
 */
public class Main {
    public static void main(String[] args) {
        List<String> ids = new ArrayList<>();
        if (args.length == 1) {
            ids.add(args[0]);
        } else if (args.length == 2) {
            String ra = args[0];
            String dec = args[1];
            String url = "http://skydot.lanl.gov/nsvs/cone_search.php?ra=" + ra + "&dec=" + dec + "&rad=0.5&saturated=on&apincompl=on&nocorr=on&hiscat=on&hicorr=on";
            try {
                Jsoup.connect(url).get().getElementsByTag("a").forEach(a -> ids.add(a.text()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        ids.forEach(Main::readData);
    }

    public static void readData(String objId) {
        Document doc;
        String url = "http://skydot.lanl.gov/nsvs/star.php?num=" + objId + "&amp;mask=6420";
        try {
            Jsoup.connect(url).get();
            Connection.Response res = Jsoup.connect(url).execute();
            doc = Jsoup.connect("http://skydot.lanl.gov/nsvs/print_light_curve.php").cookies(res.cookies()).get();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Elements trs = doc.getElementsByTag("tr");
        trs.remove(0);
        for (Element tr : trs) {
            Double julianDate = Double.parseDouble(tr.child(0).text()) + 2450000.5;
            PhotometricData data = new PhotometricData(julianDate.toString(), tr.child(1).text(), tr.child(2).text(), objId);
            System.out.println(data.toCsv());
        }
    }
}
