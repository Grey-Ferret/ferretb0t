
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total___ {

    @SerializedName("balance")
    @Expose
    private Balance__ balance;

    public Balance__ getBalance() {
        return balance;
    }

    public void setBalance(Balance__ balance) {
        this.balance = balance;
    }

}
