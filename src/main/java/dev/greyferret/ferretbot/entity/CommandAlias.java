package dev.greyferret.ferretbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "command_alias")
public class CommandAlias {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "command", referencedColumnName = "id")
	private Command command;

	public CommandAlias() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}
}
