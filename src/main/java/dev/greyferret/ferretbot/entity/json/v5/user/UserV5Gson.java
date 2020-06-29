
package dev.greyferret.ferretbot.entity.json.v5.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
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
}
