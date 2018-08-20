
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Twitch_ {

    @SerializedName("_id")
    @Expose
    private Integer id;
    @SerializedName("connect")
    @Expose
    private Connect_____ connect;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("images")
    @Expose
    private Images__ images;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_lc")
    @Expose
    private String nameLc;
    @SerializedName("url")
    @Expose
    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Connect_____ getConnect() {
        return connect;
    }

    public void setConnect(Connect_____ connect) {
        this.connect = connect;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Images__ getImages() {
        return images;
    }

    public void setImages(Images__ images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLc() {
        return nameLc;
    }

    public void setNameLc(String nameLc) {
        this.nameLc = nameLc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
