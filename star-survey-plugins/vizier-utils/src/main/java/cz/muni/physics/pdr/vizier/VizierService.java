package cz.muni.physics.pdr.vizier;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michal
 * @version 1.0
 * @since 8/19/2016
 */
public class VizierService {

    public Connection getConnectionFromVizier(String catalogue, OutputType outputType) {
        return getConnectionFromVizier(catalogue, outputType, new HashMap<>());
    }

    public Connection getConnectionFromVizier(String catalogue, OutputType outputType, Map<String, String> data) {
        if (!data.containsKey("-out.max")) {
            data.put("-out.max", "50");
        }
        return Jsoup.connect(outputType.getUrl())
                .data("-to", "4")
                .data("-from", "-3")
                .data("-this", "-3")
                .data("source", catalogue)
                .data("tables", catalogue)
                .data("-source", catalogue)
                .data("CDSportal", "http://cdsportal.u-strasbg.fr/StoreVizierData.html")
                .data("-out.form", outputType.getName())
                .data("-out.add", "_r")
                .data("outaddvalue", "default")
                .data("-sort", "_r")
                .data("-oc.form", "dec")
                .data("-c.eq", "J2000")
                .data("-c.geom", "r")
                .data("-order", "I")
                .data("noneucd1p", "on")
                .data("-file", ".")
                .data("-meta.ucd", "2")
                .data("-meta", "1")
                .data("-meta.foot", "1")
                .data("-usenav", "0")
                .data("-bmark", "POST")
                .data(data);
    }

    public String getBodyFromVizier(String catalogue, OutputType outputType, Map<String, String> data) throws IOException {
        return getConnectionFromVizier(catalogue, outputType, data).method(Connection.Method.POST).execute().body();
    }

    public String getBodyFromVizier(String catalogue, OutputType outputType) throws IOException {
        return getBodyFromVizier(catalogue, outputType, new HashMap<String, String>());
    }

    public Document getDocumentFromVizier(String catalogue, OutputType outputType, Map<String, String> data) throws IOException {
        return getConnectionFromVizier(catalogue, outputType, data).method(Connection.Method.POST).execute().parse();
    }

    public Document getDocumentFromVizier(String catalogue, OutputType outputType) throws IOException {
        return getConnectionFromVizier(catalogue, outputType, new HashMap<String, String>()).method(Connection.Method.POST).execute().parse();
    }

    public enum OutputType {
        TSV("http://vizier.u-strasbg.fr/viz-bin/asu-tsv", "; -Separated-Values"),
        HTML("http://vizier.u-strasbg.fr/viz-bin/VizieR-4", "HTML Table"),
        DETAILED("http://vizier.u-strasbg.fr/viz-bin/VizieR-4", "Detailed results");

        private String url;
        private String name;

        OutputType(String url, String name) {
            this.url = url;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
