package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.entity.GameVoteEntity;
import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.exception.NotEnoughEmotesDiscordException;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@EnableConfigurationProperties({BotConfig.class})
@Log4j2
public class GameVoteProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private DiscordProcessor discordProcessor;
	@Autowired
	private GameVoteGameService gameVoteGameService;
	@Autowired
	private BotConfig botConfig;

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
	}

	public void processGameVoteMessage(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if (discordConfig.getSubVoteAdminId().contains(event.getMember().getUser().getId())) {
			if (message.equalsIgnoreCase("!reset")) {
				boolean reseted = gameVoteGameService.reset();
				if (reseted) {
					discordProcessor.readVoteChannel.sendMessage("Список игр успешно сброшен!").queue();
				} else {
					discordProcessor.readVoteChannel.sendMessage("Что-то пошло не так...").queue();
				}
			} else if (message.equalsIgnoreCase("!publish")) {
				postGameVote(discordProcessor.writeVoteChannel, true);
			}
		}
		if (message.toLowerCase().startsWith("!игры")) {
			postGameVote(discordProcessor.readVoteChannel, false);
		}
		if (message.toLowerCase().startsWith("!игра")) {
			if (message.indexOf(" ") > -1) {
				String game = message.substring(message.indexOf(" ") + 1);
				if (StringUtils.isBlank(game)) {
					discordProcessor.readVoteChannel.sendMessage("Ошибка получения игры...").queue();
				} else {
					boolean foundOption = false;
					List<GameVoteGame> gameVoteList = gameVoteGameService.getByGame(game);
					for (GameVoteGame gameVoteGame : gameVoteList) {
						String _game = gameVoteGame.getGame();
						if (StringUtils.deleteWhitespace(_game).equalsIgnoreCase(StringUtils.deleteWhitespace(game))) {
							if (event.getMember().getUser().getId().equals(gameVoteGame.getId())) {
								discordProcessor.readVoteChannel.sendMessage("Такая игра уже предложена вами!").queue();
							} else {
								discordProcessor.readVoteChannel.sendMessage("Такая игра уже предложена...").queue();
							}
							foundOption = true;
							break;
						}
					}
					if (!foundOption) {
						if (gameVoteGameService.containsId(event.getMember().getUser().getId())) {
							discordProcessor.readVoteChannel.sendMessage("Игра была успешно добавлена, заменив старый вариант.").queue();
						} else {
							discordProcessor.readVoteChannel.sendMessage("Игра была успешно добавлена!").queue();
						}
						gameVoteGameService.addOrUpdate(new GameVoteGame(event.getMember().getUser().getId(), event.getMember(), game));
					}
				}
			}
		}
	}

	private void postGameVote(TextChannel channel, boolean withEmotes) {
		try {
			List<GameVoteGame> subGames = gameVoteGameService.getAll();
			log.info("Found " + subGames.size() + " SubGames");
			log.info(subGames.toString());
			List<Emote> publicEmotes = discordProcessor.getPublicEmotes();
			log.info("Found " + publicEmotes.size() + " emotes");
			GameVoteEntity gameVoteEntity = FerretBotUtils.formGameVoteEntity(subGames, publicEmotes, withEmotes);
			log.info("Formed GameVoteEntity.");
			log.info(gameVoteEntity.toString());
			if (StringUtils.isBlank(gameVoteEntity.getMessage())) {
				channel.sendMessage("Нет предложенных игр...").queue();
			} else {
				Message complete = channel.sendMessage(gameVoteEntity.getMessage()).complete(true);
				String voteId = complete.getId();
				if (withEmotes) {
					for (Emote emote : gameVoteEntity.getEmotes()) {
						channel.addReactionById(voteId, emote).queue();
					}
				}
			}
		} catch (RateLimitedException e) {
			log.error(e.toString());
		} catch (NotEnoughEmotesDiscordException e) {
			log.error(e.toString());
			channel.sendMessage("Кол-во доступных эмоций меньше чем количество игр.").queue();
		}
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		if (botConfig.isSubVoteOn()) {
			Thread thread = new Thread(this);
			thread.setName("GameVote Thread");
			thread.start();
			log.info(thread.getName() + " started");
		} else {
			log.info("Game Vote off");
		}
	}
}
