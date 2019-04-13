
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mixer {

    @SerializedName("connect")
    @Expose
    private Connect__ connect;

    public Connect__ getConnect() {
        return connect;
    }

    public void setConnect(Connect__ connect) {
        this.connect = connect;
    }

}
