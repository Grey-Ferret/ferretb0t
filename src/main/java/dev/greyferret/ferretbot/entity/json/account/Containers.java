
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Containers {

    @SerializedName("assets")
    @Expose
    private Assets assets;
    @SerializedName("attachments")
    @Expose
    private Attachments attachments;
    @SerializedName("contents")
    @Expose
    private Contents contents;
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("stages")
    @Expose
    private Stages stages;
    @SerializedName("tipjars")
    @Expose
    private Tipjars tipjars;
    @SerializedName("video")
    @Expose
    private Video video;

    public Assets getAssets() {
        return assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    public Contents getContents() {
        return contents;
    }

    public void setContents(Contents contents) {
        this.contents = contents;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Stages getStages() {
        return stages;
    }

    public void setStages(Stages stages) {
        this.stages = stages;
    }

    public Tipjars getTipjars() {
        return tipjars;
    }

    public void setTipjars(Tipjars tipjars) {
        this.tipjars = tipjars;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

}
