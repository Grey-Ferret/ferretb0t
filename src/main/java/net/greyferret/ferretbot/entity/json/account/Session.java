
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("account")
    @Expose
    private Account account;
    @SerializedName("is_admin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("is_manager")
    @Expose
    private Boolean isManager;
    @SerializedName("tipjar")
    @Expose
    private Tipjar tipjar;
    @SerializedName("delay_next_tip")
    @Expose
    private Integer delayNextTip;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean isManager) {
        this.isManager = isManager;
    }

    public Tipjar getTipjar() {
        return tipjar;
    }

    public void setTipjar(Tipjar tipjar) {
        this.tipjar = tipjar;
    }

    public Integer getDelayNextTip() {
        return delayNextTip;
    }

    public void setDelayNextTip(Integer delayNextTip) {
        this.delayNextTip = delayNextTip;
    }

}
