package cz.muni.physics.pdr.asas3;

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
        //http://www.astrouw.edu.pl//cgi-asas/asas_cgi_get_data?123300+2643.0,asas3
        //http://www.astrouw.edu.pl/asas/i_aasc/aasc_form.php?catsrc=asas3
        Main main = new Main();

        if(args.length == 1){
            if (args[0].startsWith("http")){
                main.readData(args[0]);
            }
        }
    }

    private void readData(String... args) throws IOException {
        URL url;
        if (args.length == 1) {
            if (args[0].startsWith("http")) {//je to link s asas3.id
                url = new URL(args[0]);
            } else { // je to nieco co dat do searchu
                url = new URL(searchFor(args[0]));
            }
        } else {
            return;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
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
