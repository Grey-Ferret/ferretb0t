
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connect {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("supports_state")
    @Expose
    private Boolean supportsState;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getSupportsState() {
        return supportsState;
    }

    public void setSupportsState(Boolean supportsState) {
        this.supportsState = supportsState;
    }

}
