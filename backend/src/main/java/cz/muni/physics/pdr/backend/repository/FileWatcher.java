package cz.muni.physics.pdr.backend.repository;

import java.io.File;

/**
 * Object of this class can be used to check for updates on file
 * @author Michal Krajčovič
 * @version 1.0
 * @since 18/04/16
 */
public class FileWatcher {
    private long timeStamp;
    private File file;

    /**
     * Creates FileWatcher object with file on given path
     * @param path
     */
    public FileWatcher(String path) {
        this.file = new File(path);
        this.timeStamp = file.lastModified();
    }

    /**
     * Creates FileWatcher object with given file
     * @param file
     */
    public FileWatcher(File file) {
        this.file = file;
        this.timeStamp = file.lastModified();
    }

    /**
     * Checks if file has been updated since creation of object/last check.
     * @return
     */
    public boolean isFileUpdated() {
        if (this.timeStamp != file.lastModified()) {
            this.timeStamp = file.lastModified();
            return true;
        }
        return false;
    }

    /**
     * Forces to update last modification time stamp of file
     */
    public void forceUpdate() {
        this.timeStamp = file.lastModified();
    }
}
