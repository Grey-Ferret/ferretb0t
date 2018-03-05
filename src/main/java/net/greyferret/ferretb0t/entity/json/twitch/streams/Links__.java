
package net.greyferret.ferretb0t.entity.json.twitch.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links__ {

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("channel")
    @Expose
    private String channel;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
