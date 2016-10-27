
package cz.muni.physics.pdr.app.updater.github;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "url",
    "assets_url",
    "upload_url",
    "html_url",
    "id",
    "tag_name",
    "target_commitish",
    "name",
    "draft",
    "author",
    "prerelease",
    "created_at",
    "published_at",
    "assets",
    "tarball_url",
    "zipball_url",
    "body"
})
public class Release {

    @JsonProperty("url")
    private String url;
    @JsonProperty("assets_url")
    private String assetsUrl;
    @JsonProperty("upload_url")
    private String uploadUrl;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("tag_name")
    private String tagName;
    @JsonProperty("target_commitish")
    private String targetCommitish;
    @JsonProperty("name")
    private String name;
    @JsonProperty("draft")
    private Boolean draft;
    @JsonProperty("author")
    private Author author;
    @JsonProperty("prerelease")
    private Boolean prerelease;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("published_at")
    private String publishedAt;
    @JsonProperty("assets")
    private List<Asset> assets = new ArrayList<Asset>();
    @JsonProperty("tarball_url")
    private String tarballUrl;
    @JsonProperty("zipball_url")
    private String zipballUrl;
    @JsonProperty("body")
    private String body;
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
     *     The assetsUrl
     */
    @JsonProperty("assets_url")
    public String getAssetsUrl() {
        return assetsUrl;
    }

    /**
     * 
     * @param assetsUrl
     *     The assets_url
     */
    @JsonProperty("assets_url")
    public void setAssetsUrl(String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }

    /**
     * 
     * @return
     *     The uploadUrl
     */
    @JsonProperty("upload_url")
    public String getUploadUrl() {
        return uploadUrl;
    }

    /**
     * 
     * @param uploadUrl
     *     The upload_url
     */
    @JsonProperty("upload_url")
    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    /**
     * 
     * @return
     *     The htmlUrl
     */
    @JsonProperty("html_url")
    public String getHtmlUrl() {
        return htmlUrl;
    }

    /**
     * 
     * @param htmlUrl
     *     The html_url
     */
    @JsonProperty("html_url")
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
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
     *     The tagName
     */
    @JsonProperty("tag_name")
    public String getTagName() {
        return tagName;
    }

    /**
     * 
     * @param tagName
     *     The tag_name
     */
    @JsonProperty("tag_name")
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 
     * @return
     *     The targetCommitish
     */
    @JsonProperty("target_commitish")
    public String getTargetCommitish() {
        return targetCommitish;
    }

    /**
     * 
     * @param targetCommitish
     *     The target_commitish
     */
    @JsonProperty("target_commitish")
    public void setTargetCommitish(String targetCommitish) {
        this.targetCommitish = targetCommitish;
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
     *     The draft
     */
    @JsonProperty("draft")
    public Boolean getDraft() {
        return draft;
    }

    /**
     * 
     * @param draft
     *     The draft
     */
    @JsonProperty("draft")
    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    /**
     * 
     * @return
     *     The author
     */
    @JsonProperty("author")
    public Author getAuthor() {
        return author;
    }

    /**
     * 
     * @param author
     *     The author
     */
    @JsonProperty("author")
    public void setAuthor(Author author) {
        this.author = author;
    }

    /**
     * 
     * @return
     *     The prerelease
     */
    @JsonProperty("prerelease")
    public Boolean getPrerelease() {
        return prerelease;
    }

    /**
     * 
     * @param prerelease
     *     The prerelease
     */
    @JsonProperty("prerelease")
    public void setPrerelease(Boolean prerelease) {
        this.prerelease = prerelease;
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
     *     The publishedAt
     */
    @JsonProperty("published_at")
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * 
     * @param publishedAt
     *     The published_at
     */
    @JsonProperty("published_at")
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * 
     * @return
     *     The assets
     */
    @JsonProperty("assets")
    public List<Asset> getAssets() {
        return assets;
    }

    /**
     * 
     * @param assets
     *     The assets
     */
    @JsonProperty("assets")
    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    /**
     * 
     * @return
     *     The tarballUrl
     */
    @JsonProperty("tarball_url")
    public String getTarballUrl() {
        return tarballUrl;
    }

    /**
     * 
     * @param tarballUrl
     *     The tarball_url
     */
    @JsonProperty("tarball_url")
    public void setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
    }

    /**
     * 
     * @return
     *     The zipballUrl
     */
    @JsonProperty("zipball_url")
    public String getZipballUrl() {
        return zipballUrl;
    }

    /**
     * 
     * @param zipballUrl
     *     The zipball_url
     */
    @JsonProperty("zipball_url")
    public void setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
    }

    /**
     * 
     * @return
     *     The body
     */
    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    /**
     * 
     * @param body
     *     The body
     */
    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
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
