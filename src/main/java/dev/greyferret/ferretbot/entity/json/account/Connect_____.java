
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connect_____ {

    @SerializedName("channel")
    @Expose
    private Channel___ channel;
    @SerializedName("user")
    @Expose
    private User_ user;

    public Channel___ getChannel() {
        return channel;
    }

    public void setChannel(Channel___ channel) {
        this.channel = channel;
    }

    public User_ getUser() {
        return user;
    }

    public void setUser(User_ user) {
        this.user = user;
    }

}
