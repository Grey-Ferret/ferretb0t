package dev.greyferret.ferretbot.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "adventure_response")
public class AdventureResponse {
	@Column(name = "id", updatable = false, nullable = false)
	@Id
	@GeneratedValue
	private Long id;
	@Column(name = "text")
	@Type(type = "org.hibernate.type.TextType")
	private String text;
	@Column(name = "response")
	@Type(type = "org.hibernate.type.TextType")
	private String response;
	@Column(name = "key")
	private String key;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = true)
	@JoinColumn(name = "adventure_id", referencedColumnName = "id")
	private Adventure adventure;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Adventure getAdventure() {
		return adventure;
	}

	public void setAdventure(Adventure adventure) {
		this.adventure = adventure;
	}
}
