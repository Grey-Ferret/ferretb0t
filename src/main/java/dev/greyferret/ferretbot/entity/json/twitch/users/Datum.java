
package dev.greyferret.ferretbot.entity.json.twitch.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("broadcaster_type")
    @Expose
    private String broadcasterType;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("profile_image_url")
    @Expose
    private String profileImageUrl;
    @SerializedName("offline_image_url")
    @Expose
    private String offlineImageUrl;
    @SerializedName("view_count")
    @Expose
    private int viewCount;
}
