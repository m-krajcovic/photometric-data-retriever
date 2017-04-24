package cz.muni.physics.pdr.ogle;

import cz.muni.physics.pdr.java.PluginUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michal
 * @version 1.0
 * @since 7/7/2016
 */
public class Main {

    public static class OgleConfig {
        private String name;
        private String identFilePath;
        private String photDirPath;
        private String ogleId;

        @Override
        public String toString() {
            return "OgleConfig{" +
                    "name='" + name + '\'' +
                    ", identFilePath='" + identFilePath + '\'' +
                    ", photDirPath='" + photDirPath + '\'' +
                    ", ogleId='" + ogleId + '\'' +
                    '}';
        }
    }

    private List<OgleConfig> ogleConfigs;
    private Map<String, String> idCache = new HashMap<>();
    private String starId;
    private String field;

    public Main(String field, String starId) {
        this.field = field;
        this.starId = starId;
        ogleConfigs = loadOgleConfigs();
    }

    private List<OgleConfig> loadOgleConfigs() {
        List<OgleConfig> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/ogleconfig.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(";");
                if (cols.length >= 3) {
                    OgleConfig config = new OgleConfig();
                    config.name = cols[0];
                    config.identFilePath = cols[1];
                    config.photDirPath = cols[2];
                    config.ogleId = findOgleId(config.identFilePath);
                    result.add(config);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void doMagic() throws IOException {
        ogleConfigs.stream().filter(config -> config.ogleId != null).forEach(ogleConfig -> {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect("ogle.astrouw.edu.pl");
                ftpClient.login("anonymous", "anonymous");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                String remote = ogleConfig.photDirPath + ogleConfig.ogleId + ".dat";
                try (InputStream is = ftpClient.retrieveFileStream(remote);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    StringBuilder original = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        original.append(line).append("\n");
                        line = line.trim();
                        String[] cols = line.split(" ");
                        String jd = Double.toString(Double.parseDouble(cols[0].trim()) + 2450000);
                        String mag = cols[1].trim();
                        String err = cols[2].trim();
                        System.out.println(jd + "," + mag + "," + err + "," + ogleConfig.name);
                    }
                    PluginUtils.saveOriginal("OGLE-" + ogleConfig.name + "-" + ogleConfig.ogleId + ".dat", original.toString(), 5);
                } catch (IOException e) {
                    e.printStackTrace();
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
        });
    }

    private String findOgleId(String identFilePath) {
        if (idCache.containsKey(identFilePath)) {
            return idCache.get(identFilePath);
        }
        try (BufferedReader reader = openIdentReader(identFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split("\\s+"); // 0 a 6
                if (cols.length >= 7 && cols[6].startsWith(field) && cols[6].endsWith("_" + starId)) {
                    idCache.put(identFilePath, cols[0]);
                    return cols[0];
                } else if (cols.length == 6 && cols[5].startsWith(field) && cols[5].endsWith("_" + starId)) {
                    idCache.put(identFilePath, cols[0]);
                    return cols[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedReader openIdentReader(String identFilePath) {
        InputStream is = Main.class.getResourceAsStream(identFilePath);
        return new BufferedReader(new InputStreamReader(is));
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String field = args[0];
            String starId = args[1];
            String[] split = args[0].trim().split("[\\-\\s_]");
            if (split.length > 1) {
                field = split[0];
                if (starId.trim().isEmpty()) {
                    starId = split[split.length - 1];
                }
            }
            Main main = new Main(field, starId);
            try {
                main.doMagic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        Map<String, String> dirs = new HashMap<>();
//OGLE SMC-SC10 37156
//        OGLE SMC-SC4 163724

//        /// uniqueName,ident.dat, photDir
//        dirs.put("3-I", "/ogle/ogle/OIII-CVS/smc/ecl/phot/I/");
//        dirs.put("3-V", "/ogle/ogle/OIII-CVS/smc/ecl/phot/V/");
//
//        if (args.length == 1) {
//            String ogleId = main.findByStarId(args[0]);
//            for (Map.Entry<String, String> entry: dirs.entrySet()) {
//                main.readData(ogleId, entry.getKey(), entry.getValue());
//            }
//        }
    }
}
