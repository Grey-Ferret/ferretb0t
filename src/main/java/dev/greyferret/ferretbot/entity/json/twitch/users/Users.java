
package dev.greyferret.ferretbot.entity.json.twitch.users;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Users {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Users withData(List<Datum> data) {
        this.data = data;
        return this;
    }

}
