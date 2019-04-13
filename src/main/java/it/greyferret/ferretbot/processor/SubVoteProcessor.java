package it.greyferret.ferretbot.processor;

import it.greyferret.ferretbot.config.DiscordConfig;
import it.greyferret.ferretbot.entity.SubVoteEntity;
import it.greyferret.ferretbot.entity.SubVoteGame;
import it.greyferret.ferretbot.exception.NotEnoughEmotesDiscordException;
import it.greyferret.ferretbot.util.FerretBotUtils;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
@EnableConfigurationProperties({DiscordConfig.class})
public class SubVoteProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(SubVoteProcessor.class);

	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private DiscordProcessor discordProcessor;

	HashMap<String, SubVoteGame> games;

	@PostConstruct
	private void postConstruct() {
		games = new HashMap<>();
	}

	@Override
	public void run() {
	}

	public void processSubVoteMessage(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if (event.getMember().getUser().getId().equals(discordConfig.getSubVoteAdminId())) {
			if (message.equalsIgnoreCase("!reset")) {
				games = new HashMap<>();
				discordProcessor.subsChannel.sendMessage("Список игр успешно сброшен!").queue();
			} else if (message.equalsIgnoreCase("!publish")) {
				postSubVote(discordProcessor.subVoteChannel, true);
			}
		}
		if (message.toLowerCase().startsWith("!игры")) {
			postSubVote(discordProcessor.subsChannel, false);
		}
		if (message.toLowerCase().startsWith("!игра")) {
			if (message.indexOf(" ") > -1) {
				String game = message.substring(message.indexOf(" ") + 1);
				boolean foundOption = false;
				for (String subId : games.keySet()) {
					String _game = games.get(subId).getGame();
					if (StringUtils.deleteWhitespace(_game).equalsIgnoreCase(StringUtils.deleteWhitespace(game))) {
						if (event.getMember().getUser().getId().equals(subId)) {
							discordProcessor.subsChannel.sendMessage("Такая игра уже предложена вами!").queue();
						} else {
							discordProcessor.subsChannel.sendMessage("Такая игра уже предложена...").queue();
						}
						foundOption = true;
						break;
					}
				}
				if (!foundOption) {
					if (games.containsKey(event.getMember().getUser().getId())) {
						games.replace(event.getMember().getUser().getId(), new SubVoteGame(event.getMember().getNickname(), game));
						discordProcessor.subsChannel.sendMessage("Игра была успешно добавлена, заменив старый вариант.").queue();
					} else {
						games.put(event.getMember().getUser().getId(), new SubVoteGame(event.getMember().getNickname(), game));
						discordProcessor.subsChannel.sendMessage("Игра была успешно добавлена!").queue();
					}
				}
			}
		}
	}

	private void postSubVote(TextChannel channel, boolean withEmotes) {
		try {
			SubVoteEntity subVoteEntity = FerretBotUtils.formSubVoteEntity(games, discordProcessor.getEmotes(), withEmotes);
			if (StringUtils.isBlank(subVoteEntity.getMessage())) {
				channel.sendMessage("Нет предложенных игр...").queue();
			} else {
				Message complete = channel.sendMessage(subVoteEntity.getMessage()).complete(true);
				String voteId = complete.getId();
				if (withEmotes) {
					for (Emote emote : subVoteEntity.getEmotes()) {
						channel.addReactionById(voteId, emote).queue();
					}
				}
			}
		} catch (RateLimitedException e) {
			logger.error(e);
		} catch (NotEnoughEmotesDiscordException e) {
			logger.error(e);
		}
	}
}
