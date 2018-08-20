
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group_ {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("handle_lc")
    @Expose
    private String handleLc;
    @SerializedName("slug")
    @Expose
    private String slug;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHandleLc() {
        return handleLc;
    }

    public void setHandleLc(String handleLc) {
        this.handleLc = handleLc;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

}
