
package dev.greyferret.ferretbot.entity.json.twitch.users.follows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("from_id")
    @Expose
    private String fromId;
    @SerializedName("from_name")
    @Expose
    private String fromName;
    @SerializedName("to_id")
    @Expose
    private String toId;
    @SerializedName("to_name")
    @Expose
    private String toName;
    @SerializedName("followed_at")
    @Expose
    private String followedAt;

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public Datum withFromId(String fromId) {
        this.fromId = fromId;
        return this;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public Datum withFromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Datum withToId(String toId) {
        this.toId = toId;
        return this;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public Datum withToName(String toName) {
        this.toName = toName;
        return this;
    }

    public String getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(String followedAt) {
        this.followedAt = followedAt;
    }

    public Datum withFollowedAt(String followedAt) {
        this.followedAt = followedAt;
        return this;
    }

}
