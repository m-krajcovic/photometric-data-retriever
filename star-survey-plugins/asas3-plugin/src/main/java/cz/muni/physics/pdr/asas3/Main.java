package cz.muni.physics.pdr.asas3;

import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

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
        String id = "";
        Main main = new Main();
        if (args[0].equals("-n")) {
            id = main.getIdFromForm(args[1]);
        } else if (args[0].equals("-i")) {
            id = args[1];
        } else if (args[0].equals("-c")) {
            String rah = args[1];
            String decdeg = args[2];
            id = main.getIdFromForm(rah + " " + decdeg);
        }
        if (id.startsWith("J")) {
            id = id.substring(1);
        }
        main.readData("https://www.astrouw.edu.pl/cgi-asas/asas_cgi_get_data?" + id + ",asas3");
    }

    public String getIdFromForm(String query) {
        WebDriver driver = new HtmlUnitDriver();
        driver.get("https://www.astrouw.edu.pl/asas/i_aasc/aasc_form.php?catsrc=asas3");
        driver.findElement(By.name("coo")).sendKeys(query);
        driver.findElement(By.name("submit")).submit();
        driver.close();
        driver.getWindowHandles().forEach(w -> {
            driver.switchTo().window(w);
        });
        Document doc = Jsoup.parse(driver.getPageSource());
        String href = doc.getElementsByTag("a").first().attr("href");
        return href.split("/")[3].split(",")[0];
    }

    private void readData(String urlString) throws IOException {
        URL url = new URL(urlString);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(PluginUtils.copyUrlOpenStream(url, "ASAS3-" + url.getQuery().split(",")[0] + ".txt", 3)))) {
            String line;
            List<AsasData> dataList = new ArrayList<>();
            double[] avgErr = new double[5];
            Arrays.fill(avgErr, 0);
            while ((line = in.readLine()) != null) {
                System.out.println(line);
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
