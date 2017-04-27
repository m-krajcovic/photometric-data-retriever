package cz.muni.physics.pdr.ogle;

import cz.muni.physics.pdr.java.PluginUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Set<String> fields;

    public Main(Set<String> fields, String starId) {
        this.fields = fields;
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
        findRightConfigs().forEach(ogleConfig -> {
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
            } catch (IOException e) {
                e.printStackTrace();
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
                int colToCheck = 0;
                for (String field : fields) {
                    if (field.contains("SC")) {
                        if (cols.length == 6) {
                            colToCheck = 5;
                        } else if (cols.length >= 7) {
                            colToCheck = 6;
                        } else {
                            return null;
                        }
                    }
                }
                if (checkObject(cols[colToCheck])) {
                    idCache.put(identFilePath, cols[0]);
                    return cols[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<OgleConfig> findRightConfigs() {
        List<OgleConfig> configs = new ArrayList<>();
        for (OgleConfig ogleConfig : ogleConfigs) {
            List<String> sc = fields.stream().filter(f -> !f.contains("SC")).collect(Collectors.toList());
            boolean check = true;
            for (String field : sc) {
                check = check && ogleConfig.name.contains(field);
            }
            if (check) {
                configs.add(ogleConfig);
            }
        }
        return configs;
    }

    private boolean checkObject(String value) {
        boolean result = true;
        for (String field : fields) {
            result = result && value.contains(field);
        }
        String[] split = value.split("-");
        result = result && split[split.length -1].matches("0*"+starId);
        return result;
    }

    private BufferedReader openIdentReader(String identFilePath) {
        InputStream is = Main.class.getResourceAsStream(identFilePath);
        return new BufferedReader(new InputStreamReader(is));
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            Set<String> fields = new HashSet<>();
            String field = args[0];
            String starId = args[1];
            String[] split = args[0].trim().split("[\\-\\s_]");
            int length = split.length;
            if (length > 1) {
                fields.addAll(Arrays.asList(split).subList(0, length - 1));
                if (starId.trim().isEmpty() || split[length - 1].matches("\\d+")) {
                    starId = split[length - 1];
                } else {
                    fields.add(split[length - 1]);
                }
            } else {
                fields.add(field);
            }
            if (fields.contains("OGLE")) {
                fields.remove("OGLE");
            }
            for (String s : fields) {
                System.out.println(s);
            }
            Main main = new Main(fields, starId);
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
