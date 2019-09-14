package dev.greyferret.ferretbot.entity;

import dev.greyferret.ferretbot.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "raffle")
public class Raffle implements Serializable {
	@Value("${main.zone-id}")
	private String zoneId;

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
	private LocalDateTime date;

	public Raffle() {

	}

	public Raffle(Prize prize, Viewer viewer) {
		this.prize = prize.getName();
		this.winner = viewer;
		setDate(ZonedDateTime.now(ZoneId.of(zoneId)));
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

	public ZonedDateTime getDate(ZoneId zoneId) {
		if (this.date == null) {
			return null;
		}
		return ZonedDateTime.of(this.date, zoneId);
	}

	public void setDate(ZonedDateTime date) {
		this.date = date.toLocalDateTime();
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
