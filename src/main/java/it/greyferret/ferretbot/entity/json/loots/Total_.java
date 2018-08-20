
package it.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total_ {

    @SerializedName("balance")
    @Expose
    private Balance balance;

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

}
