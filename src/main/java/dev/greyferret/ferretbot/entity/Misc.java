package dev.greyferret.ferretbot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Misc info for certain temporary functions
 * <p>
 * Created by GreyFerret on 29.05.2019
 */
@Entity
@Table(name = "misc")
public class Misc implements Serializable {
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "text")
	private String text;
	@Column(name = "number")
	private int number;

	public Misc() {
	}

	public Misc(String id) {
		this.setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
