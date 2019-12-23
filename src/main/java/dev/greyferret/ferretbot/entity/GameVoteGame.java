package dev.greyferret.ferretbot.entity;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Entity
@Table(name = "game_vote", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "vote_channel_id"})})
public class GameVoteGame implements Comparable<GameVoteGame> {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "vote_channel_id", nullable = false)
	@ColumnDefault("-1")
	private Long voteChannelId;
	@Column(name = "user_nickname")
	private String userNickname;
	@Column(name = "game")
	private String game;
	@Column(name = "in_vote")
	private boolean inVote = false;
	@Column(name = "gameVote")
	private String gameVote;
	@Column(name = "emote_id")
	private Long emoteId;
	@Column(name = "voters")
	private HashMap<Long, Integer> voters;

	private GameVoteGame() {
	}

	public GameVoteGame(String userId, Member member, String game, Long emoteId, Long voteChannelId) {
		String nickname = member.getNickname();
		if (StringUtils.isBlank(nickname)) {
			nickname = member.getUser().getName();
		}
		this.userId = userId;
		this.game = game;
		this.userNickname = nickname;
		this.emoteId = emoteId;
		this.voters = new HashMap<>();
		this.inVote = false;
		this.gameVote = game;
		this.voteChannelId = voteChannelId;
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEmoteId() {
		return emoteId;
	}

	public void setEmoteId(Long emoteId) {
		this.emoteId = emoteId;
	}

	public HashMap<Long, Integer> getVoters() {
		return voters;
	}

	public void setVoters(HashMap<Long, Integer> voters) {
		this.voters = voters;
	}

	public Long getVoteChannelId() {
		return voteChannelId;
	}

	public void setVoteChannelId(Long voteChannelId) {
		this.voteChannelId = voteChannelId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "GameVoteGame{" +
				"id=" + id +
				", userId='" + userId + '\'' +
				", voteChannelId=" + voteChannelId +
				", userNickname='" + userNickname + '\'' +
				", game='" + game + '\'' +
				", inVote=" + inVote +
				", gameVote='" + gameVote + '\'' +
				", emoteId=" + emoteId +
				", voters=" + voters +
				'}';
	}

	public Integer calcVotesWithBonus() {
		Integer res = 0;
		for (Long voterId : this.voters.keySet()) {
			res = res + this.voters.get(voterId);
		}
		return res;
	}

	@Override
	public int compareTo(@NotNull GameVoteGame o) {
		return o.calcVotesWithBonus() - this.calcVotesWithBonus();
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
}
