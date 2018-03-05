package net.greyferret.ferretb0t.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * User
 * <p>
 * Created by GreyFerret on 27.12.2017.
 */
@Entity
@Table(name = "viewer")
public class Viewer implements Serializable {
    @Id
    @Column(name = "login")
    private String login;
    @Column(name = "login_case")
    private String loginWithCase;
    @Column(name = "loots_name")
    private String lootsName;
    @Column(name = "loots_name_case")
    private String lootsNameWithCase;
    @Column(name = "points")
    private Long points;
    @Column(name = "true_points")
    private Long pointsTrue;
    @Column(name = "created")
    private Date created;
    @Column(name = "updated")
    private Date updated;
    @Column(name = "sub")
    private Boolean sub;
    @Column(name = "sub_streak")
    private int subStreak;

    private Viewer() {
    }

    public Viewer(String author) {
        this.login = author;
        this.loginWithCase = author.toLowerCase();
        this.lootsName = author;
        this.lootsNameWithCase = author.toLowerCase();
        this.points = 0L;
        this.pointsTrue = 0l;
        this.subStreak = 0;
        this.sub = false;
    }

    public String getLogin() {
        return login;
    }

    @Deprecated
    public void setLogin(String login) {
        this.login = login;
    }

    public String getLoginWithCase() {
        return loginWithCase;
    }

    public void setLoginWithCase(String loginWithCase) {
        this.loginWithCase = loginWithCase;
        this.login = loginWithCase.toLowerCase();
    }

    public String getLootsName() {
        return lootsName;
    }

    @Deprecated
    public void setLootsName(String lootsName) {
        this.lootsName = lootsName;
    }

    public String getLootsNameWithCase() {
        return lootsNameWithCase;
    }

    public void setLootsNameWithCase(String lootsNameWithCase) {
        this.lootsNameWithCase = lootsNameWithCase;
        this.lootsName = lootsNameWithCase.toLowerCase();
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public void addPoints(long i) {
        Long temp = this.points + i;
        setPoints(temp);
    }

    public void addTruePoints(long i) {
        Long temp = this.pointsTrue + i;
        setPointsTrue(temp);
        temp = this.points + i;
        setPoints(temp);
    }

    public Long getPointsTrue() {
        return pointsTrue;
    }

    public void setPointsTrue(Long pointsTrue) {
        this.pointsTrue = pointsTrue;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @PostUpdate
    private void postUpdate() {
        this.updated = new Date();
    }

    @PostPersist
    private void postPersist() {
        Date date = new Date();
        this.created = date;
        this.updated = date;
    }

    public Boolean getSub() {
        return sub;
    }

    public void setSub(Boolean sub) {
        this.sub = sub;
    }

    public int getSubStreak() {
        return subStreak;
    }

    public void setSubStreak(int subStreak) {
        this.subStreak = subStreak;
    }

    public boolean removePoints(Long points) {
        if (this.points < points)
            return false;
        Long temp = this.points;
        temp = temp - points;
        setPoints(temp);
        return true;
    }
}
