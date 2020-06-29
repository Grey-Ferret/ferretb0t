
package dev.greyferret.ferretbot.pubsub.domain.redeem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class DefaultImage {

    @SerializedName("url_1x")
    @Expose
    private String url1x;
    @SerializedName("url_2x")
    @Expose
    private String url2x;
    @SerializedName("url_4x")
    @Expose
    private String url4x;
}
