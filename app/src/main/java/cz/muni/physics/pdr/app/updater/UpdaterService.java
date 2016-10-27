package cz.muni.physics.pdr.app.updater;

import java.io.File;
import java.util.function.BiConsumer;

/**
 * @author Michal
 * @version 1.0
 * @since 10/13/2016
 */
public interface UpdaterService {

    File downloadUpdate(UpdaterStatus status) throws UpdaterUnavailableException;

    boolean applyUpdate(File update);

    UpdaterStatus status() throws UpdaterUnavailableException;

    String getCurrentVersion();

    File downloadUpdate(UpdaterStatus serverStatus, BiConsumer<Long, Long> consumer) throws UpdaterUnavailableException;
}
