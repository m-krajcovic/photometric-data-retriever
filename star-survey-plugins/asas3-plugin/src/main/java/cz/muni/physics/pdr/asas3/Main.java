package cz.muni.physics.pdr.asas3;

import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        args = new String[]{"RW Com"};
        if (args.length == 1) {
            String id = "";
            if (args[0].split("\\s").length > 1) {
                Connection data = Jsoup.connect("http://www.astrouw.edu.pl/cgi-asas/asas_cat_input")
                        .method(Connection.Method.POST)
                        .data("source", "asas2")
                        .data("coo", args[0])
                        .data("equinox", "2000")
                        .data("nmin", "4")
                        .data("box", "15")
                        .data("submit", "Search")
                        .followRedirects(true)
                        .ignoreContentType(true);
                Document doc = data.execute().parse();
                System.out.println(doc.html());

                String href = doc.getElementsByTag("a").first().attr("href");
                id = href.split("/")[3].split(",")[0];
            } else {
                id = args[0];
            }
//            main.readData("http://www.astrouw.edu.pl/cgi-asas/asas_cgi_get_data?" + id + ",asas3");
        }
    }

    private void readData(String urlString) throws IOException {
        URL url = new URL(urlString);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(PluginUtils.copyUrlOpenStream(url, "ASAS3-" + url.getQuery().split(",")[0] + ".txt", 3)))) {
            String line;
            List<AsasData> dataList = new ArrayList<>();
            double[] avgErr = new double[5];
            Arrays.fill(avgErr, 0);
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] csv = line.split("\\s+");
                if (csv.length == 14) {
                    if (csv[12].trim().equals("A") || csv[12].trim().equals("B")) {
                        AsasData data = new AsasData(csv);
                        for (int i = 0; i < avgErr.length; i++) {
                            avgErr[i] += data.getErrs()[i];
                        }
                        dataList.add(data);
                    }
                }
            }
            int bestColIndex = getIndexOfMin(avgErr);
            for (AsasData asasData : dataList) {
                System.out.println(asasData.getHjd() + "," + asasData.getMags()[bestColIndex] + "," + asasData.getErrs()[bestColIndex]);
            }
        }
    }

    public int getIndexOfMin(double[] data) {
        double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < data.length; i++) {
            Double d = data[i];
            if (Double.compare(d, min) < 0) {
                min = d;
                index = i;
            }
        }
        return index;
    }

    private String searchFor(String text) {
        //http://www.astrouw.edu.pl/asas/i_aasc/aasc_form.php?catsrc=asas3
        return "";
    }

    private class AsasData {
        private double hjd;
        private double[] mags = new double[5];
        private double[] errs = new double[5];

        private AsasData(String[] csv) {
            this.hjd = Double.parseDouble(csv[1]) + 2450000;
            for (int i = 0; i < 5; i++) {
                mags[i] = Double.parseDouble(csv[i + 2]);
                errs[i] = Double.parseDouble(csv[i + 7]);
            }
        }

        public double getHjd() {
            return hjd;
        }

        public void setHjd(double hjd) {
            this.hjd = hjd;
        }

        public double[] getMags() {
            return mags;
        }

        public void setMags(double[] mags) {
            this.mags = mags;
        }

        public double[] getErrs() {
            return errs;
        }

        public void setErrs(double[] errs) {
            this.errs = errs;
        }
    }
}
