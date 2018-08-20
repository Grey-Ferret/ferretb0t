
package it.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transfer__ {

    @SerializedName("lts")
    @Expose
    private Long lts;
    @SerializedName("lts_from")
    @Expose
    private LtsFrom__ ltsFrom;

    public Long getLts() {
        return lts;
    }

    public void setLts(Long lts) {
        this.lts = lts;
    }

    public LtsFrom__ getLtsFrom() {
        return ltsFrom;
    }

    public void setLtsFrom(LtsFrom__ ltsFrom) {
        this.ltsFrom = ltsFrom;
    }

}
