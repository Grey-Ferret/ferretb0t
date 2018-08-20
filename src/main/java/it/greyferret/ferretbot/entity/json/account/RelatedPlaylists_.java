
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RelatedPlaylists_ {

    @SerializedName("likes")
    @Expose
    private String likes;
    @SerializedName("favorites")
    @Expose
    private String favorites;
    @SerializedName("uploads")
    @Expose
    private String uploads;
    @SerializedName("watchHistory")
    @Expose
    private String watchHistory;
    @SerializedName("watchLater")
    @Expose
    private String watchLater;

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public String getUploads() {
        return uploads;
    }

    public void setUploads(String uploads) {
        this.uploads = uploads;
    }

    public String getWatchHistory() {
        return watchHistory;
    }

    public void setWatchHistory(String watchHistory) {
        this.watchHistory = watchHistory;
    }

    public String getWatchLater() {
        return watchLater;
    }

    public void setWatchLater(String watchLater) {
        this.watchLater = watchLater;
    }

}
