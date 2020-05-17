package dev.greyferret.ferretbot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "chatGift")
@Data
public class ChatGiftFluffDto {
	@Column(name = "id", updatable = false, nullable = false)
	@Id
	@GeneratedValue
	private Long id;
	@Column(name = "gift")
	private String gift;
}
