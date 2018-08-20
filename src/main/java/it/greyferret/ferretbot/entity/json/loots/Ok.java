
package it.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ok {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("attachments")
    @Expose
    private Attachments attachments;
    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("tracking")
    @Expose
    private Tracking tracking;
    @SerializedName("transfer")
    @Expose
    private Transfer transfer;
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

    public Attachments getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
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
