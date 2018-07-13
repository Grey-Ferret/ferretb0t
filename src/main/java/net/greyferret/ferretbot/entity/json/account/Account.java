
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Account {

    @SerializedName("configuration")
    @Expose
    private Configuration configuration;
    @SerializedName("has_session")
    @Expose
    private Boolean hasSession;
    @SerializedName("has_session_chroma")
    @Expose
    private Boolean hasSessionChroma;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("balance")
    @Expose
    private Balance balance;
    @SerializedName("connections")
    @Expose
    private Connections connections;
    @SerializedName("connections_bots")
    @Expose
    private ConnectionsBots connectionsBots;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("handle")
    @Expose
    private String handle;
    @SerializedName("handle_at")
    @Expose
    private String handleAt;
    @SerializedName("handle_lc")
    @Expose
    private String handleLc;
    @SerializedName("handle_sl")
    @Expose
    private String handleSl;
    @SerializedName("handle_uc")
    @Expose
    private String handleUc;
    @SerializedName("has_websocket")
    @Expose
    private Boolean hasWebsocket;
    @SerializedName("legal")
    @Expose
    private Legal legal;
    @SerializedName("logged_in")
    @Expose
    private Boolean loggedIn;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_lc")
    @Expose
    private String nameLc;
    @SerializedName("name_sl")
    @Expose
    private String nameSl;
    @SerializedName("name_uc")
    @Expose
    private String nameUc;
    @SerializedName("origin")
    @Expose
    private Origin origin;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("targeting")
    @Expose
    private Targeting targeting;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("token_chroma")
    @Expose
    private String tokenChroma;
    @SerializedName("token_chroma_short")
    @Expose
    private String tokenChromaShort;
    @SerializedName("user")
    @Expose
    private User__ user;
    @SerializedName("is_beta_user")
    @Expose
    private Boolean isBetaUser;
    @SerializedName("has_beta_group")
    @Expose
    private Boolean hasBetaGroup;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("modified")
    @Expose
    private Integer modified;
    @SerializedName("groups")
    @Expose
    private List<Group__> groups = null;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Boolean getHasSession() {
        return hasSession;
    }

    public void setHasSession(Boolean hasSession) {
        this.hasSession = hasSession;
    }

    public Boolean getHasSessionChroma() {
        return hasSessionChroma;
    }

    public void setHasSessionChroma(Boolean hasSessionChroma) {
        this.hasSessionChroma = hasSessionChroma;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    public ConnectionsBots getConnectionsBots() {
        return connectionsBots;
    }

    public void setConnectionsBots(ConnectionsBots connectionsBots) {
        this.connectionsBots = connectionsBots;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getHandleAt() {
        return handleAt;
    }

    public void setHandleAt(String handleAt) {
        this.handleAt = handleAt;
    }

    public String getHandleLc() {
        return handleLc;
    }

    public void setHandleLc(String handleLc) {
        this.handleLc = handleLc;
    }

    public String getHandleSl() {
        return handleSl;
    }

    public void setHandleSl(String handleSl) {
        this.handleSl = handleSl;
    }

    public String getHandleUc() {
        return handleUc;
    }

    public void setHandleUc(String handleUc) {
        this.handleUc = handleUc;
    }

    public Boolean getHasWebsocket() {
        return hasWebsocket;
    }

    public void setHasWebsocket(Boolean hasWebsocket) {
        this.hasWebsocket = hasWebsocket;
    }

    public Legal getLegal() {
        return legal;
    }

    public void setLegal(Legal legal) {
        this.legal = legal;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLc() {
        return nameLc;
    }

    public void setNameLc(String nameLc) {
        this.nameLc = nameLc;
    }

    public String getNameSl() {
        return nameSl;
    }

    public void setNameSl(String nameSl) {
        this.nameSl = nameSl;
    }

    public String getNameUc() {
        return nameUc;
    }

    public void setNameUc(String nameUc) {
        this.nameUc = nameUc;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setTargeting(Targeting targeting) {
        this.targeting = targeting;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenChroma() {
        return tokenChroma;
    }

    public void setTokenChroma(String tokenChroma) {
        this.tokenChroma = tokenChroma;
    }

    public String getTokenChromaShort() {
        return tokenChromaShort;
    }

    public void setTokenChromaShort(String tokenChromaShort) {
        this.tokenChromaShort = tokenChromaShort;
    }

    public User__ getUser() {
        return user;
    }

    public void setUser(User__ user) {
        this.user = user;
    }

    public Boolean getIsBetaUser() {
        return isBetaUser;
    }

    public void setIsBetaUser(Boolean isBetaUser) {
        this.isBetaUser = isBetaUser;
    }

    public Boolean getHasBetaGroup() {
        return hasBetaGroup;
    }

    public void setHasBetaGroup(Boolean hasBetaGroup) {
        this.hasBetaGroup = hasBetaGroup;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Integer getModified() {
        return modified;
    }

    public void setModified(Integer modified) {
        this.modified = modified;
    }

    public List<Group__> getGroups() {
        return groups;
    }

    public void setGroups(List<Group__> groups) {
        this.groups = groups;
    }

}
