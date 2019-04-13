
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class From__ {

    @SerializedName("account")
    @Expose
    private Account__ account;

    public Account__ getAccount() {
        return account;
    }

    public void setAccount(Account__ account) {
        this.account = account;
    }

}
