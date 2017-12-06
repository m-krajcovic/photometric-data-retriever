package cz.muni.physics.pdr.app.updater;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.File;
import java.util.Comparator;

/**
 * @author Michal
 * @version 1.0
 * @since 10/14/2016
 */
public class VersionComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        int[] version1 = splitVersion(getVersion(o1));
        int[] version2 = splitVersion(getVersion(o2));
        int length = Integer.max(version1.length, version2.length);
        for (int i = 0; i < length; i++) {
            int v1 = 0;
            int v2 = 0;
            if (version1.length > i) {
                v1 = version1[i];
            }
            if (version2.length > i) {
                v2 = version2[i];
            }
            if (v1 != v2) {
                return Integer.compare(v1, v2);
            }
        }
        return 0;
    }

    public static String getVersion(File o1) {
        return o1.getName().substring(o1.getName().indexOf("-") + 1, o1.getName().lastIndexOf("."));
    }

    private int[] splitVersion(String version) {
        version = version.replaceAll("[a-zA-Z\\s]", "");
        String[] strSplit = version.split("\\.");
        int[] intSplit = new int[strSplit.length];
        for (int i = 0; i < strSplit.length; i++) {
            intSplit[i] = Integer.parseInt(strSplit[i]);
        }
        return intSplit;
    }
}
