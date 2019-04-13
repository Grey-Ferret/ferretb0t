
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channel_ {

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("snippet")
    @Expose
    private Snippet snippet;
    @SerializedName("contentDetails")
    @Expose
    private ContentDetails contentDetails;
    @SerializedName("statistics")
    @Expose
    private Statistics statistics;
    @SerializedName("topicDetails")
    @Expose
    private TopicDetails topicDetails;
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("brandingSettings")
    @Expose
    private BrandingSettings brandingSettings;
    @SerializedName("auditDetails")
    @Expose
    private AuditDetails auditDetails;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public TopicDetails getTopicDetails() {
        return topicDetails;
    }

    public void setTopicDetails(TopicDetails topicDetails) {
        this.topicDetails = topicDetails;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BrandingSettings getBrandingSettings() {
        return brandingSettings;
    }

    public void setBrandingSettings(BrandingSettings brandingSettings) {
        this.brandingSettings = brandingSettings;
    }

    public AuditDetails getAuditDetails() {
        return auditDetails;
    }

    public void setAuditDetails(AuditDetails auditDetails) {
        this.auditDetails = auditDetails;
    }

}
