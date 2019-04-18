package dev.greyferret.ferretbot.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "adventure")
public class Adventure implements Serializable {
	@Column(name = "id", updatable = false, nullable = false)
	@GeneratedValue
	@Id
	private Long id;
	@Column(name = "is_start")
	@ColumnDefault("false")
	private boolean isStart;
	@Column(name = "is_final")
	@ColumnDefault("false")
	private boolean isFinal;
	@Column(name = "text")
	@Type(type = "org.hibernate.type.TextType")
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean start) {
		isStart = start;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean aFinal) {
		isFinal = aFinal;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
