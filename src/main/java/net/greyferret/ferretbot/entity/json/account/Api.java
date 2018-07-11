
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Api {

    @SerializedName("access_token")
    @Expose
    private Object accessToken;
    @SerializedName("context")
    @Expose
    private Object context;
    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("protocol")
    @Expose
    private String protocol;
    @SerializedName("proxy")
    @Expose
    private String proxy;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("server_host")
    @Expose
    private String serverHost;
    @SerializedName("server_version")
    @Expose
    private String serverVersion;

    public Object getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Object accessToken) {
        this.accessToken = accessToken;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

}
