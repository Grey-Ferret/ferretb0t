
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connections {

    @SerializedName("twitch")
    @Expose
    private Twitch twitch;
    @SerializedName("youtube")
    @Expose
    private Youtube youtube;

    public Twitch getTwitch() {
        return twitch;
    }

    public void setTwitch(Twitch twitch) {
        this.twitch = twitch;
    }

    public Youtube getYoutube() {
        return youtube;
    }

    public void setYoutube(Youtube youtube) {
        this.youtube = youtube;
    }

}
