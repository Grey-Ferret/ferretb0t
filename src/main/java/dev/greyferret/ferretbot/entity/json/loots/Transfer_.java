
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transfer_ {

    @SerializedName("lts")
    @Expose
    private Long lts;
    @SerializedName("lts_from")
    @Expose
    private LtsFrom_ ltsFrom;

    public Long getLts() {
        return lts;
    }

    public void setLts(Long lts) {
        this.lts = lts;
    }

    public LtsFrom_ getLtsFrom() {
        return ltsFrom;
    }

    public void setLtsFrom(LtsFrom_ ltsFrom) {
        this.ltsFrom = ltsFrom;
    }

}
