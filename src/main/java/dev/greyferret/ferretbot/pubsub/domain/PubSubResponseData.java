
package dev.greyferret.ferretbot.pubsub.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PubSubResponseData {

    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("message")
    @Expose
    private String message;

}
