
package net.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Balance {

    @SerializedName("tipjar")
    @Expose
    private Tipjar tipjar;

    public Tipjar getTipjar() {
        return tipjar;
    }

    public void setTipjar(Tipjar tipjar) {
        this.tipjar = tipjar;
    }

}
