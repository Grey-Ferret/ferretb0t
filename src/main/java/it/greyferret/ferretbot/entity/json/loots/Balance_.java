
package it.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Balance_ {

    @SerializedName("tipjar")
    @Expose
    private Tipjar_ tipjar;

    public Tipjar_ getTipjar() {
        return tipjar;
    }

    public void setTipjar(Tipjar_ tipjar) {
        this.tipjar = tipjar;
    }

}
