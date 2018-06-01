
package net.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tracking_ {

    @SerializedName("_total")
    @Expose
    private Total__ total;

    public Total__ getTotal() {
        return total;
    }

    public void setTotal(Total__ total) {
        this.total = total;
    }

}
