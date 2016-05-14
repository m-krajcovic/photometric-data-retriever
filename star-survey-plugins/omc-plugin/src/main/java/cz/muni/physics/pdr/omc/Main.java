package cz.muni.physics.pdr.omc;

import cz.muni.physics.pdr.java.PluginUtils;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.TableHDU;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {
    public static void main(String[] args) throws IOException, FitsException {
        Main main = new Main();
        main.readData(args);
    }

    private void readData(String... args) throws IOException, FitsException {
        String searchFormUrl = "https://sdc.cab.inta-csic.es/omc/secure/form_busqueda.jsp?resetForm=true";
        String searchUrl = "https://sdc.cab.inta-csic.es/omc/secure/form_busqueda.jsp";
        Connection.Response form = Jsoup.connect(searchFormUrl).method(Connection.Method.GET).execute();
        Connection post = Jsoup.connect(searchUrl)
                .data("cookieexists", "false")
                .data("submit", "Submit")
                .data("lct_tstep", "630")
                .data("lct_wcsflag", "Y")
                .data("obj_sstar", "S")
                .data("crv_numpoints", "1")
                .data("obj_prio", "1")
                .data("otuput_format", "html")
                .data("results_per_page", "50")
                .data("page_to_show", "1")
                .cookies(form.cookies());

        if (args.length == 1) { // je to nazov
            post.data("obj_id", args[0]);
        } else if (args.length == 2) { // su to suradnice
            post.data("ra", args[0])
                    .data("de", args[1])
                    .data("rad", "1");
        } else {
            return;
        }
        Document doc = post.post();
        Element fetchAnchor = doc.getElementsByAttributeValueStarting("href", "fetch_lcurve.jsp?obj_id=").first();
        String href = fetchAnchor.attr("href");
        URL fetchUrl = new URL("https://sdc.cab.inta-csic.es/omc/secure/" + href);
        try (InputStream is = PluginUtils.copyUrlOpenStream(fetchUrl, "OMC-" + PluginUtils.getQueryMap(fetchUrl.getQuery()).getOrDefault("obj_id", "-" + System.currentTimeMillis()) + ".fits", 3)) {
            Fits fits = new Fits(is);
            TableHDU table = (TableHDU) fits.getHDU(1);
            float[] mags = (float[]) table.getColumn("MAG_V");
            float[] errs = (float[]) table.getColumn("ERRMAG_V");
            double[] barytimes = (double[]) table.getColumn("BARYTIME");
            double[] telapses = (double[]) table.getColumn("TELAPSE");

            for (int i = 0; i < mags.length; i++) {
                double jd = barytimes[i] + 2451544.5 + telapses[i] / 2 / 86400;
                System.out.println(jd + "," + mags[i] + "," + errs[i]);
            }
        }

    }
}
