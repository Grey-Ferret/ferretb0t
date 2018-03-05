
package net.greyferret.ferretb0t.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attachments_ {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timing")
    @Expose
    private Timing_ timing;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timing_ getTiming() {
        return timing;
    }

    public void setTiming(Timing_ timing) {
        this.timing = timing;
    }

}
