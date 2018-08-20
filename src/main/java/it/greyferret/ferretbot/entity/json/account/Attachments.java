
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attachments {

    @SerializedName("allowed_mimetypes")
    @Expose
    private List<String> allowedMimetypes = null;
    @SerializedName("allowed_sources")
    @Expose
    private List<String> allowedSources = null;
    @SerializedName("maxSize")
    @Expose
    private Integer maxSize;
    @SerializedName("maxFiles")
    @Expose
    private Integer maxFiles;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("path")
    @Expose
    private String path;

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

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(Integer maxFiles) {
        this.maxFiles = maxFiles;
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
