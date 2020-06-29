
package dev.greyferret.ferretbot.pubsub.domain.redeem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Reward {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("prompt")
    @Expose
    private String prompt;
    @SerializedName("cost")
    @Expose
    private Integer cost;
    @SerializedName("is_user_input_required")
    @Expose
    private Boolean isUserInputRequired;
    @SerializedName("is_sub_only")
    @Expose
    private Boolean isSubOnly;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("default_image")
    @Expose
    private DefaultImage defaultImage;
    @SerializedName("background_color")
    @Expose
    private String backgroundColor;
    @SerializedName("is_enabled")
    @Expose
    private Boolean isEnabled;
    @SerializedName("is_paused")
    @Expose
    private Boolean isPaused;
    @SerializedName("is_in_stock")
    @Expose
    private Boolean isInStock;
    @SerializedName("max_per_stream")
    @Expose
    private MaxPerStream maxPerStream;
    @SerializedName("should_redemptions_skip_request_queue")
    @Expose
    private Boolean shouldRedemptionsSkipRequestQueue;
    @SerializedName("template_id")
    @Expose
    private Object templateId;
    @SerializedName("updated_for_indicator_at")
    @Expose
    private String updatedForIndicatorAt;
}
