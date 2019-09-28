package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.entity.SubVoteEntity;
import dev.greyferret.ferretbot.entity.SubVoteGame;
import dev.greyferret.ferretbot.service.SubVoteGameService;
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
public class SubVoteProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private DiscordProcessor discordProcessor;
	@Autowired
	private SubVoteGameService subVoteGameService;
	@Autowired
	private BotConfig botConfig;

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
	}

	public void processSubVoteMessage(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if (discordConfig.getSubVoteAdminId().contains(event.getMember().getUser().getIdLong())) {
			if (message.equalsIgnoreCase("!reset")) {
				boolean reseted = subVoteGameService.reset();
				if (reseted) {
					event.getMessage().addReaction("\uD83D\uDC4D").queue();
				} else {
					discordProcessor.subsChannel.sendMessage("Что-то пошло не так...").queue();
				}
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
				game = game.replaceAll("\n|\r\n", " ");
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
						subVoteGameService.addOrUpdate(new SubVoteGame(event.getMember().getUser().getId(), event.getMember(), game));
						event.getMessage().addReaction("\uD83D\uDC4D").queue();
					}
				}
			}
		}
	}

	private void postSubVote(TextChannel channel, boolean withEmotes) {
		try {
			List<SubVoteGame> subGames = subVoteGameService.getAll();
			log.info("Found " + subGames.size() + " SubGames");
			log.info(subGames.toString());
			List<Emote> publicEmotes = discordProcessor.getPublicEmotes();
			log.info("Found " + publicEmotes.size() + " emotes");
			SubVoteEntity subVoteEntity = null;
			subVoteEntity = FerretBotUtils.formSubVoteEntity(subGames, publicEmotes, withEmotes);
			log.info("Formed subVoteEntity.");
			log.info(subVoteEntity.toString());
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
			log.error(e.toString());
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		if (botConfig.isSubVoteOn()) {
			Thread thread = new Thread(this);
			thread.setName("SubVote Thread");
			thread.start();
			log.info(thread.getName() + " started");
		} else {
			log.info("Sub Vote Off");
		}
	}
}
