
package dev.greyferret.ferretbot.entity.json.twitch.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class TwitchStreamsJson {

    @SerializedName("data")
    @Expose
    private List<StreamData> data = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

}
