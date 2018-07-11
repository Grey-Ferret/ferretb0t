
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Dictionaries {

    @SerializedName("countries")
    @Expose
    private List<Country_> countries = null;
    @SerializedName("freemailers")
    @Expose
    private List<String> freemailers = null;
    @SerializedName("languages")
    @Expose
    private List<Language> languages = null;
    @SerializedName("states")
    @Expose
    private States states;

    public List<Country_> getCountries() {
        return countries;
    }

    public void setCountries(List<Country_> countries) {
        this.countries = countries;
    }

    public List<String> getFreemailers() {
        return freemailers;
    }

    public void setFreemailers(List<String> freemailers) {
        this.freemailers = freemailers;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public States getStates() {
        return states;
    }

    public void setStates(States states) {
        this.states = states;
    }

}
