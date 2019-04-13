
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thumbnails_ {

    @SerializedName("default")
    @Expose
    private Default_ _default;
    @SerializedName("medium")
    @Expose
    private Medium_ medium;
    @SerializedName("high")
    @Expose
    private High_ high;

    public Default_ getDefault() {
        return _default;
    }

    public void setDefault(Default_ _default) {
        this._default = _default;
    }

    public Medium_ getMedium() {
        return medium;
    }

    public void setMedium(Medium_ medium) {
        this.medium = medium;
    }

    public High_ getHigh() {
        return high;
    }

    public void setHigh(High_ high) {
        this.high = high;
    }

}
