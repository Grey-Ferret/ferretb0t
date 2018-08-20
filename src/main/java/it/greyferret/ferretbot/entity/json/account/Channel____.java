
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channel____ {

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
    private Snippet_ snippet;
    @SerializedName("contentDetails")
    @Expose
    private ContentDetails_ contentDetails;
    @SerializedName("statistics")
    @Expose
    private Statistics_ statistics;
    @SerializedName("topicDetails")
    @Expose
    private TopicDetails_ topicDetails;
    @SerializedName("status")
    @Expose
    private Status_ status;
    @SerializedName("brandingSettings")
    @Expose
    private BrandingSettings_ brandingSettings;
    @SerializedName("auditDetails")
    @Expose
    private AuditDetails_ auditDetails;

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

    public Snippet_ getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet_ snippet) {
        this.snippet = snippet;
    }

    public ContentDetails_ getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails_ contentDetails) {
        this.contentDetails = contentDetails;
    }

    public Statistics_ getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics_ statistics) {
        this.statistics = statistics;
    }

    public TopicDetails_ getTopicDetails() {
        return topicDetails;
    }

    public void setTopicDetails(TopicDetails_ topicDetails) {
        this.topicDetails = topicDetails;
    }

    public Status_ getStatus() {
        return status;
    }

    public void setStatus(Status_ status) {
        this.status = status;
    }

    public BrandingSettings_ getBrandingSettings() {
        return brandingSettings;
    }

    public void setBrandingSettings(BrandingSettings_ brandingSettings) {
        this.brandingSettings = brandingSettings;
    }

    public AuditDetails_ getAuditDetails() {
        return auditDetails;
    }

    public void setAuditDetails(AuditDetails_ auditDetails) {
        this.auditDetails = auditDetails;
    }

}
