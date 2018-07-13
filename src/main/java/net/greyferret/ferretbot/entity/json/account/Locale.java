
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Locale {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("localName")
    @Expose
    private String localName;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("nameBase")
    @Expose
    private String nameBase;
    @SerializedName("nameLocale")
    @Expose
    private String nameLocale;
    @SerializedName("googleCode")
    @Expose
    private String googleCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameBase() {
        return nameBase;
    }

    public void setNameBase(String nameBase) {
        this.nameBase = nameBase;
    }

    public String getNameLocale() {
        return nameLocale;
    }

    public void setNameLocale(String nameLocale) {
        this.nameLocale = nameLocale;
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public void setGoogleCode(String googleCode) {
        this.googleCode = googleCode;
    }

}
