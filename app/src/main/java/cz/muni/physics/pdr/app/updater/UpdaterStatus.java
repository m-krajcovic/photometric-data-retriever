package cz.muni.physics.pdr.app.updater;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;

/**
 * @author Michal
 * @version 1.0
 * @since 10/15/2016
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdaterStatus {
    private String serverVersion;
    private String url;

    private File localUpdate;
    private String localVersion;
    private Long serverVersionSize;

    public UpdaterStatus() {
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public File getLocalUpdate() {
        return localUpdate;
    }

    public void setLocalUpdate(File localUpdate) {
        this.localUpdate = localUpdate;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public void setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
    }

    public Long getServerVersionSize() {
        return serverVersionSize;
    }

    public void setServerVersionSize(Long serverVersionSize) {
        this.serverVersionSize = serverVersionSize;
    }
}
