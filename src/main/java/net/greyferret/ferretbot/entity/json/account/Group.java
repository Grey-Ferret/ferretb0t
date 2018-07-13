
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("group")
    @Expose
    private Group_ group;
    @SerializedName("_t")
    @Expose
    private Integer t;
    @SerializedName("terms")
    @Expose
    private String terms;

    public Group_ getGroup() {
        return group;
    }

    public void setGroup(Group_ group) {
        this.group = group;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

}
