
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Snippet_ {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("customUrl")
    @Expose
    private String customUrl;
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("thumbnails")
    @Expose
    private Thumbnails_ thumbnails;
    @SerializedName("localized")
    @Expose
    private Localized_ localized;
    @SerializedName("country")
    @Expose
    private String country;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Thumbnails_ getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails_ thumbnails) {
        this.thumbnails = thumbnails;
    }

    public Localized_ getLocalized() {
        return localized;
    }

    public void setLocalized(Localized_ localized) {
        this.localized = localized;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
