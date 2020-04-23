package dev.greyferret.ferretbot.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@ToString
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

	public void addTruePoints(long i) {
		Long temp = this.pointsTrue + i;
		setPointsTrue(temp);

		temp = this.points + i;
		setPoints(temp);
	}

	public void addPoints(long i) {
		Long temp = this.points + i;
		setPoints(temp);
	}

	public boolean removePoints(Long points) {
		if (this.points < points)
			return false;
		Long temp = this.points;
		temp = temp - points;
		setPoints(temp);
		return true;
	}

	@Deprecated
	public void setLogin(String login) {
		this.login = login;
	}

	public void setUpdatedMeta(ZonedDateTime updatedVisual) {
		this.updatedMeta = updatedVisual.toLocalDateTime();
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

	public Boolean isVip() {
		return getVip();
	}

	public Boolean isSub() {
		return getSub();
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
}
