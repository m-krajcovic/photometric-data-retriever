package cz.muni.physics.pdr.hip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {
    //http://cdsarc.u-strasbg.fr/viz-bin/nph-Plot/Vgraph/txt?I/239/./14810d

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.readData(args);
    }

    private void readData(String... args) throws IOException {
        URL url;
        if (args.length == 1) {
            if (args[0].startsWith("http")) {//is url
                url = new URL(args[0]);
            } else { // it's id
                url = new URL("http://cdsarc.u-strasbg.fr/viz-bin/nph-Plot/Vgraph/txt?I/239/./" + args[0] + "&0&P=0&-Y&mag&-y&-&-&-");
            }
        } else {
            return;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] csv = line.split("\\s+");
                if (csv.length == 3) {
                    csv[0] = Double.toString(Double.parseDouble(csv[0]) + 2440000);
                    System.out.println(String.join(",", csv));
                }
            }
        }
    }
}

