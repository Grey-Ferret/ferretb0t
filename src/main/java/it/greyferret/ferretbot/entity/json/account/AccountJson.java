
package it.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountJson {

    @SerializedName("api")
    @Expose
    private Api api;
    @SerializedName("caster_app")
    @Expose
    private Boolean casterApp;
    @SerializedName("cdn")
    @Expose
    private String cdn;
    @SerializedName("cdn_proxy")
    @Expose
    private String cdnProxy;
    @SerializedName("config")
    @Expose
    private Config config;
    @SerializedName("css")
    @Expose
    private Css css;
    @SerializedName("douyu")
    @Expose
    private Douyu douyu;
    @SerializedName("env")
    @Expose
    private String env;
    @SerializedName("facebook")
    @Expose
    private Facebook facebook;
    @SerializedName("google")
    @Expose
    private Google google;
    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("i18n")
    @Expose
    private I18n i18n;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("link_alt")
    @Expose
    private String linkAlt;
    @SerializedName("mixer")
    @Expose
    private Mixer mixer;
    @SerializedName("mobile")
    @Expose
    private Boolean mobile;
    @SerializedName("onboarding")
    @Expose
    private Onboarding onboarding;
    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("paypal")
    @Expose
    private Paypal paypal;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("protocol")
    @Expose
    private String protocol;
    @SerializedName("req")
    @Expose
    private Req req;
    @SerializedName("session")
    @Expose
    private Session session;
    @SerializedName("smashcast")
    @Expose
    private Smashcast smashcast;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("streaming_networks")
    @Expose
    private List<String> streamingNetworks = null;
    @SerializedName("twitch")
    @Expose
    private Twitch__ twitch;
    @SerializedName("util")
    @Expose
    private Util util;
    @SerializedName("usd_int_factor")
    @Expose
    private Integer usdIntFactor;
    @SerializedName("youtube")
    @Expose
    private Youtube__ youtube;
    @SerializedName("webhooks")
    @Expose
    private Webhooks webhooks;
    @SerializedName("dictionaries")
    @Expose
    private Dictionaries dictionaries;

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Boolean getCasterApp() {
        return casterApp;
    }

    public void setCasterApp(Boolean casterApp) {
        this.casterApp = casterApp;
    }

    public String getCdn() {
        return cdn;
    }

    public void setCdn(String cdn) {
        this.cdn = cdn;
    }

    public String getCdnProxy() {
        return cdnProxy;
    }

    public void setCdnProxy(String cdnProxy) {
        this.cdnProxy = cdnProxy;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Css getCss() {
        return css;
    }

    public void setCss(Css css) {
        this.css = css;
    }

    public Douyu getDouyu() {
        return douyu;
    }

    public void setDouyu(Douyu douyu) {
        this.douyu = douyu;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public Google getGoogle() {
        return google;
    }

    public void setGoogle(Google google) {
        this.google = google;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public I18n getI18n() {
        return i18n;
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkAlt() {
        return linkAlt;
    }

    public void setLinkAlt(String linkAlt) {
        this.linkAlt = linkAlt;
    }

    public Mixer getMixer() {
        return mixer;
    }

    public void setMixer(Mixer mixer) {
        this.mixer = mixer;
    }

    public Boolean getMobile() {
        return mobile;
    }

    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }

    public Onboarding getOnboarding() {
        return onboarding;
    }

    public void setOnboarding(Onboarding onboarding) {
        this.onboarding = onboarding;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Paypal getPaypal() {
        return paypal;
    }

    public void setPaypal(Paypal paypal) {
        this.paypal = paypal;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Req getReq() {
        return req;
    }

    public void setReq(Req req) {
        this.req = req;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Smashcast getSmashcast() {
        return smashcast;
    }

    public void setSmashcast(Smashcast smashcast) {
        this.smashcast = smashcast;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<String> getStreamingNetworks() {
        return streamingNetworks;
    }

    public void setStreamingNetworks(List<String> streamingNetworks) {
        this.streamingNetworks = streamingNetworks;
    }

    public Twitch__ getTwitch() {
        return twitch;
    }

    public void setTwitch(Twitch__ twitch) {
        this.twitch = twitch;
    }

    public Util getUtil() {
        return util;
    }

    public void setUtil(Util util) {
        this.util = util;
    }

    public Integer getUsdIntFactor() {
        return usdIntFactor;
    }

    public void setUsdIntFactor(Integer usdIntFactor) {
        this.usdIntFactor = usdIntFactor;
    }

    public Youtube__ getYoutube() {
        return youtube;
    }

    public void setYoutube(Youtube__ youtube) {
        this.youtube = youtube;
    }

    public Webhooks getWebhooks() {
        return webhooks;
    }

    public void setWebhooks(Webhooks webhooks) {
        this.webhooks = webhooks;
    }

    public Dictionaries getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Dictionaries dictionaries) {
        this.dictionaries = dictionaries;
    }

}
