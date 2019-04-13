package dev.greyferret.ferretbot.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "command")
public class Command {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "response")
	private String response;
	@Column(name = "game")
	private String game;
	@Column(name = "response_type", nullable = false, columnDefinition = "int default 0")
	private Integer responseType;
	@Column(name = "disabled")
	@ColumnDefault("false")
	private boolean disabled;

	public Command() {
		this.responseType = 0;
		this.disabled = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public Integer getResponseType() {
		return responseType;
	}

	public void setResponseType(Integer responseType) {
		this.responseType = responseType;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Command command = (Command) o;
		return Objects.equals(id, command.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
