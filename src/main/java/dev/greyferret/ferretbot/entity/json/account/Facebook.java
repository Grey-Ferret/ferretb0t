
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Facebook {

    @SerializedName("connect")
    @Expose
    private Connect_ connect;

    public Connect_ getConnect() {
        return connect;
    }

    public void setConnect(Connect_ connect) {
        this.connect = connect;
    }

}
