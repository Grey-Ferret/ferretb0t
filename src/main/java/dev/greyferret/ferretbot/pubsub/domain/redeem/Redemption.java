
package dev.greyferret.ferretbot.pubsub.domain.redeem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Redemption {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("redeemed_at")
    @Expose
    private String redeemedAt;
    @SerializedName("reward")
    @Expose
    private Reward reward;
    @SerializedName("status")
    @Expose
    private String status;
}
