
package dev.greyferret.ferretbot.entity.json.twitch.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Datum withId(String id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Datum withLogin(String login) {
        this.login = login;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Datum withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Datum withType(String type) {
        this.type = type;
        return this;
    }

    public String getBroadcasterType() {
        return broadcasterType;
    }

    public void setBroadcasterType(String broadcasterType) {
        this.broadcasterType = broadcasterType;
    }

    public Datum withBroadcasterType(String broadcasterType) {
        this.broadcasterType = broadcasterType;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Datum withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Datum withProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public String getOfflineImageUrl() {
        return offlineImageUrl;
    }

    public void setOfflineImageUrl(String offlineImageUrl) {
        this.offlineImageUrl = offlineImageUrl;
    }

    public Datum withOfflineImageUrl(String offlineImageUrl) {
        this.offlineImageUrl = offlineImageUrl;
        return this;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public Datum withViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

}
