package cz.muni.physics.pdr.backend.repository;

import java.io.File;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 18/04/16
 */
public class FileWatcher {
    private long timeStamp;
    private File file;

    public FileWatcher(String path) {
        this.file = new File(path);
        this.timeStamp = file.lastModified();
    }

    public FileWatcher(File file) {
        this.file = file;
        this.timeStamp = file.lastModified();
    }

    public boolean isFileUpdated() {
        if (this.timeStamp != file.lastModified()) {
            this.timeStamp = file.lastModified();
            return true;
        }
        return false;
    }
}
