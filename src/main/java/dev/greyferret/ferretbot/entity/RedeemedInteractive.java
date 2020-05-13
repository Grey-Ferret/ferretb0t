package dev.greyferret.ferretbot.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "interactive_command_redeemed")
public class RedeemedInteractive {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
	@JoinColumn(name = "interactive", referencedColumnName = "id")
	private Interactive interactive;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, optional = false)
	@JoinColumn(name = "viewer", referencedColumnName = "login")
	private Viewer viewer;
	@Column(name = "shown")
	@ColumnDefault("false")
	private boolean shown;
	@Column(name = "date")
	private LocalDateTime date;

	public RedeemedInteractive() {
		this.shown = false;
		this.date = LocalDateTime.now();
	}

	public RedeemedInteractive(Interactive interactive,
	                           Viewer viewer) {
		super();
		this.interactive = interactive;
		this.viewer = viewer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RedeemedInteractive that = (RedeemedInteractive) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
