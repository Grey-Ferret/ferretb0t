
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentDetails_ {

    @SerializedName("relatedPlaylists")
    @Expose
    private RelatedPlaylists_ relatedPlaylists;

    public RelatedPlaylists_ getRelatedPlaylists() {
        return relatedPlaylists;
    }

    public void setRelatedPlaylists(RelatedPlaylists_ relatedPlaylists) {
        this.relatedPlaylists = relatedPlaylists;
    }

}
