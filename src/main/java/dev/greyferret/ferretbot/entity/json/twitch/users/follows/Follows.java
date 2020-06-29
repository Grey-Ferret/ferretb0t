
package dev.greyferret.ferretbot.entity.json.twitch.users.follows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Follows {

    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;
}
