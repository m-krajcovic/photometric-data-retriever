package cz.muni.physics.pdr.ptf;

import cz.muni.physics.pdr.java.PluginUtils;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.ImageHDU;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Michal
 * @version 1.0
 * @since 7/7/2016
 */
public class Main {
    public static void main(String[] args) throws IOException, FitsException {
//http://irsa.ipac.caltech.edu/ibe/data/ptf/images/level1/proc/2012/09/20/f1/c0/p5/v1/PTF_201209202356_i_p_scie_t053919_u014094242_f01_p022592_c00.fits
        Main main = new Main();
//        main.readFits("http://irsa.ipac.caltech.edu/ibe/data/ptf/images/level1/proc/2012/09/20/f1/c0/p5/v1/PTF_201209202356_i_p_scie_t053919_u014094242_f01_p022592_c00.fits");

        File fitsFile = new File("C:\\Users\\Michal\\Downloads\\PTF_201209202356_i_p_scie_t053919_u014094242_f01_p022592_c00.fits");
        System.out.println(fitsFile.exists());
        Fits fits = new Fits(fitsFile);

        System.out.println(fits.getHDU(0));
        System.out.println(fits.getHDU(1));
    }

    public void readFits(String fitsUrl) throws IOException, FitsException {
        URL fetchUrl = new URL(fitsUrl);
        String[] fileName = fitsUrl.split("/");
        try (InputStream is = PluginUtils.copyUrlOpenStream(fetchUrl, "PTF-" + fileName[fileName.length - 1], 3)) {
            Fits fits = new Fits(is);
            for (BasicHDU<?> basicHDU : fits.read()) {
                System.out.println(basicHDU);
            }
            ImageHDU image = (ImageHDU) fits.getHDU(1);

//            float[] mags = (float[]) table.getColumn("MAG_V");
//            float[] errs = (float[]) table.getColumn("ERRMAG_V");
//            double[] barytimes = (double[]) table.getColumn("BARYTIME");
//            double[] telapses = (double[]) table.getColumn("TELAPSE");
//
//            for (int i = 0; i < mags.length; i++) {
//                double jd = barytimes[i] + 2451544.5 + telapses[i] / 2 / 86400;
//                System.out.println(jd + "," + mags[i] + "," + errs[i]);
//            }
        }
    }
}
