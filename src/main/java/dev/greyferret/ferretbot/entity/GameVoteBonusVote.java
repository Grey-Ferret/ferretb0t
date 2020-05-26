package dev.greyferret.ferretbot.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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

	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "votes")
	private Integer votes;

	@Column(name = "disable_base_vote")
	@ColumnDefault(value = "false")
	private boolean disableBaseVote;
}
