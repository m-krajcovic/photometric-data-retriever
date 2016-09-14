package cz.muni.physics.pdr.ogle2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author Michal
 * @version 1.0
 * @since 7/7/2016
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Main main = new Main();

        if (args.length == 2) {
            Document doc = main.queryOgle(args[0], args[1]);
            Elements anchors = doc.getElementsByTag("a");
            for (Element anchor : anchors) {
                if (anchor.text().equals(args[1])) {
                    main.getData(anchor.parent().previousElementSibling().text(), args[1]);
                }
            }
        }
    }

    public void getData(String field, String starId) throws IOException {
        String url = "http://ogledb.astrouw.edu.pl/~ogle/photdb/getobj.php?field=" + field + "&starid=" + starId;
        Document doc = Jsoup.connect(url).get();
        Element pre = doc.getElementsByTag("pre").first();
        for (String line : pre.text().split("\n")) {
            String[] cols = line.split("\\s");
            System.out.println(cols[0] + "," + cols[1] + "," + cols[2] + "," + field);
        }
    }

    public Document queryOgle(String field, String starId) throws IOException {
        return Jsoup.connect("http://ogledb.astrouw.edu.pl/~ogle/photdb/query.php?qtype=phot&first=1").data(
                "db_target", field.split("[-_]")[0].toLowerCase(),
                "dbtyp", "psf2",
                "sort", "field",
                "use_field", "on",
                "val_field", field.replaceAll("-", "_"),
                "use_starid", "on",
                "val_starid", starId,
                "valmin_x", "",
                "valmax_x", "",
                "valmin_y", "",
                "valmax_y", "",
                "disp_starcat", "on",
                "val_starcat", "",
                "disp_ra", "on",
                "valmin_ra", "",
                "valmax_ra", "",
                "disp_decl", "on",
                "valmin_decl", "",
                "valmax_decl", "",
                "valmin_ngood", "",
                "valmax_ngood", "",
                "disp_pgood", "on",
                "valmin_pgood", "",
                "valmax_pgood", "",
                "disp_imean", "on",
                "valmin_imean", "",
                "valmax_imean", "",
                "disp_imed", "on",
                "valmin_imed", "",
                "valmax_imed", "",
                "disp_isig", "on",
                "valmin_isig", "",
                "valmax_isig", "",
                "disp_imederr", "on",
                "valmin_imederr", "",
                "valmax_imederr", "",
                "disp_ndetect", "on",
                "valmin_ndetect", "",
                "valmax_ndetect", "",
                "query", "",
                "sorting", "ASC",
                "pagelen", "50",
                "maxobj", ""
        ).post();
    }
}
