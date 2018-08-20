
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Config {

    @SerializedName("countries_browser")
    @Expose
    private List<Object> countriesBrowser = null;
    @SerializedName("discord")
    @Expose
    private Discord discord;
    @SerializedName("languages_browser")
    @Expose
    private List<Object> languagesBrowser = null;
    @SerializedName("languages_available")
    @Expose
    private List<String> languagesAvailable = null;
    @SerializedName("language_fallback")
    @Expose
    private String languageFallback;
    @SerializedName("roles")
    @Expose
    private List<String> roles = null;
    @SerializedName("twitter")
    @Expose
    private Twitter twitter;
    @SerializedName("uploads")
    @Expose
    private Uploads uploads;

    public List<Object> getCountriesBrowser() {
        return countriesBrowser;
    }

    public void setCountriesBrowser(List<Object> countriesBrowser) {
        this.countriesBrowser = countriesBrowser;
    }

    public Discord getDiscord() {
        return discord;
    }

    public void setDiscord(Discord discord) {
        this.discord = discord;
    }

    public List<Object> getLanguagesBrowser() {
        return languagesBrowser;
    }

    public void setLanguagesBrowser(List<Object> languagesBrowser) {
        this.languagesBrowser = languagesBrowser;
    }

    public List<String> getLanguagesAvailable() {
        return languagesAvailable;
    }

    public void setLanguagesAvailable(List<String> languagesAvailable) {
        this.languagesAvailable = languagesAvailable;
    }

    public String getLanguageFallback() {
        return languageFallback;
    }

    public void setLanguageFallback(String languageFallback) {
        this.languageFallback = languageFallback;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public Uploads getUploads() {
        return uploads;
    }

    public void setUploads(Uploads uploads) {
        this.uploads = uploads;
    }

}
