package it.greyferret.ferretbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "dare")
public class Dare {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Column(name = "text")
	private String text;
	@Column(name = "category")
	private Integer category;

	public Dare() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Dare)) return false;

		Dare dare = (Dare) o;

		if (!text.equals(dare.text)) return false;
		return category.equals(dare.category);
	}

	@Override
	public int hashCode() {
		int result = text.hashCode();
		result = 31 * result + category.hashCode();
		return result;
	}

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

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}
}
