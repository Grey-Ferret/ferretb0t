
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tracking {

    @SerializedName("_total")
    @Expose
    private Total_ total;

    public Total_ getTotal() {
        return total;
    }

    public void setTotal(Total_ total) {
        this.total = total;
    }

}
