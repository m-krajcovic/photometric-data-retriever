package cz.muni.physics.pdr.app.updater;

import cz.muni.physics.pdr.app.spring.Screens;
import cz.muni.physics.pdr.app.updater.github.Asset;
import cz.muni.physics.pdr.app.updater.github.Release;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author Michal
 * @version 1.0
 * @since 10/27/2016
 */
public class WindowsUpdaterService implements UpdaterService {

    private static final Logger logger = LogManager.getLogger(WindowsUpdaterService.class);

    @Value("${version}")
    private String version;

    @Value("${updater.github.latest.release}")
    private String githubApiUrl;

    @Value("${updater.download.folder}")
    private String updatesFolderPath;

    @Autowired
    private Screens app;

    @Override
    public File downloadUpdate(UpdaterStatus status) throws UpdaterUnavailableException {
        File updatesFolder = new File(updatesFolderPath);
        if (!updatesFolder.exists()) {
            updatesFolder.mkdirs();
        }
        File output = new File(updatesFolder, "pdr-" + status.getServerVersion() + ".exe");
        try {
            FileUtils.copyURLToFile(new URL(status.getUrl()), output);
            return output;
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public boolean applyUpdate(File update) {
        app.getPrimaryStage().close();
        try {
            Process p = Runtime.getRuntime().exec(update.getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UpdaterStatus status() throws UpdaterUnavailableException {
        try {
            RestTemplate rest = new RestTemplate();
            Release latestRelease = rest.getForObject(githubApiUrl, Release.class);
            UpdaterStatus status = new UpdaterStatus();
            status.setServerVersion(latestRelease.getTagName().replaceAll("[a-zA-Z\\s]", ""));
            Optional<Asset> exe = latestRelease.getAssets().stream().filter(a -> a.getName().endsWith("exe")).findFirst();
            if (exe.isPresent()) {
                Asset asset = exe.get();
                status.setUrl(asset.getBrowserDownloadUrl());
                status.setServerVersionSize(asset.getSize());
            }

            File updatesFolder = new File(updatesFolderPath);
            if (updatesFolder.exists() && updatesFolder.listFiles() != null) {
                Optional<File> maxOpt = Arrays.stream(updatesFolder.listFiles((dir, name) -> name.startsWith("pdr-") && name.endsWith(".exe"))).max(new VersionComparator());
                if (maxOpt.isPresent()) {
                    File localUpdate = maxOpt.get();
                    status.setLocalUpdate(localUpdate);
                    status.setLocalVersion(VersionComparator.getVersion(localUpdate));
                }
            }
            return status;
        } catch (RestClientException exc) {
            throw new UpdaterUnavailableException(exc);
        }
    }

    @Override
    public String getCurrentVersion() {
        return version;
    }

    @Override
    public File downloadUpdate(UpdaterStatus serverStatus, BiConsumer<Long, Long> consumer) throws UpdaterUnavailableException {
        if (consumer == null) {
            return downloadUpdate(serverStatus);
        } else {
            File updatesFolder = new File(updatesFolderPath);
            if (!updatesFolder.exists()) {
                updatesFolder.mkdirs();
            }
            File output = new File(updatesFolder, "pdr-" + serverStatus.getServerVersion() + ".exe");

            try (BufferedInputStream in = new BufferedInputStream(new URL(serverStatus.getUrl()).openStream());
                 FileOutputStream fout = new FileOutputStream(output)) {
                final byte data[] = new byte[1024];
                int count;
                long alreadyDownloaded = 0;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                    alreadyDownloaded += count;
                    consumer.accept(alreadyDownloaded, serverStatus.getServerVersionSize());
                }
            } catch (IOException e) {
                logger.error(e);
                throw new UpdaterUnavailableException(e);
            }

            return output;
        }
    }
}
