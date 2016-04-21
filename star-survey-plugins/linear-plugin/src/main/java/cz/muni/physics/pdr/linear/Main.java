package cz.muni.physics.pdr.linear;

import cz.muni.physics.pdr.java.PhotometricData;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String objID = "";
        if (args.length == 1) { //id
            objID = args[0];
        } else if (args.length == 2) { // radeg dedeg
            Connection connection = Jsoup.connect("https://astroweb.lanl.gov/lineardb/user_sql_query_result");
            connection.timeout(10000)
//                .data("sql", MessageFormat.format("SELECT \"objectID\" FROM nearby_obj_circle({0}, {1}, 2) LIMIT 50000", args[0], args[1]))
                    .data("sql", "SELECT \"objectID\" from object limit 1")
                    .data("output", "CSV File");
            Document document = connection.post();
            String output = document.body().text();
            String[] split = output.split(" ");
            if (split.length == 2) {
                try {
                    Double.parseDouble(split[1]);
                    objID = split[1];
                } catch (NumberFormatException e) {
                    return;
                }
            }
        } else {
            return;
        }
        String objUrl = "https://astroweb.lanl.gov/lineardb/object_data?objectID=" + objID;
        Document doc = Jsoup.connect(objUrl).timeout(10000).get();
        String scriptOutput = doc.getElementsByTag("script").html();
        Pattern p1 = Pattern.compile("var mjd = \\[(.*)\\]");
        Pattern p2 = Pattern.compile("var mag = \\[(.*)\\]");
        Pattern p3 = Pattern.compile("var err = \\[(.*)\\]");
        Matcher mjd = p1.matcher(scriptOutput);
        Matcher mag = p2.matcher(scriptOutput);
        Matcher err = p3.matcher(scriptOutput);
        String[] mjds;
        String[] mags;
        String[] errs;
        if (mjd.find() && mag.find() && err.find()) {
            mjds = mjd.group(1).split(", ");
            mags = mag.group(1).split(", ");
            errs = err.group(1).split(", ");
            for (int i = 0; i < mjds.length; i++) {
                mjds[i] = Double.toString(Double.parseDouble(mjds[i]) + 2400000.5);
                PhotometricData data = new PhotometricData(mjds[i], mags[i], errs[i]);
                System.out.println(data.toCsv());
            }
        }
    }
}
