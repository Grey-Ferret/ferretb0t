
package dev.greyferret.ferretbot.entity.json.twitch.users.follows;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Datum {

    @SerializedName("from_id")
    @Expose
    private String fromId;
    @SerializedName("from_name")
    @Expose
    private String fromName;
    @SerializedName("to_id")
    @Expose
    private String toId;
    @SerializedName("to_name")
    @Expose
    private String toName;
    @SerializedName("followed_at")
    @Expose
    private String followedAt;
}
