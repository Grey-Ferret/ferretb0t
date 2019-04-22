package dev.greyferret.ferretbot.entity;

import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "sub_vote_game")
public class SubVoteGame {
	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;
	@Column(name = "name")
	private String name;
	@Column(name = "game")
	private String game;

	private SubVoteGame() {
	}

	public SubVoteGame(String id, Member member, String game) {
		String nickname = member.getNickname();
		if (StringUtils.isBlank(nickname)) {
			nickname = member.getUser().getName();
		}
		this.id = id;
		this.game = game;
		this.name = nickname;
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
}
