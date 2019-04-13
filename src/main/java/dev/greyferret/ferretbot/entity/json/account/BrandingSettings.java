
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrandingSettings {

    @SerializedName("channel")
    @Expose
    private Channel__ channel;
    @SerializedName("image")
    @Expose
    private Image image;
    @SerializedName("hints")
    @Expose
    private List<Hint> hints = null;

    public Channel__ getChannel() {
        return channel;
    }

    public void setChannel(Channel__ channel) {
        this.channel = channel;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Hint> getHints() {
        return hints;
    }

    public void setHints(List<Hint> hints) {
        this.hints = hints;
    }

}
