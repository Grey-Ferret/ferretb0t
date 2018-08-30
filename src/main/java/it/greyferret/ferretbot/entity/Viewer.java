package it.greyferret.ferretbot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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
	private Date created;
	@Column(name = "updated")
	private Date updated;
	@Column(name = "updated_visual")
	private Date updatedVisual;
	@Column(name = "sub", nullable = false, columnDefinition = "boolean default false")
	private Boolean sub;
	@Column(name = "sub_streak")
	private int subStreak;
	@Column(name = "suitable_for_raffle", columnDefinition = "boolean default true")
	private boolean suitableForRaffle;

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
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1 * hoursToUpdateVisual);
		this.updatedVisual = cal.getTime();
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
		this.updatedVisual = new Date();
	}

	public Date getUpdatedVisual() {
		return updatedVisual;
	}

	public void setUpdatedVisual(Date updatedVisual) {
		this.updatedVisual = updatedVisual;
	}

	public boolean removePoints(Long points) {
		if (this.points < points)
			return false;
		Long temp = this.points;
		temp = temp - points;
		setPoints(temp);
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Viewer viewer = (Viewer) o;
		return Objects.equals(login, viewer.login);
	}

	@Override
	public int hashCode() {

		return Objects.hash(login);
	}
}
