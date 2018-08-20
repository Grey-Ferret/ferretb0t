
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connect____ {

    @SerializedName("channel")
    @Expose
    private Channel_ channel;

    public Channel_ getChannel() {
        return channel;
    }

    public void setChannel(Channel_ channel) {
        this.channel = channel;
    }

}
