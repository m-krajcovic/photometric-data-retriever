package cz.muni.physics.pdr.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michal on 5/13/2016.
 */
public class PluginUtils {
    public static void deleteOldestFile(int allowedCount) {
        File dir = getOutputDir();
        if (dir != null) {
            File[] files;
            while ((files = dir.listFiles()) != null && files.length > allowedCount) {
                File oldest = getOldest(files);
                if (oldest != null) oldest.delete();
            }
        }
    }

    public static File getOldest(File[] files) {
        if (files == null) {
            throw new NullPointerException("Parameter files cannot be null");
        }
        if (files.length == 0) {
            return null;
        }
        File oldest = files[0];
        for (int i = 1; i < files.length; i++) {
            if (files[i].lastModified() < oldest.lastModified()) {
                oldest = files[i];
            }
        }
        return oldest;
    }

    public static File saveOriginal(String fileName, String text, int allowedCount) throws IOException {
        return saveOriginal(fileName, text.getBytes(), allowedCount);
    }

    public static File saveOriginal(String fileName, byte[] text, int allowedCount) throws IOException {
            File file = new File(getOutputDir(), fileName);
        try (InputStream is = new ByteArrayInputStream(text)) {
            writeFile(is, file);
        }
        deleteOldestFile(allowedCount);
        return file;
    }

    public static File copyUrlToFile(URL url, String destName, int allowedCount) throws IOException {
        File f = copyUrlToFile(url, new File(getOutputDir(), destName));
        deleteOldestFile(allowedCount);
        return f;
    }

    public static File copyInputStreamToFile(InputStream is, File dest) throws IOException {
        writeFile(is, dest);
        return dest;
    }

    public static File copyUrlToFile(URL url, File dest) throws IOException {
        try (InputStream is = url.openStream()) {
            return copyInputStreamToFile(is, dest);
        }
    }

    public static File getOutputDir() {
        try {
            File jar = new File(PluginUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            System.err.println(jar.getAbsolutePath());
            File pluginDir = jar.getParentFile();
            File outDir = new File(pluginDir, "output");
            return outDir;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static void writeFile(InputStream is, File file) throws IOException {
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            final byte[] data = new byte[1024];
            int count;
            while ((count = is.read(data, 0, 1024)) != -1) {
                fos.write(data, 0, count);
            }
        }
    }

    public static InputStream copyUrlOpenStream(URL url, String destName, int allowedCount) throws IOException {
        return new FileInputStream(copyUrlToFile(url, destName, allowedCount));
    }

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }
}
