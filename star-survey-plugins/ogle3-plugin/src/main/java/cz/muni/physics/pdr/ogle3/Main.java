package cz.muni.physics.pdr.ogle3;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Michal
 * @version 1.0
 * @since 7/7/2016
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        if (args.length == 1) {
            String ogleId = main.findByStarId(args[0]);
            main.readData(ogleId, "I");
            main.readData(ogleId, "V");
        }
    }

    public void readData(String id, String band) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect("ogle.astrouw.edu.pl");
        ftpClient.login("anonymous", "anonymous");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        String photDir = "/ogle/ogle3/OIII-CVS/smc/ecl/phot/";

        try (InputStream is = ftpClient.retrieveFileStream(photDir + band + "/" + id + ".dat");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(" ");
                String jd = Double.toString(Double.parseDouble(cols[0]) + 2450000);
                String mag = cols[1];
                String err = cols[2];
                System.out.println(jd + "," + mag + "," + err + "," + band);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
    }

    public String findByStarId(String starId) {
        try (BufferedReader reader = openIdentReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\\s+"); // 0 a 6
                if (cols.length >= 7 && cols[6].endsWith("_" + starId)) {
                    return cols[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedReader openIdentReader() {
        InputStream is = Main.class.getResourceAsStream("/ident.dat");
        return new BufferedReader(new InputStreamReader(is));
    }
}
