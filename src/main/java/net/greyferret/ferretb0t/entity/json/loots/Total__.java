
package net.greyferret.ferretb0t.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total__ {

    @SerializedName("balance")
    @Expose
    private Balance_ balance;

    public Balance_ getBalance() {
        return balance;
    }

    public void setBalance(Balance_ balance) {
        this.balance = balance;
    }

}
