
package net.greyferret.ferretb0t.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timing_ {

    @SerializedName("delay_tip_hold")
    @Expose
    private Long delayTipHold;
    @SerializedName("on_hold_delay_start")
    @Expose
    private Long onHoldDelayStart;
    @SerializedName("on_hold_modified")
    @Expose
    private Long onHoldModified;

    public Long getDelayTipHold() {
        return delayTipHold;
    }

    public void setDelayTipHold(Long delayTipHold) {
        this.delayTipHold = delayTipHold;
    }

    public Long getOnHoldDelayStart() {
        return onHoldDelayStart;
    }

    public void setOnHoldDelayStart(Long onHoldDelayStart) {
        this.onHoldDelayStart = onHoldDelayStart;
    }

    public Long getOnHoldModified() {
        return onHoldModified;
    }

    public void setOnHoldModified(Long onHoldModified) {
        this.onHoldModified = onHoldModified;
    }

}
