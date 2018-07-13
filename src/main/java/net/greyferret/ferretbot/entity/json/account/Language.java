
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Language {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("localName")
    @Expose
    private String localName;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("locales")
    @Expose
    private List<Locale> locales = null;
    @SerializedName("speakers")
    @Expose
    private Double speakers;
    @SerializedName("nameBase")
    @Expose
    private String nameBase;
    @SerializedName("nameLocale")
    @Expose
    private Boolean nameLocale;
    @SerializedName("googleCode")
    @Expose
    private String googleCode;
    @SerializedName("google")
    @Expose
    private Boolean google;
    @SerializedName("gengo")
    @Expose
    private Boolean gengo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public void setLocales(List<Locale> locales) {
        this.locales = locales;
    }

    public Double getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Double speakers) {
        this.speakers = speakers;
    }

    public String getNameBase() {
        return nameBase;
    }

    public void setNameBase(String nameBase) {
        this.nameBase = nameBase;
    }

    public Boolean getNameLocale() {
        return nameLocale;
    }

    public void setNameLocale(Boolean nameLocale) {
        this.nameLocale = nameLocale;
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public void setGoogleCode(String googleCode) {
        this.googleCode = googleCode;
    }

    public Boolean getGoogle() {
        return google;
    }

    public void setGoogle(Boolean google) {
        this.google = google;
    }

    public Boolean getGengo() {
        return gengo;
    }

    public void setGengo(Boolean gengo) {
        this.gengo = gengo;
    }

}
