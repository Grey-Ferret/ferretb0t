
package net.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rejected {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("attachments")
    @Expose
    private Attachments__ attachments;
    @SerializedName("from")
    @Expose
    private From__ from;
    @SerializedName("tracking")
    @Expose
    private Tracking__ tracking;
    @SerializedName("transfer")
    @Expose
    private Transfer__ transfer;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("submitted")
    @Expose
    private Long submitted;
    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("modified")
    @Expose
    private Long modified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Attachments__ getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments__ attachments) {
        this.attachments = attachments;
    }

    public From__ getFrom() {
        return from;
    }

    public void setFrom(From__ from) {
        this.from = from;
    }

    public Tracking__ getTracking() {
        return tracking;
    }

    public void setTracking(Tracking__ tracking) {
        this.tracking = tracking;
    }

    public Transfer__ getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer__ transfer) {
        this.transfer = transfer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Long submitted) {
        this.submitted = submitted;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

}
