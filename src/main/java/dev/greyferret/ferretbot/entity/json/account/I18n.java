
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class I18n {

    @SerializedName("countries_browser")
    @Expose
    private List<Object> countriesBrowser = null;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("languages_browser")
    @Expose
    private List<Object> languagesBrowser = null;

    public List<Object> getCountriesBrowser() {
        return countriesBrowser;
    }

    public void setCountriesBrowser(List<Object> countriesBrowser) {
        this.countriesBrowser = countriesBrowser;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Object> getLanguagesBrowser() {
        return languagesBrowser;
    }

    public void setLanguagesBrowser(List<Object> languagesBrowser) {
        this.languagesBrowser = languagesBrowser;
    }

}
