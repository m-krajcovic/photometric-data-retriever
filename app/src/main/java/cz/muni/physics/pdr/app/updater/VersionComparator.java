package cz.muni.physics.pdr.app.updater;

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
        return getVersion(o1).compareTo(getVersion(o2));
    }

    public static String getVersion(File o1) {
        return o1.getName().substring(o1.getName().indexOf("-") + 1, o1.getName().lastIndexOf("."));
    }
}
