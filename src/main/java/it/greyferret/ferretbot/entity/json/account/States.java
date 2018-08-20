
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class States {

    @SerializedName("ca")
    @Expose
    private List<Ca> ca = null;
    @SerializedName("us")
    @Expose
    private List<U> us = null;

    public List<Ca> getCa() {
        return ca;
    }

    public void setCa(List<Ca> ca) {
        this.ca = ca;
    }

    public List<U> getUs() {
        return us;
    }

    public void setUs(List<U> us) {
        this.us = us;
    }

}
