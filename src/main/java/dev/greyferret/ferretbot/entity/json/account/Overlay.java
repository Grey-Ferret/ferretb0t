
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Overlay {

    @SerializedName("formats")
    @Expose
    private List<String> formats = null;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("video")
    @Expose
    private Boolean video;

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

}
