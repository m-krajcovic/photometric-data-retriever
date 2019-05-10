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
//        args = new String[]{"79.379833", "20.132083"};
        //79.379833, 20.132083
        URL url;
        if (args.length == 2) { // su to suradnice
            url = new URL(
                    // https://asas-sn.osu.edu/variables?utf8=%E2%9C%93&ra={0}&dec={1}&radius=0.5&mean_vmag_min=&mean_vmag_max=&amplitude_min=&amplitude_max=&period_min=&period_max=&sllk_statistic_min=&sllk_statistic_max=&class_probability_min=&class_probability_max=&rfr_score_min=&rfr_score_max=&curve_type[]=ROT&curve_type[]=DSCT&curve_type[]=HADS&curve_type[]=CWA&curve_type[]=CWB&curve_type[]=DCEP&curve_type[]=DCEPS&curve_type[]=RVA&curve_type[]=M&curve_type[]=EA&curve_type[]=EB&curve_type[]=EW&curve_type[]=ELL&curve_type[]=YSO&curve_type[]=L&curve_type[]=GCAS&curve_type[]=SR&curve_type[]=SRD&curve_type[]=LSP&curve_type[]=GCAS%3A&curve_type[]=ROT%3A&curve_type[]=SR%3A&curve_type[]=VAR&curve_type[]=RRAB%3A&curve_type[]=DSCT%3A&curve_type[]=M%3A&curve_type[]=RRAB&curve_type[]=RRC&curve_type[]=RRD&curve_type[]=RCB&curve_type[]=RCB%3A&curve_type[]=DYPer&curve_type[]=CV&curve_type[]=CV%2BE&curve_type[]=CV%3A&curve_type[]=UG&curve_type[]=UGER&curve_type[]=UGSS&curve_type[]=UGSU&curve_type[]=UGSU%2BE&curve_type[]=UGSU%3A&curve_type[]=UGWZ&curve_type[]=UGZ&curve_type[]=AM&curve_type[]=AM%2BE&curve_type[]=AM%3A&curve_type[]=DQ&curve_type[]=DQ%3A&curve_type[]=UV&curve_type[]=UV%3A&curve_type[]=SXARI&curve_type[]=SXARI%3A&curve_type[]=HB&curve_type[]=R&curve_type[]=SXPHE&curve_type[]=PPN&curve_type[]=PVTELI&curve_type[]=V1093HER&curve_type[]=V361HYA&curve_type[]=V838MON&curve_type[]=WR&curve_type[]=ZAND&curve_type[]=SDOR&curve_type[]=SDOR%3A&curve_type[]=HMXB&curve_type[]=LMXB&curve_type[]=ZZ&curve_type[]=ZZ%3A&curve_type[]=ZZA&curve_type[]=ZZB&curve_type[]=ZZLep&curve_type[]=ZZO&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20II&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20I&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20IV&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20III&sort_type=lcid&sort_option=asc&
                    MessageFormat.format("https://asas-sn.osu.edu/variables?utf8=%E2%9C%93&ra={0}&dec={1}&radius=0.5&mean_vmag_min=&mean_vmag_max=&amplitude_min=&amplitude_max=&period_min=&period_max=&sllk_statistic_min=&sllk_statistic_max=&class_probability_min=&class_probability_max=&rfr_score_min=&rfr_score_max=&curve_type[]=ROT&curve_type[]=DSCT&curve_type[]=HADS&curve_type[]=CWA&curve_type[]=CWB&curve_type[]=DCEP&curve_type[]=DCEPS&curve_type[]=RVA&curve_type[]=M&curve_type[]=EA&curve_type[]=EB&curve_type[]=EW&curve_type[]=ELL&curve_type[]=YSO&curve_type[]=L&curve_type[]=GCAS&curve_type[]=SR&curve_type[]=SRD&curve_type[]=LSP&curve_type[]=GCAS%3A&curve_type[]=ROT%3A&curve_type[]=SR%3A&curve_type[]=VAR&curve_type[]=RRAB%3A&curve_type[]=DSCT%3A&curve_type[]=M%3A&curve_type[]=RRAB&curve_type[]=RRC&curve_type[]=RRD&curve_type[]=RCB&curve_type[]=RCB%3A&curve_type[]=DYPer&curve_type[]=CV&curve_type[]=CV%2BE&curve_type[]=CV%3A&curve_type[]=UG&curve_type[]=UGER&curve_type[]=UGSS&curve_type[]=UGSU&curve_type[]=UGSU%2BE&curve_type[]=UGSU%3A&curve_type[]=UGWZ&curve_type[]=UGZ&curve_type[]=AM&curve_type[]=AM%2BE&curve_type[]=AM%3A&curve_type[]=DQ&curve_type[]=DQ%3A&curve_type[]=UV&curve_type[]=UV%3A&curve_type[]=SXARI&curve_type[]=SXARI%3A&curve_type[]=HB&curve_type[]=R&curve_type[]=SXPHE&curve_type[]=PPN&curve_type[]=PVTELI&curve_type[]=V1093HER&curve_type[]=V361HYA&curve_type[]=V838MON&curve_type[]=WR&curve_type[]=ZAND&curve_type[]=SDOR&curve_type[]=SDOR%3A&curve_type[]=HMXB&curve_type[]=LMXB&curve_type[]=ZZ&curve_type[]=ZZ%3A&curve_type[]=ZZA&curve_type[]=ZZB&curve_type[]=ZZLep&curve_type[]=ZZO&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20II&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20I&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20IV&references[]=The%20ASAS-SN%20Catalog%20of%20Variable%20Stars:%20III&sort_type=lcid&sort_option=asc&", args[0], args[1]));
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
