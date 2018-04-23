package cz.muni.physics.pdr.kepler;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.java.Plugin;
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
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main implements Plugin {
    public static void main(String[] args) {
    }

    private void readData() {

    }

    @Override
    public List<PhotometricData> getDataFromUrl(String url) {
        return null;
    }
}
