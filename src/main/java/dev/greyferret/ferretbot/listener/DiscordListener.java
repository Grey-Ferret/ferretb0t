package dev.greyferret.ferretbot.listener;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.entity.GamevoteChannelCombination;
import dev.greyferret.ferretbot.processor.DiscordProcessor;
import dev.greyferret.ferretbot.processor.GameVoteProcessor;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Log4j2
public class DiscordListener extends ListenerAdapter {
	@Autowired
	private DiscordProcessor discordProcessor;
	@Autowired
	private BotConfig botConfig;
	@Autowired
	private GameVoteProcessor gameVoteProcessor;
	@Autowired
	private GameVoteGameService gameVoteGameService;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.getMessage().getMember().getUser().getName().equalsIgnoreCase("Dyno")) {
			if (event.isFromType(ChannelType.PRIVATE)) {
				log.info("PRIVATE: " + FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			} else {
				log.info(FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			}
		}

		boolean foundChannelForSubVote = false;
		for (GamevoteChannelCombination combination : discordProcessor.gameVoteChannelCombinations) {
			if (event.getChannel().getIdLong() == combination.getAddChannelId()) {
				foundChannelForSubVote = true;
			}
		}
		if (botConfig.isSubVoteOn() && foundChannelForSubVote) {
			gameVoteProcessor.processGameVoteMessage(event);
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		long userId = event.getUser().getIdLong();
		GamevoteChannelCombination channelCombination = discordProcessor.getGamevoteCombinationByVoteChannel(event.getChannel().getIdLong());
		if (userId == event.getJDA().getSelfUser().getIdLong() || channelCombination == null) {
			return;
		}
		ArrayList<Long> voteMessageIds = gameVoteProcessor.getVoteMessageIds(channelCombination.getAddChannelId());
		if (voteMessageIds.contains(event.getMessageIdLong())) {
			long emoteId = event.getReactionEmote().getIdLong();
			GameVoteGame game = gameVoteGameService.getGameByChannelIdAndEmoteId(channelCombination.getAddChannelId(), emoteId);
			if (game.getVoters().containsKey(userId)) {
				return;
			}
			log.info("Reaction added for game {} (message {}) from {}", game, event.getMessageId(), event.getMember().getUser());
			HashMap<Long, Long> usersRemoveChanceMap = gameVoteProcessor.getUsersRemoveChance();
			if (usersRemoveChanceMap.containsKey(userId)) {
				if (usersRemoveChanceMap.get(userId) == emoteId) {
					return;
				}
			}
			boolean roleToDoubleBoolean = false;
			Long gameVoteDoubleVoteRoleId = channelCombination.getGameVoteDoubleVoteRoleId();
			if(gameVoteDoubleVoteRoleId > 0) {
				Role roleToDouble = event.getJDA().getRoleById(gameVoteDoubleVoteRoleId);
				roleToDoubleBoolean = event.getMember().getRoles().contains(roleToDouble);
			}
			gameVoteGameService.addVoter(channelCombination.getAddChannelId(), roleToDoubleBoolean, emoteId, userId);
			gameVoteProcessor.createOrUpdatePost(channelCombination);
		}
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		long userId = event.getUser().getIdLong();
		GamevoteChannelCombination channelCombination = discordProcessor.getGamevoteCombinationByVoteChannel(event.getChannel().getIdLong());
		if (userId == event.getJDA().getSelfUser().getIdLong() || channelCombination == null) {
			return;
		}
		ArrayList<Long> voteMessageIds = gameVoteProcessor.getVoteMessageIds(channelCombination.getAddChannelId());
		if (voteMessageIds.contains(event.getMessageIdLong())) {
			long emoteId = event.getReactionEmote().getIdLong();
			GameVoteGame game = gameVoteGameService.getGameByChannelIdAndEmoteId(channelCombination.getAddChannelId(), emoteId);
			if (!game.getVoters().containsKey(userId)) {
				return;
			}
			log.info("Reaction removed for game {} (message {}) from {}", game, event.getMessageId(), event.getMember().getUser());
			boolean available = gameVoteProcessor.addUserRemoveChance(userId, emoteId);
			if (available) {
				gameVoteGameService.removeVoter(channelCombination.getAddChannelId(), emoteId, userId);
				gameVoteProcessor.createOrUpdatePost(channelCombination);
			}
		}
	}
}
