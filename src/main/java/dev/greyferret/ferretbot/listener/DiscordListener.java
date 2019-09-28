package dev.greyferret.ferretbot.listener;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.processor.DiscordProcessor;
import dev.greyferret.ferretbot.processor.GameVoteProcessor;
import dev.greyferret.ferretbot.processor.SubVoteProcessor;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
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
	private SubVoteProcessor subVoteProcessor;
	@Autowired
	private GameVoteProcessor gameVoteProcessor;
	@Autowired
	private GameVoteGameService gameVoteGameService;
	@Autowired
	private DiscordConfig discordConfig;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.getMessage().getMember().getUser().getName().equalsIgnoreCase("Dyno")) {
			if (event.isFromType(ChannelType.PRIVATE)) {
				log.info("PRIVATE: " + FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			} else {
				log.info(FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			}
		}

		if (botConfig.isSubVoteOn() && event.getChannel() == discordProcessor.subsChannel) {
			subVoteProcessor.processSubVoteMessage(event);
		}
		if (botConfig.isSubVoteOn() && event.getChannel() == discordProcessor.readVoteChannel) {
			gameVoteProcessor.processGameVoteMessage(event);
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		long userId = event.getUser().getIdLong();
		if (event.getUser().getIdLong() == discordConfig.getSelfId()) {
			return;
		}
		ArrayList<Long> voteMessageIds = gameVoteProcessor.getVoteMessageIds();
		if (voteMessageIds.contains(event.getMessageIdLong())) {
			log.info(event);
			long emoteId = event.getReactionEmote().getIdLong();
			GameVoteGame game = gameVoteGameService.getGameByEmoteId(emoteId);
			if (game.getVoters().contains(userId)) {
				return;
			}
			HashMap<Long, Long> usersRemoveChanceMap = gameVoteProcessor.getUsersRemoveChance();
			if (usersRemoveChanceMap.keySet().contains(userId)) {
				if (usersRemoveChanceMap.get(userId) == emoteId) {
					return;
				}
			}
			gameVoteGameService.addVoter(emoteId, userId);
			gameVoteProcessor.createOrUpdatePost();
		}
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		long userId = event.getUser().getIdLong();
		if (event.getUser().getIdLong() == discordConfig.getSelfId()) {
			return;
		}
		ArrayList<Long> voteMessageIds = gameVoteProcessor.getVoteMessageIds();
		if (voteMessageIds.contains(event.getMessageIdLong())) {
			log.info(event);
			long emoteId = event.getReactionEmote().getIdLong();
			GameVoteGame game = gameVoteGameService.getGameByEmoteId(emoteId);
			if (!game.getVoters().contains(userId)) {
				return;
			}
			boolean available = gameVoteProcessor.addUserRemoveChance(userId, emoteId);
			if (available) {
				gameVoteGameService.removeVoter(emoteId, userId);
				gameVoteProcessor.createOrUpdatePost();
			}
		}
	}
}
