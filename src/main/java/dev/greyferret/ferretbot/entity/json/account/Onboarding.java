
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Onboarding {

    @SerializedName("countries_allowed")
    @Expose
    private List<String> countriesAllowed = null;
    @SerializedName("countries_preferred")
    @Expose
    private List<String> countriesPreferred = null;
    @SerializedName("steps_allowed")
    @Expose
    private List<String> stepsAllowed = null;
    @SerializedName("networks")
    @Expose
    private List<String> networks = null;
    @SerializedName("networks_allowed")
    @Expose
    private List<String> networksAllowed = null;
    @SerializedName("url")
    @Expose
    private Url url;

    public List<String> getCountriesAllowed() {
        return countriesAllowed;
    }

    public void setCountriesAllowed(List<String> countriesAllowed) {
        this.countriesAllowed = countriesAllowed;
    }

    public List<String> getCountriesPreferred() {
        return countriesPreferred;
    }

    public void setCountriesPreferred(List<String> countriesPreferred) {
        this.countriesPreferred = countriesPreferred;
    }

    public List<String> getStepsAllowed() {
        return stepsAllowed;
    }

    public void setStepsAllowed(List<String> stepsAllowed) {
        this.stepsAllowed = stepsAllowed;
    }

    public List<String> getNetworks() {
        return networks;
    }

    public void setNetworks(List<String> networks) {
        this.networks = networks;
    }

    public List<String> getNetworksAllowed() {
        return networksAllowed;
    }

    public void setNetworksAllowed(List<String> networksAllowed) {
        this.networksAllowed = networksAllowed;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

}
