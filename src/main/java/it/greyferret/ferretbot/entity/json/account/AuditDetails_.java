
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuditDetails_ {

    @SerializedName("communityGuidelinesGoodStanding")
    @Expose
    private Boolean communityGuidelinesGoodStanding;
    @SerializedName("copyrightStrikesGoodStanding")
    @Expose
    private Boolean copyrightStrikesGoodStanding;
    @SerializedName("contentIdClaimsGoodStanding")
    @Expose
    private Boolean contentIdClaimsGoodStanding;

    public Boolean getCommunityGuidelinesGoodStanding() {
        return communityGuidelinesGoodStanding;
    }

    public void setCommunityGuidelinesGoodStanding(Boolean communityGuidelinesGoodStanding) {
        this.communityGuidelinesGoodStanding = communityGuidelinesGoodStanding;
    }

    public Boolean getCopyrightStrikesGoodStanding() {
        return copyrightStrikesGoodStanding;
    }

    public void setCopyrightStrikesGoodStanding(Boolean copyrightStrikesGoodStanding) {
        this.copyrightStrikesGoodStanding = copyrightStrikesGoodStanding;
    }

    public Boolean getContentIdClaimsGoodStanding() {
        return contentIdClaimsGoodStanding;
    }

    public void setContentIdClaimsGoodStanding(Boolean contentIdClaimsGoodStanding) {
        this.contentIdClaimsGoodStanding = contentIdClaimsGoodStanding;
    }

}
