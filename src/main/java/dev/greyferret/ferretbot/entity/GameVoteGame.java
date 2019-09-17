package dev.greyferret.ferretbot.entity;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;

@Entity
@Table(name = "game_vote_game")
public class GameVoteGame implements Comparable<GameVoteGame> {
	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;
	@Column(name = "name")
	private String name;
	@Column(name = "game")
	private String game;
	@Column(name = "in_vote")
	private boolean inVote = false;
	@Column(name = "gameVote")
	private String gameVote;
	@Column(name = "emote_id")
	private Long emoteId;
	@Column(name = "voters")
	private HashSet<Long> voters;

	private GameVoteGame() {
	}

	public GameVoteGame(String id, Member member, String game, Long emoteId) {
		String nickname = member.getNickname();
		if (StringUtils.isBlank(nickname)) {
			nickname = member.getUser().getName();
		}
		this.id = id;
		this.game = game;
		this.name = nickname;
		this.emoteId = emoteId;
		this.voters = new HashSet<>();
		this.inVote = false;
		this.gameVote = game;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getEmoteId() {
		return emoteId;
	}

	public void setEmoteId(Long emoteId) {
		this.emoteId = emoteId;
	}

	public HashSet<Long> getVoters() {
		return voters;
	}

	public void setVoters(HashSet<Long> voters) {
		this.voters = voters;
	}

	@Override
	public String toString() {
		return "GameVoteGame{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", game='" + game + '\'' +
				", inVote=" + inVote +
				", gameVote='" + gameVote + '\'' +
				", emoteId=" + emoteId +
				", voters=" + voters +
				'}';
	}

	@Override
	public int compareTo(@NotNull GameVoteGame o) {
		return o.getVoters().size() - this.getVoters().size();
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
