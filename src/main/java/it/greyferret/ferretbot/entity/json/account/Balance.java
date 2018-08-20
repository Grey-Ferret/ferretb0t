
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Balance {

    @SerializedName("lts")
    @Expose
    private Integer lts;
    @SerializedName("usd")
    @Expose
    private Integer usd;

    public Integer getLts() {
        return lts;
    }

    public void setLts(Integer lts) {
        this.lts = lts;
    }

    public Integer getUsd() {
        return usd;
    }

    public void setUsd(Integer usd) {
        this.usd = usd;
    }

}
