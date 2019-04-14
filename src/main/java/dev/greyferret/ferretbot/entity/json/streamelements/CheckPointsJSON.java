
package dev.greyferret.ferretbot.entity.json.streamelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckPointsJSON {

    @SerializedName("channel")
    @Expose
    private String channel;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("points")
    @Expose
    private Long points;
    @SerializedName("pointsAlltime")
    @Expose
    private Long pointsAlltime;
    @SerializedName("rank")
    @Expose
    private Long rank;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getPointsAlltime() {
        return pointsAlltime;
    }

    public void setPointsAlltime(Long pointsAlltime) {
        this.pointsAlltime = pointsAlltime;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

}
