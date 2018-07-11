
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Channel__ {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("keywords")
    @Expose
    private String keywords;
    @SerializedName("defaultTab")
    @Expose
    private String defaultTab;
    @SerializedName("showRelatedChannels")
    @Expose
    private Boolean showRelatedChannels;
    @SerializedName("showBrowseView")
    @Expose
    private Boolean showBrowseView;
    @SerializedName("featuredChannelsTitle")
    @Expose
    private String featuredChannelsTitle;
    @SerializedName("unsubscribedTrailer")
    @Expose
    private String unsubscribedTrailer;
    @SerializedName("profileColor")
    @Expose
    private String profileColor;
    @SerializedName("country")
    @Expose
    private String country;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDefaultTab() {
        return defaultTab;
    }

    public void setDefaultTab(String defaultTab) {
        this.defaultTab = defaultTab;
    }

    public Boolean getShowRelatedChannels() {
        return showRelatedChannels;
    }

    public void setShowRelatedChannels(Boolean showRelatedChannels) {
        this.showRelatedChannels = showRelatedChannels;
    }

    public Boolean getShowBrowseView() {
        return showBrowseView;
    }

    public void setShowBrowseView(Boolean showBrowseView) {
        this.showBrowseView = showBrowseView;
    }

    public String getFeaturedChannelsTitle() {
        return featuredChannelsTitle;
    }

    public void setFeaturedChannelsTitle(String featuredChannelsTitle) {
        this.featuredChannelsTitle = featuredChannelsTitle;
    }

    public String getUnsubscribedTrailer() {
        return unsubscribedTrailer;
    }

    public void setUnsubscribedTrailer(String unsubscribedTrailer) {
        this.unsubscribedTrailer = unsubscribedTrailer;
    }

    public String getProfileColor() {
        return profileColor;
    }

    public void setProfileColor(String profileColor) {
        this.profileColor = profileColor;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
