
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Youtube_ {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("connect")
    @Expose
    private Connect______ connect;
    @SerializedName("images")
    @Expose
    private Images___ images;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_lc")
    @Expose
    private String nameLc;
    @SerializedName("url")
    @Expose
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Connect______ getConnect() {
        return connect;
    }

    public void setConnect(Connect______ connect) {
        this.connect = connect;
    }

    public Images___ getImages() {
        return images;
    }

    public void setImages(Images___ images) {
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
