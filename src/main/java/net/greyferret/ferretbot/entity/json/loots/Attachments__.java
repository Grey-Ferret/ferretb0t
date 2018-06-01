
package net.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attachments__ {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timing")
    @Expose
    private Timing__ timing;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timing__ getTiming() {
        return timing;
    }

    public void setTiming(Timing__ timing) {
        this.timing = timing;
    }

}
