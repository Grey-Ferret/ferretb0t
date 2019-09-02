
package dev.greyferret.ferretbot.entity.json.twitch.users.follows;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Follows {

    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Follows withTotal(int total) {
        this.total = total;
        return this;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Follows withData(List<Datum> data) {
        this.data = data;
        return this;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Follows withPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

}
