
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status_ {

    @SerializedName("privacyStatus")
    @Expose
    private String privacyStatus;
    @SerializedName("isLinked")
    @Expose
    private Boolean isLinked;
    @SerializedName("longUploadsStatus")
    @Expose
    private String longUploadsStatus;

    public String getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(String privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public Boolean getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(Boolean isLinked) {
        this.isLinked = isLinked;
    }

    public String getLongUploadsStatus() {
        return longUploadsStatus;
    }

    public void setLongUploadsStatus(String longUploadsStatus) {
        this.longUploadsStatus = longUploadsStatus;
    }

}
