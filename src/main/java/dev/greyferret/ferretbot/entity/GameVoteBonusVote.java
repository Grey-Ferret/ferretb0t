package dev.greyferret.ferretbot.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "game_vote_bonus_vote")
@Getter
@Setter
public class GameVoteBonusVote {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "text_channel_id", nullable = false)
	private Long textChannelId;

	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "votes")
	private Integer votes;
}
