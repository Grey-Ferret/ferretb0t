
package dev.greyferret.ferretbot.pubsub.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.greyferret.ferretbot.pubsub.enums.PubSubType;
import lombok.Data;

@Data
public class PubSubResponse {

    @SerializedName("type")
    @Expose
    private PubSubType type;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private PubSubResponseData data;
}
