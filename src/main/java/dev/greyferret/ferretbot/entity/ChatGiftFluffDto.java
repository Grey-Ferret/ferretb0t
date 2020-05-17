package dev.greyferret.ferretbot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "chat_gift")
@Data
@NoArgsConstructor
public class ChatGiftFluffDto {
	@Column(name = "id", updatable = false, nullable = false)
	@Id
	@GeneratedValue
	private Long id;
	@Column(name = "gift", unique = true)
	private String gift;
}
