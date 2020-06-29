
package dev.greyferret.ferretbot.entity.json.twitch.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Users {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
}
