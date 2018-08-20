
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Timezone {

    @SerializedName("dst")
    @Expose
    private Boolean dst;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_lc")
    @Expose
    private String nameLc;
    @SerializedName("name_uc")
    @Expose
    private String nameUc;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("offset")
    @Expose
    private String offset;
    @SerializedName("primary")
    @Expose
    private Boolean primary;
    @SerializedName("primary_name")
    @Expose
    private String primaryName;
    @SerializedName("labels")
    @Expose
    private List<String> labels = null;
    @SerializedName("regions")
    @Expose
    private List<String> regions = null;
    @SerializedName("offset_minutes")
    @Expose
    private Integer offsetMinutes;
    @SerializedName("offset_m")
    @Expose
    private Integer offsetM;
    @SerializedName("offset_s")
    @Expose
    private Integer offsetS;

    public Boolean getDst() {
        return dst;
    }

    public void setDst(Boolean dst) {
        this.dst = dst;
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

    public String getNameUc() {
        return nameUc;
    }

    public void setNameUc(String nameUc) {
        this.nameUc = nameUc;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public Integer getOffsetMinutes() {
        return offsetMinutes;
    }

    public void setOffsetMinutes(Integer offsetMinutes) {
        this.offsetMinutes = offsetMinutes;
    }

    public Integer getOffsetM() {
        return offsetM;
    }

    public void setOffsetM(Integer offsetM) {
        this.offsetM = offsetM;
    }

    public Integer getOffsetS() {
        return offsetS;
    }

    public void setOffsetS(Integer offsetS) {
        this.offsetS = offsetS;
    }

}
