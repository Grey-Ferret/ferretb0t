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

	public GamevoteChannelCombination(TextChannel addChannel, TextChannel voteChannel) {
		this.addChannel = addChannel;
		this.voteChannel = voteChannel;
		this.addChannelId = addChannel.getIdLong();
		this.voteChannelId = voteChannel.getIdLong();
	}
}
