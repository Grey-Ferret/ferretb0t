
package dev.greyferret.ferretbot.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Count {

    @SerializedName("in_queue")
    @Expose
    private Long inQueue;
    @SerializedName("on_hold")
    @Expose
    private Long onHold;
    @SerializedName("ok")
    @Expose
    private Long ok;
    @SerializedName("running")
    @Expose
    private Long running;
    @SerializedName("failed")
    @Expose
    private Long failed;
    @SerializedName("rejected")
    @Expose
    private Long rejected;
    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("ok_muted")
    @Expose
    private Long okMuted;

    public Long getInQueue() {
        return inQueue;
    }

    public void setInQueue(Long inQueue) {
        this.inQueue = inQueue;
    }

    public Long getOnHold() {
        return onHold;
    }

    public void setOnHold(Long onHold) {
        this.onHold = onHold;
    }

    public Long getOk() {
        return ok;
    }

    public void setOk(Long ok) {
        this.ok = ok;
    }

    public Long getRunning() {
        return running;
    }

    public void setRunning(Long running) {
        this.running = running;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getRejected() {
        return rejected;
    }

    public void setRejected(Long rejected) {
        this.rejected = rejected;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getOkMuted() {
        return okMuted;
    }

    public void setOkMuted(Long okMuted) {
        this.okMuted = okMuted;
    }

}
