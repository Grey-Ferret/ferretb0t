
package dev.greyferret.ferretbot.pubsub.domain.redeem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@lombok.Data
public class Data {

    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("redemption")
    @Expose
    private Redemption redemption;

}
