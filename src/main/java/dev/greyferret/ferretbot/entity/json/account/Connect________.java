
package dev.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Connect________ {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("redirect_uri")
    @Expose
    private String redirectUri;
    @SerializedName("scopes")
    @Expose
    private List<String> scopes = null;
    @SerializedName("force_prompt")
    @Expose
    private Boolean forcePrompt;
    @SerializedName("supports_state")
    @Expose
    private Boolean supportsState;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public Boolean getForcePrompt() {
        return forcePrompt;
    }

    public void setForcePrompt(Boolean forcePrompt) {
        this.forcePrompt = forcePrompt;
    }

    public Boolean getSupportsState() {
        return supportsState;
    }

    public void setSupportsState(Boolean supportsState) {
        this.supportsState = supportsState;
    }

}
