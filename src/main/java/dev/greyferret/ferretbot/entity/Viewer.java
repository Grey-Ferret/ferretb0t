package dev.greyferret.ferretbot.entity;

import dev.greyferret.ferretbot.config.SpringConfig;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
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
	@Column(name = "updated")
	private LocalDateTime updated;
	@Column(name = "age")
	private LocalDateTime age;
	@Column(name = "updated_visual")
	private LocalDateTime updatedVisual;
	@Column(name = "sub", nullable = false)
	@ColumnDefault("false")
	private Boolean sub;
	@Column(name = "vip", nullable = false)
	@ColumnDefault("false")
	private Boolean vip;
	@Column(name = "sub_streak")
	private int subStreak;
	@Column(name = "suitable_for_raffle", nullable = false)
	@ColumnDefault("true")
	private boolean suitableForRaffle;
	@Column(name = "approved", nullable = false)
	@ColumnDefault("false")
	private Boolean approved;

	public static int hoursToUpdateVisual = 168;

	public Viewer() {

	}

	public Viewer(String author) {
		this.login = author.toLowerCase();
		this.loginVisual = author;
		this.points = 0L;
		this.pointsTrue = 0l;
		this.subStreak = 0;
		this.sub = false;
		this.vip = false;
		this.approved = false;
		ZonedDateTime zdt = ZonedDateTime.now(SpringConfig.getZoneId());
		zdt.minusHours(hoursToUpdateVisual);
		setUpdatedVisual(zdt);
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

	public ZonedDateTime getCreated() {
		if (this.created == null) {
			return null;
		}
		return ZonedDateTime.of(this.created, SpringConfig.getZoneId());
	}

	public void setCreated(ZonedDateTime created) {
		this.created = created.toLocalDateTime();
	}

	public ZonedDateTime getUpdated() {
		if (this.updated == null) {
			return null;
		}
		return ZonedDateTime.of(this.updated, SpringConfig.getZoneId());
	}

	public void setUpdated(ZonedDateTime updated) {
		this.updated = updated.toLocalDateTime();
	}

	@PostUpdate
	private void postUpdate() {
		setUpdated(ZonedDateTime.now(SpringConfig.getZoneId()));
	}

	@PostPersist
	private void postPersist() {
		ZonedDateTime zdt = ZonedDateTime.now(SpringConfig.getZoneId());
		setCreated(zdt);
		setUpdated(zdt);
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

	public void setLoginVisual(String loginVisual) {
		this.loginVisual = loginVisual;
		setUpdatedVisual(ZonedDateTime.now(SpringConfig.getZoneId()));
	}

	public ZonedDateTime getUpdatedVisual() {
		if (this.updatedVisual == null) {
			return null;
		}
		return ZonedDateTime.of(this.updatedVisual, SpringConfig.getZoneId());
	}

	public void setUpdatedVisual(ZonedDateTime updatedVisual) {
		this.updatedVisual = updatedVisual.toLocalDateTime();
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

	public ZonedDateTime getAge() {
		if (this.age == null) {
			return null;
		}
		return ZonedDateTime.of(this.age, SpringConfig.getZoneId());
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

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	public void setAge(LocalDateTime age) {
		this.age = age;
	}

	public void setUpdatedVisual(LocalDateTime updatedVisual) {
		this.updatedVisual = updatedVisual;
	}

	@Override
	public String toString() {
		return "Viewer{" +
				"login='" + login + '\'' +
				", loginVisual='" + loginVisual + '\'' +
				", points=" + points +
				", pointsTrue=" + pointsTrue +
				", created=" + created +
				", updated=" + updated +
				", age=" + age +
				", updatedVisual=" + updatedVisual +
				", sub=" + sub +
				", vip=" + vip +
				", subStreak=" + subStreak +
				", suitableForRaffle=" + suitableForRaffle +
				", approved=" + approved +
				'}';
	}
}
