
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("_count")
    @Expose
    private Count count;
    @SerializedName("_total")
    @Expose
    private Total total;
    @SerializedName("in_queue")
    @Expose
    private List<Object> inQueue = null;
    @SerializedName("on_hold")
    @Expose
    private List<Object> onHold = null;
    @SerializedName("ok")
    @Expose
    private List<Ok> ok = null;
    @SerializedName("running")
    @Expose
    private List<Object> running = null;
    @SerializedName("failed")
    @Expose
    private List<Failed> failed = null;
    @SerializedName("rejected")
    @Expose
    private List<Rejected> rejected = null;

    public Count getCount() {
        return count;
    }

    public void setCount(Count count) {
        this.count = count;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public List<Object> getInQueue() {
        return inQueue;
    }

    public void setInQueue(List<Object> inQueue) {
        this.inQueue = inQueue;
    }

    public List<Object> getOnHold() {
        return onHold;
    }

    public void setOnHold(List<Object> onHold) {
        this.onHold = onHold;
    }

    public List<Ok> getOk() {
        return ok;
    }

    public void setOk(List<Ok> ok) {
        this.ok = ok;
    }

    public List<Object> getRunning() {
        return running;
    }

    public void setRunning(List<Object> running) {
        this.running = running;
    }

    public List<Failed> getFailed() {
        return failed;
    }

    public void setFailed(List<Failed> failed) {
        this.failed = failed;
    }

    public List<Rejected> getRejected() {
        return rejected;
    }

    public void setRejected(List<Rejected> rejected) {
        this.rejected = rejected;
    }

}
