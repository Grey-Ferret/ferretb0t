package dev.greyferret.ferretbot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "raffle")
public class Raffle implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private int id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = true)
	@JoinColumn(name = "winner", referencedColumnName = "login")
	private Viewer winner;
	@Column(name = "prize")
	private String prize;
	@Column(name = "date")
	private Date date;

	public Raffle() {

	}

	public Raffle(Prize prize, Viewer viewer) {
		this.prize = prize.getName();
		this.winner = viewer;
		this.date = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Viewer getWinner() {
		return winner;
	}

	public void setWinner(Viewer winner) {
		this.winner = winner;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(Prize prize) {
		this.prize = prize.getName();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
