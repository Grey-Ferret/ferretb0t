
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Twitter {

    @SerializedName("loots")
    @Expose
    private Loots_ loots;

    public Loots_ getLoots() {
        return loots;
    }

    public void setLoots(Loots_ loots) {
        this.loots = loots;
    }

}
