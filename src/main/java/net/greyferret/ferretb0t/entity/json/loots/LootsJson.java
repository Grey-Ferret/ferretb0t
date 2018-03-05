
package net.greyferret.ferretb0t.entity.json.loots;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LootsJson {

    @SerializedName("_t")
    @Expose
    private Long t;
    @SerializedName("_t_ms")
    @Expose
    private Long tMs;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("status")
    @Expose
    private Long status;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    public Long getTMs() {
        return tMs;
    }

    public void setTMs(Long tMs) {
        this.tMs = tMs;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
