package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.entity.SubVoteEntity;
import dev.greyferret.ferretbot.entity.SubVoteGame;
import dev.greyferret.ferretbot.exception.NotEnoughEmotesDiscordException;
import dev.greyferret.ferretbot.service.SubVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
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
import java.util.List;

@Component
@EnableConfigurationProperties({DiscordConfig.class})
public class SubVoteProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(SubVoteProcessor.class);

	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private DiscordProcessor discordProcessor;
	@Autowired
	private SubVoteGameService subVoteGameService;

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
	}

	public void processSubVoteMessage(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if (event.getMember().getUser().getId().equals(discordConfig.getSubVoteAdminId())) {
			if (message.equalsIgnoreCase("!reset")) {
				boolean reseted = subVoteGameService.reset();
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
				if (StringUtils.isBlank(game)) {
					discordProcessor.subsChannel.sendMessage("Ошибка получения игры...").queue();
				} else {
					boolean foundOption = false;
					List<SubVoteGame> subVoteGameList = subVoteGameService.getByGame(game);
					for (SubVoteGame subVoteGame : subVoteGameList) {
						String _game = subVoteGame.getGame();
						if (StringUtils.deleteWhitespace(_game).equalsIgnoreCase(StringUtils.deleteWhitespace(game))) {
							if (event.getMember().getUser().getId().equals(subVoteGame.getId())) {
								discordProcessor.subsChannel.sendMessage("Такая игра уже предложена вами!").queue();
							} else {
								discordProcessor.subsChannel.sendMessage("Такая игра уже предложена...").queue();
							}
							foundOption = true;
							break;
						}
					}
					if (!foundOption) {
						if (subVoteGameService.containsId(event.getMember().getUser().getId())) {
							discordProcessor.subsChannel.sendMessage("Игра была успешно добавлена, заменив старый вариант.").queue();
						} else {
							discordProcessor.subsChannel.sendMessage("Игра была успешно добавлена!").queue();
						}
						subVoteGameService.addOrUpdate(new SubVoteGame(event.getMember().getUser().getId(), event.getMember(), game));
					}
				}
			}
		}
	}

	private void postSubVote(TextChannel channel, boolean withEmotes) {
		try {
			SubVoteEntity subVoteEntity = FerretBotUtils.formSubVoteEntity(subVoteGameService.getAll(), discordProcessor.getPublicEmotes(), withEmotes);
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
