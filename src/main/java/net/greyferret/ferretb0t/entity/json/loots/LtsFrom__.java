
package net.greyferret.ferretb0t.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LtsFrom__ {

    @SerializedName("lts")
    @Expose
    private Long lts;
    @SerializedName("lts_factor")
    @Expose
    private Long ltsFactor;
    @SerializedName("lts_per_transaction")
    @Expose
    private Long ltsPerTransaction;

    public Long getLts() {
        return lts;
    }

    public void setLts(Long lts) {
        this.lts = lts;
    }

    public Long getLtsFactor() {
        return ltsFactor;
    }

    public void setLtsFactor(Long ltsFactor) {
        this.ltsFactor = ltsFactor;
    }

    public Long getLtsPerTransaction() {
        return ltsPerTransaction;
    }

    public void setLtsPerTransaction(Long ltsPerTransaction) {
        this.ltsPerTransaction = ltsPerTransaction;
    }

}
