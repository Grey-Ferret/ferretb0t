
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Configuration {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("dst")
    @Expose
    private Boolean dst;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("languages")
    @Expose
    private List<String> languages = null;
    @SerializedName("leaderboards")
    @Expose
    private Leaderboards leaderboards;
    @SerializedName("overlay")
    @Expose
    private Overlay overlay;
    @SerializedName("timezone")
    @Expose
    private Timezone timezone;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getDst() {
        return dst;
    }

    public void setDst(Boolean dst) {
        this.dst = dst;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Leaderboards getLeaderboards() {
        return leaderboards;
    }

    public void setLeaderboards(Leaderboards leaderboards) {
        this.leaderboards = leaderboards;
    }

    public Overlay getOverlay() {
        return overlay;
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

}
