
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConnectionsBots {

    @SerializedName("twitch")
    @Expose
    private Twitch_ twitch;
    @SerializedName("youtube")
    @Expose
    private Youtube_ youtube;

    public Twitch_ getTwitch() {
        return twitch;
    }

    public void setTwitch(Twitch_ twitch) {
        this.twitch = twitch;
    }

    public Youtube_ getYoutube() {
        return youtube;
    }

    public void setYoutube(Youtube_ youtube) {
        this.youtube = youtube;
    }

}
