package cz.muni.physics.pdr.kepler;

import cz.muni.physics.pdr.java.PhotometricData;
import cz.muni.physics.pdr.java.PluginUtils;
import nom.tam.fits.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 21/04/16
 */
public class Main {
    public static void main(String[] args) {
        Main plugin = new Main();
        if (args.length < 1) return;
        // "001429092"
        StringBuilder kic = new StringBuilder(args[0]);
        if (args[0].length() < 9) {
            for (int i = 0; i < 9 - args[0].length(); i++) {
                kic.insert(0, "0");
            }
        }
        plugin.getDataFromUrl(kic.toString());
    }

    public List<PhotometricData> getDataFromUrl(String id) {
        FTPClient ftpClient = new FTPClient();
        String targetDownloadedZipFileName = "KEPLER-" + id + ".tar";
        File targetDownloadedTarFile = new File(PluginUtils.getOutputDir(), targetDownloadedZipFileName);
        if (!targetDownloadedTarFile.exists()) {
            try {
                ftpClient.connect("archive.stsci.edu");
                ftpClient.login("anonymous", "anonymous");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                ftpClient.changeWorkingDirectory("/pub/kepler/lightcurves/" + id.substring(0, 4) + "/" + id);

                FTPFile[] ftpFiles = ftpClient.listFiles("*.tar");

                if (ftpFiles.length > 0) {
                    FTPFile targetZipFile = ftpFiles[0];

                    try (InputStream is = ftpClient.retrieveFileStream(targetZipFile.getName())) {
                        PluginUtils.copyInputStreamToFile(is, targetDownloadedTarFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try (TarArchiveInputStream tarInput = new TarArchiveInputStream(new FileInputStream(targetDownloadedTarFile))) {
            TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
            while (currentEntry != null) {
                Fits fits = new Fits(tarInput);
                BasicHDU<?>[] read = fits.read();
                if (read.length > 2 && read[1] instanceof BinaryTableHDU) {
                    BinaryTableHDU table = (BinaryTableHDU) read[1];
                    float[] fluxes = (float[]) table.getColumn("PDCSAP_FLUX");
                    float[] errs = (float[]) table.getColumn("PDCSAP_FLUX_ERR");
                    double[] times = (double[]) table.getColumn("TIME");

                    for (int i = 0; i < fluxes.length; i++) {
                        // fixing kepler error + transform to jd
                        double jd = times[i] + (times[i] <= 57139 ? 66.184 : 67.184) + 2454833;
                        double flux = -2.5 * Math.log(fluxes[i]);
                        System.out.println(jd + "," + flux + "," + errs[i]);
                    }
                }
                currentEntry = tarInput.getNextTarEntry(); // You forgot to iterate to the next file
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FitsException e) {
            e.printStackTrace();
        }
        return null;
    }
}
