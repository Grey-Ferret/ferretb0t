
package dev.greyferret.ferretbot.entity.json.twitch.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TwitchStreamsJson {

    @SerializedName("data")
    @Expose
    private List<StreamData> data = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

}
