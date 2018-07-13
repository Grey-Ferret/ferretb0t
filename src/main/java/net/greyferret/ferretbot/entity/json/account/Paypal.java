
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Paypal {

    @SerializedName("env")
    @Expose
    private String env;
    @SerializedName("use_button")
    @Expose
    private Boolean useButton;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Boolean getUseButton() {
        return useButton;
    }

    public void setUseButton(Boolean useButton) {
        this.useButton = useButton;
    }

}
