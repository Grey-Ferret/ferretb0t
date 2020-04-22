package dev.greyferret.ferretbot.security.twitch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenJson {
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("expires_in")
    @Expose
    private Long pagination;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
}
