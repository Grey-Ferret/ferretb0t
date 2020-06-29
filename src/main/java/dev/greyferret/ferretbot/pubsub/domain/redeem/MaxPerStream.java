
package dev.greyferret.ferretbot.pubsub.domain.redeem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class MaxPerStream {

    @SerializedName("is_enabled")
    @Expose
    private Boolean isEnabled;
    @SerializedName("max_per_stream")
    @Expose
    private Integer maxPerStream;
}
