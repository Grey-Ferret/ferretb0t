package dev.greyferret.ferretbot.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;

@Entity
@Table(name = "game_vote_voting")
public class GameVoteVoting implements Comparable<GameVoteVoting>, Serializable {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER)
	private GameVoteGame game;
	@Column(name = "vote_channel_id", nullable = false)
	@ColumnDefault("-1")
	private Long voteChannelId;
	@Column(name = "in_vote")
	private boolean inVote = false;
	@Column(name = "gameVote")
	private String gameVote;
	@Column(name = "voters")
	private HashMap<Long, Integer> voters;

	public GameVoteVoting () {

	}

	public GameVoteVoting(Long voteChannelId, GameVoteGame gameVoteGame) {
		this.voteChannelId = voteChannelId;
		this.voters = new HashMap<>();
		this.inVote = false;
		this.game = gameVoteGame;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVoteChannelId() {
		return voteChannelId;
	}

	public void setVoteChannelId(Long voteChannelId) {
		this.voteChannelId = voteChannelId;
	}

	public boolean isInVote() {
		return inVote;
	}

	public void setInVote(boolean inVote) {
		this.inVote = inVote;
	}

	public String getGameVote() {
		return gameVote;
	}

	public void setGameVote(String gameVote) {
		this.gameVote = gameVote;
	}

	public HashMap<Long, Integer> getVoters() {
		return voters;
	}

	public void setVoters(HashMap<Long, Integer> voters) {
		this.voters = voters;
	}

	public GameVoteGame getGame() {
		return game;
	}

	public void setGame(GameVoteGame game) {
		this.game = game;
	}

	public Integer calcVotesWithBonus() {
		Integer res = 0;
		for (Long voterId : this.voters.keySet()) {
			res = res + this.voters.get(voterId);
		}
		return res;
	}

	@Override
	public int compareTo(@NotNull GameVoteVoting o) {
		return o.calcVotesWithBonus() - this.calcVotesWithBonus();
	}
}
