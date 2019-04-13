
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Media {

    @SerializedName("allowed_extensions")
    @Expose
    private List<String> allowedExtensions = null;
    @SerializedName("allowed_mimetypes")
    @Expose
    private List<String> allowedMimetypes = null;
    @SerializedName("allowed_sources")
    @Expose
    private List<String> allowedSources = null;
    @SerializedName("services_available")
    @Expose
    private List<String> servicesAvailable = null;
    @SerializedName("services_default")
    @Expose
    private String servicesDefault;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("path")
    @Expose
    private String path;

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public List<String> getAllowedMimetypes() {
        return allowedMimetypes;
    }

    public void setAllowedMimetypes(List<String> allowedMimetypes) {
        this.allowedMimetypes = allowedMimetypes;
    }

    public List<String> getAllowedSources() {
        return allowedSources;
    }

    public void setAllowedSources(List<String> allowedSources) {
        this.allowedSources = allowedSources;
    }

    public List<String> getServicesAvailable() {
        return servicesAvailable;
    }

    public void setServicesAvailable(List<String> servicesAvailable) {
        this.servicesAvailable = servicesAvailable;
    }

    public String getServicesDefault() {
        return servicesDefault;
    }

    public void setServicesDefault(String servicesDefault) {
        this.servicesDefault = servicesDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
