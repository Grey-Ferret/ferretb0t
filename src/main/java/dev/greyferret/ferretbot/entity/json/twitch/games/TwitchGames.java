package dev.greyferret.ferretbot.entity.json.twitch.games;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class TwitchGames {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
}
