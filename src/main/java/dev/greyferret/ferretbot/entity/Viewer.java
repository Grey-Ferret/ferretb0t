package dev.greyferret.ferretbot.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

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
	@Column(name = "login_visual")
	private String loginVisual;
	@Column(name = "points")
	private Long points;
	@Column(name = "true_points")
	private Long pointsTrue;
	@Column(name = "created")
	private LocalDateTime created;
	@Column(name = "age")
	private LocalDateTime age;
	@Column(name = "updated_meta")
	private LocalDateTime updatedMeta;
	@Column(name = "sub", nullable = false)
	@ColumnDefault("false")
	private Boolean sub;
	@Column(name = "vip", nullable = false)
	@ColumnDefault("false")
	private Boolean vip;
	@Column(name = "sub_cumulative")
	@ColumnDefault("0")
	private int subCumulative;
	@Column(name = "sub_streak")
	@ColumnDefault("0")
	private int subStreak;
	@Column(name = "suitable_for_raffle", nullable = false)
	@ColumnDefault("true")
	private boolean suitableForRaffle;
	@Column(name = "approved", nullable = false)
	@ColumnDefault("false")
	private Boolean approved;
	@Column(name = "twitch_user_id")
	private String twitchUserId;
	@Column(name = "followed_at")
	private String followedAt;
	@Column(name = "follower")
	@ColumnDefault("false")
	private Boolean follower;

	public static int hoursToUpdateVisual = 168;

	public Viewer() {
		this.subStreak = 0;
		this.subCumulative = 0;
	}

	public Viewer(String author, ZoneId zoneId) {
		this.login = author.toLowerCase();
		this.loginVisual = author;
		this.points = 0L;
		this.pointsTrue = 0l;
		this.subStreak = 0;
		this.subCumulative = 0;
		this.sub = false;
		this.vip = false;
		this.approved = false;
		this.twitchUserId = "";
		this.followedAt = "";
		this.follower = false;
		ZonedDateTime zdt = ZonedDateTime.now(zoneId).minusHours(hoursToUpdateVisual);
		setUpdatedMeta(zdt);
		this.suitableForRaffle = true;
	}

	public String getLogin() {
		return login;
	}

	@Deprecated
	public void setLogin(String login) {
		this.login = login;
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

	public ZonedDateTime getCreated(ZoneId zoneId) {
		if (this.created == null) {
			return null;
		}
		return ZonedDateTime.of(this.created, zoneId);
	}

	public void setCreated(ZonedDateTime created) {
		this.created = created.toLocalDateTime();
	}

	public Boolean isSub() {
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

	public boolean isSuitableForRaffle() {
		return suitableForRaffle;
	}

	public void setSuitableForRaffle(boolean suitableForRaffle) {
		this.suitableForRaffle = suitableForRaffle;
	}

	public String getLoginVisual() {
		return loginVisual;
	}

	public void setLoginVisual(String loginVisual, ZoneId zoneId) {
		this.loginVisual = loginVisual;
		setUpdatedMeta(ZonedDateTime.now(zoneId));
	}

	public ZonedDateTime getUpdatedVisual(ZoneId zoneId) {
		if (this.updatedMeta == null) {
			return null;
		}
		return ZonedDateTime.of(this.updatedMeta, zoneId);
	}

	public void setUpdatedMeta(ZonedDateTime updatedVisual) {
		this.updatedMeta = updatedVisual.toLocalDateTime();
	}

	public boolean removePoints(Long points) {
		if (this.points < points)
			return false;
		Long temp = this.points;
		temp = temp - points;
		setPoints(temp);
		return true;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public ZonedDateTime getAge(ZoneId zoneId) {
		if (this.age == null) {
			return null;
		}
		return ZonedDateTime.of(this.age, zoneId);
	}

	public void setAge(ZonedDateTime age) {
		this.age = age.toLocalDateTime();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Viewer viewer = (Viewer) o;
		return login.equalsIgnoreCase(viewer.login);
	}

	@Override
	public int hashCode() {

		return Objects.hash(login);
	}

	public Boolean isVip() {
		return vip;
	}

	public void setVip(Boolean vip) {
		this.vip = vip;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public void setAge(LocalDateTime age) {
		this.age = age;
	}

	public void setUpdatedVisual(LocalDateTime updatedVisual) {
		this.updatedMeta = updatedVisual;
	}

	public int getSubCumulative() {
		return subCumulative;
	}

	public void setSubCumulative(int subCumulative) {
		this.subCumulative = subCumulative;
	}

	public String getFollowedAt() {
		return followedAt;
	}

	public void setFollowedAt(String followedAt) {
		this.followedAt = followedAt;
	}

	public boolean getFollower() {
		return follower;
	}

	public void setFollower(boolean follower) {
		this.follower = follower;
	}

	public String getTwitchUserId() {
		return twitchUserId;
	}

	public void setTwitchUserId(String twitchUserId) {
		this.twitchUserId = twitchUserId;
	}

	@Override
	public String toString() {
		return "Viewer{" +
				"login='" + login + '\'' +
				", loginVisual='" + loginVisual + '\'' +
				", points=" + points +
				", pointsTrue=" + pointsTrue +
				", created=" + created +
				", age=" + age +
				", updatedVisual=" + updatedMeta +
				", sub=" + sub +
				", vip=" + vip +
				", subCumulative=" + subCumulative +
				", subStreak=" + subStreak +
				", suitableForRaffle=" + suitableForRaffle +
				", approved=" + approved +
				", twitchUserId='" + twitchUserId + '\'' +
				", followedAt='" + followedAt + '\'' +
				", follower=" + follower +
				'}';
	}
}
