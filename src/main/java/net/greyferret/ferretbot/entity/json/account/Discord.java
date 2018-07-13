
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Discord {

    @SerializedName("loots")
    @Expose
    private Loots loots;

    public Loots getLoots() {
        return loots;
    }

    public void setLoots(Loots loots) {
        this.loots = loots;
    }

}
