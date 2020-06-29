
package dev.greyferret.ferretbot.entity.json.twitch.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Pagination {

    @SerializedName("cursor")
    @Expose
    private String cursor;
}
