package cz.muni.physics.pdr.dk154;

import au.com.bytecode.opencsv.CSVReader;
import cz.muni.physics.pdr.java.PluginUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * @author Michal
 * @version 1.0
 * @since 9/21/2016
 */
public class Main {
    public static void main(String[] args) throws IOException {

        String input = args.length == 1 ? args[0] : args[0] + " " + args[1];

        Connection.Response response = Jsoup.connect("http://vos2.asu.cas.cz/extract_jan16/q/scs/form")
                .data("_charset_", "UTF-8",
                        "__nevow_form__", "genForm",
                        "hscs_pos", input,
                        "hscs_sr", "0.05",
//                        "ssa_dateObs", "",
                        "BAND", "ALL",
                        "_DBOPTIONS_ORDER", "",
                        "_DBOPTIONS_DIR", "ASC",
                        "MAXREC", "100000",
                        "_FORMAT", "CSV",
//                        "_VERB", "H",
                        "submit", "Go")
                .timeout(10000).method(Connection.Method.POST).ignoreContentType(true).execute();

        try (Reader reader = new FileReader(PluginUtils.saveOriginal("DK154-" + input + ".csv", response.bodyAsBytes(), 5));
             CSVReader csvReader = new CSVReader(reader)) {
            for (String[] row : csvReader.readAll()) {
                String mag = row[2];
                String err = row[10];
                String hjd = row[24];
                double hjdDouble;
                try {
                    hjdDouble = Double.parseDouble(hjd) + 2400000;
                } catch (NumberFormatException e) {
                    continue;
                }
                String band = row[25];
                String id = row[33];
                System.out.println(hjdDouble + "," + mag + "," + err + "," + id +"-" +band);
            }
        }
    }
}
