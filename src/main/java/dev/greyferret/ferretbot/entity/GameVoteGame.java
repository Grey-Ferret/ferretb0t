package dev.greyferret.ferretbot.entity;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_vote_game", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class GameVoteGame implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "user_nickname")
	private String userNickname;
	@Column(name = "game")
	private String game;
	@Column(name = "emote_id")
	private Long emoteId;
	@OneToMany(cascade = CascadeType.REMOVE,
			mappedBy = "game",
			orphanRemoval = true)
	private List<GameVoteVoting> votings = new ArrayList<>();

	public GameVoteGame() {
	}

	public GameVoteGame(String userId, Member member, String game, Long emoteId) {
		String nickname = member.getNickname();
		if (StringUtils.isBlank(nickname)) {
			nickname = member.getUser().getName();
		}
		this.userId = userId;
		this.game = game;
		this.userNickname = nickname;
		this.emoteId = emoteId;
	}

	public List<GameVoteVoting> getVotings() {
		return votings;
	}

	public void setVotings(List<GameVoteVoting> votings) {
		this.votings = votings;
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
				", userNickname='" + userNickname + '\'' +
				", game='" + game + '\'' +
				", emoteId=" + emoteId +
				'}';
	}
}
