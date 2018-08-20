
package it.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tracking__ {

    @SerializedName("_total")
    @Expose
    private Total___ total;

    public Total___ getTotal() {
        return total;
    }

    public void setTotal(Total___ total) {
        this.total = total;
    }

}
