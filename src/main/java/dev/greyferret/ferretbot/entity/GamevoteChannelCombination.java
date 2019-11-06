package dev.greyferret.ferretbot.entity;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

@Getter
@Setter
public class GamevoteChannelCombination {
	private TextChannel addChannel;
	private TextChannel voteChannel;
	private Long addChannelId;
	private Long voteChannelId;
	private Long gameVoteDoubleVoteRoleId;

	public GamevoteChannelCombination(TextChannel addChannel, TextChannel voteChannel, Long gameVoteDoubleVoteRoleId) {
		this.addChannel = addChannel;
		this.voteChannel = voteChannel;
		this.gameVoteDoubleVoteRoleId = gameVoteDoubleVoteRoleId == null || gameVoteDoubleVoteRoleId < 0 ? 0 : gameVoteDoubleVoteRoleId;
		this.addChannelId = addChannel.getIdLong();
		this.voteChannelId = voteChannel.getIdLong();
	}

	@Override
	public String toString() {
		return "GamevoteChannelCombination{" +
				"addChannelId=" + addChannelId +
				", voteChannelId=" + voteChannelId +
				", addChannel=" + addChannel +
				", voteChannel=" + voteChannel +
				", gameVoteDoubleVoteRoleId=" + gameVoteDoubleVoteRoleId +
				'}';
	}
}
