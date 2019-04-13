
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Balance__ {

    @SerializedName("tipjar")
    @Expose
    private Tipjar__ tipjar;

    public Tipjar__ getTipjar() {
        return tipjar;
    }

    public void setTipjar(Tipjar__ tipjar) {
        this.tipjar = tipjar;
    }

}
