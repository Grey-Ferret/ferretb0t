
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class From_ {

    @SerializedName("account")
    @Expose
    private Account_ account;

    public Account_ getAccount() {
        return account;
    }

    public void setAccount(Account_ account) {
        this.account = account;
    }

}
