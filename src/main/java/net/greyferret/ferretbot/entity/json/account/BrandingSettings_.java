
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrandingSettings_ {

    @SerializedName("channel")
    @Expose
    private Channel_____ channel;
    @SerializedName("image")
    @Expose
    private Image_ image;
    @SerializedName("hints")
    @Expose
    private List<Hint_> hints = null;

    public Channel_____ getChannel() {
        return channel;
    }

    public void setChannel(Channel_____ channel) {
        this.channel = channel;
    }

    public Image_ getImage() {
        return image;
    }

    public void setImage(Image_ image) {
        this.image = image;
    }

    public List<Hint_> getHints() {
        return hints;
    }

    public void setHints(List<Hint_> hints) {
        this.hints = hints;
    }

}
