
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Targeting {

    @SerializedName("country")
    @Expose
    private Country country;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("overlay")
    @Expose
    private String overlay;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getOverlay() {
        return overlay;
    }

    public void setOverlay(String overlay) {
        this.overlay = overlay;
    }

}
