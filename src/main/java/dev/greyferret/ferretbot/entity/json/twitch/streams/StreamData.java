
package dev.greyferret.ferretbot.entity.json.twitch.streams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class StreamData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("game_id")
    @Expose
    private String gameId;
    @SerializedName("community_ids")
    @Expose
    private List<String> communityIds = null;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("viewer_count")
    @Expose
    private Integer viewerCount;
    @SerializedName("started_at")
    @Expose
    private String startedAt;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl;
}
