package dev.greyferret.ferretbot.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "interactive_command")
public class InteractiveCommand {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "response")
	private String response;
	@Column(name = "disabled")
	@ColumnDefault("false")
	private boolean disabled;
	@Column(name = "code")
	private String code;
	@Column(name = "price")
	private Long price;

	public InteractiveCommand() {
		this.disabled = false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InteractiveCommand that = (InteractiveCommand) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
