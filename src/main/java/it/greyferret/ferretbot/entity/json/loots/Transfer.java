
package it.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transfer {

    @SerializedName("lts")
    @Expose
    private Long lts;
    @SerializedName("lts_from")
    @Expose
    private LtsFrom ltsFrom;

    public Long getLts() {
        return lts;
    }

    public void setLts(Long lts) {
        this.lts = lts;
    }

    public LtsFrom getLtsFrom() {
        return ltsFrom;
    }

    public void setLtsFrom(LtsFrom ltsFrom) {
        this.ltsFrom = ltsFrom;
    }

}
