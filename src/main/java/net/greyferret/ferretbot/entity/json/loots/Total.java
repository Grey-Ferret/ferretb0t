
package net.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total {

    @SerializedName("lts")
    @Expose
    private Long lts;
    @SerializedName("usd")
    @Expose
    private Double usd;
    @SerializedName("usd_current")
    @Expose
    private Double usdCurrent;
    @SerializedName("usd_paid")
    @Expose
    private Double usdPaid;
    @SerializedName("usd_paid_extras")
    @Expose
    private Long usdPaidExtras;
    @SerializedName("usd_paid_t")
    @Expose
    private Long usdPaidT;
    @SerializedName("usd_pending")
    @Expose
    private Double usdPending;
    @SerializedName("usd_t")
    @Expose
    private Long usdT;
    @SerializedName("transactions")
    @Expose
    private Long transactions;
    @SerializedName("transactions_current")
    @Expose
    private Long transactionsCurrent;
    @SerializedName("transactions_pending")
    @Expose
    private Long transactionsPending;
    @SerializedName("transactions_paid")
    @Expose
    private Long transactionsPaid;

    public Long getLts() {
        return lts;
    }

    public void setLts(Long lts) {
        this.lts = lts;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Double getUsdCurrent() {
        return usdCurrent;
    }

    public void setUsdCurrent(Double usdCurrent) {
        this.usdCurrent = usdCurrent;
    }

    public Double getUsdPaid() {
        return usdPaid;
    }

    public void setUsdPaid(Double usdPaid) {
        this.usdPaid = usdPaid;
    }

    public Long getUsdPaidExtras() {
        return usdPaidExtras;
    }

    public void setUsdPaidExtras(Long usdPaidExtras) {
        this.usdPaidExtras = usdPaidExtras;
    }

    public Long getUsdPaidT() {
        return usdPaidT;
    }

    public void setUsdPaidT(Long usdPaidT) {
        this.usdPaidT = usdPaidT;
    }

    public Double getUsdPending() {
        return usdPending;
    }

    public void setUsdPending(Double usdPending) {
        this.usdPending = usdPending;
    }

    public Long getUsdT() {
        return usdT;
    }

    public void setUsdT(Long usdT) {
        this.usdT = usdT;
    }

    public Long getTransactions() {
        return transactions;
    }

    public void setTransactions(Long transactions) {
        this.transactions = transactions;
    }

    public Long getTransactionsCurrent() {
        return transactionsCurrent;
    }

    public void setTransactionsCurrent(Long transactionsCurrent) {
        this.transactionsCurrent = transactionsCurrent;
    }

    public Long getTransactionsPending() {
        return transactionsPending;
    }

    public void setTransactionsPending(Long transactionsPending) {
        this.transactionsPending = transactionsPending;
    }

    public Long getTransactionsPaid() {
        return transactionsPaid;
    }

    public void setTransactionsPaid(Long transactionsPaid) {
        this.transactionsPaid = transactionsPaid;
    }

}
