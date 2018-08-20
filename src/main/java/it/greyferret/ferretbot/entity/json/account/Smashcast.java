
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Smashcast {

    @SerializedName("connect")
    @Expose
    private Connect_______ connect;

    public Connect_______ getConnect() {
        return connect;
    }

    public void setConnect(Connect_______ connect) {
        this.connect = connect;
    }

}
