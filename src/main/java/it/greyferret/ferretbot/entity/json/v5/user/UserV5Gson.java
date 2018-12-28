
package it.greyferret.ferretbot.entity.json.v5.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserV5Gson {

    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("_id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("bio")
    @Expose
    private Object bio;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("_links")
    @Expose
    private Links links;

    /**
     * No args constructor for use in serialization
     * 
     */
    public UserV5Gson() {
    }

    /**
     * 
     * @param updatedAt
     * @param id
     * @param logo
     * @param bio
     * @param createdAt
     * @param name
     * @param links
     * @param type
     * @param displayName
     */
    public UserV5Gson(String displayName, Integer id, String name, String type, Object bio, String createdAt, String updatedAt, String logo, Links links) {
        super();
        this.displayName = displayName;
        this.id = id;
        this.name = name;
        this.type = type;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.logo = logo;
        this.links = links;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserV5Gson withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserV5Gson withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserV5Gson withName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserV5Gson withType(String type) {
        this.type = type;
        return this;
    }

    public Object getBio() {
        return bio;
    }

    public void setBio(Object bio) {
        this.bio = bio;
    }

    public UserV5Gson withBio(Object bio) {
        this.bio = bio;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserV5Gson withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserV5Gson withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public UserV5Gson withLogo(String logo) {
        this.logo = logo;
        return this;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public UserV5Gson withLinks(Links links) {
        this.links = links;
        return this;
    }
}
