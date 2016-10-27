
package cz.muni.physics.pdr.app.updater.github;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "url",
    "id",
    "name",
    "label",
    "uploader",
    "content_type",
    "state",
    "size",
    "download_count",
    "created_at",
    "updated_at",
    "browser_download_url"
})
public class Asset {

    @JsonProperty("url")
    private String url;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("label")
    private Object label;
    @JsonProperty("uploader")
    private Uploader uploader;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("state")
    private String state;
    @JsonProperty("size")
    private Long size;
    @JsonProperty("download_count")
    private Integer downloadCount;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("browser_download_url")
    private String browserDownloadUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The label
     */
    @JsonProperty("label")
    public Object getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     *     The label
     */
    @JsonProperty("label")
    public void setLabel(Object label) {
        this.label = label;
    }

    /**
     * 
     * @return
     *     The uploader
     */
    @JsonProperty("uploader")
    public Uploader getUploader() {
        return uploader;
    }

    /**
     * 
     * @param uploader
     *     The uploader
     */
    @JsonProperty("uploader")
    public void setUploader(Uploader uploader) {
        this.uploader = uploader;
    }

    /**
     * 
     * @return
     *     The contentType
     */
    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    /**
     * 
     * @param contentType
     *     The content_type
     */
    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 
     * @return
     *     The state
     */
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 
     * @return
     *     The size
     */
    @JsonProperty("size")
    public Long getSize() {
        return size;
    }

    /**
     * 
     * @param size
     *     The size
     */
    @JsonProperty("size")
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 
     * @return
     *     The downloadCount
     */
    @JsonProperty("download_count")
    public Integer getDownloadCount() {
        return downloadCount;
    }

    /**
     * 
     * @param downloadCount
     *     The download_count
     */
    @JsonProperty("download_count")
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updated_at
     */
    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The browserDownloadUrl
     */
    @JsonProperty("browser_download_url")
    public String getBrowserDownloadUrl() {
        return browserDownloadUrl;
    }

    /**
     * 
     * @param browserDownloadUrl
     *     The browser_download_url
     */
    @JsonProperty("browser_download_url")
    public void setBrowserDownloadUrl(String browserDownloadUrl) {
        this.browserDownloadUrl = browserDownloadUrl;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
