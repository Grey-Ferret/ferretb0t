package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
	@Autowired
	private ApplicationContext context;

	public static int gamesPerPost = 10;
	private ArrayList<Long> messages = new ArrayList<>();
	private Long messageWithResult = -1L;
	private final Object MESSAGES_LOCKER = new Object();

	private Thread voteCheckThread;

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
	}

	public void processGameVoteMessage(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if (discordConfig.getSubVoteAdminId().contains(event.getMember().getUser().getIdLong())) {
			if (message.equalsIgnoreCase("!reset")) {
				boolean reseted = gameVoteGameService.reset();
				resetMessageIds();
				if (reseted) {
					discordProcessor.readVoteChannel.sendMessage("Список игр успешно сброшен!").queue();
				} else {
					discordProcessor.readVoteChannel.sendMessage("Что-то пошло не так...").queue();
				}
			} else if (message.equalsIgnoreCase("!publish")) {
				boolean cleared = gameVoteGameService.clearVoters();
				resetMessageIds();
				postGameVote(discordProcessor.writeVoteChannel, true);
			} else if (message.equalsIgnoreCase("!clear")) {
				boolean cleared = gameVoteGameService.clearVoters();
				createOrUpdatePost();
				if (cleared) {
					discordProcessor.readVoteChannel.sendMessage("Список проголосовавших успешно сброшен!").queue();
				} else {
					discordProcessor.readVoteChannel.sendMessage("Что-то пошло не так...").queue();
				}
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
						Long newEmoteId = gameVoteGameService.findNewEmoteId(discordProcessor.getPublicEmotes());
						if (newEmoteId == null) {
							discordProcessor.readVoteChannel.sendMessage("Игр больше чем смайликов! Обратитесь к админам.");
							return;
						}
						boolean found = gameVoteGameService.addOrUpdate(new GameVoteGame(event.getMember().getUser().getId(), event.getMember(), game, newEmoteId));
						if (found) {
							discordProcessor.readVoteChannel.sendMessage("Игра была успешно добавлена, заменив старый вариант.").queue();
						} else {
							discordProcessor.readVoteChannel.sendMessage("Игра была успешно добавлена!").queue();
						}
					}
				}
			}
		}
	}

	private void resetMessageIds() {
		messages = new ArrayList<>();
		messageWithResult = -1L;
	}

	private void postGameVote(TextChannel channel, boolean withEmotes) {
		ArrayList<ArrayList<GameVoteGame>> posts = new ArrayList<>();
		List<GameVoteGame> subGames = gameVoteGameService.getAll();
		if (subGames == null || subGames.size() == 0) {
			channel.sendMessage("Нет предложенных игр. Будешь первым? :)").queue();
			return;
		}
		log.info("Found " + subGames.size() + " SubGames");
		log.info(subGames.toString());
		int postsAmount = (subGames.size() / gamesPerPost) + 1;
		for (int i = 0; i < postsAmount; i++) {
			ArrayList<GameVoteGame> temp = new ArrayList<>();
			int to = Math.min(gamesPerPost, subGames.size() - gamesPerPost * i);
			temp.addAll(subGames.subList(i, to));
			posts.add(temp);
		}
		for (ArrayList<GameVoteGame> games : posts) {
			String text = FerretBotUtils.formGameVoteEntity(games, channel.getJDA(), withEmotes);
			Message message = null;
			try {
				message = channel.sendMessage(text).complete(true);
			} catch (RateLimitedException e) {
				log.error(e);
			}
			if (withEmotes) {
				for (GameVoteGame game : games) {
					Emote emoteById = channel.getJDA().getEmoteById(game.getEmoteId());
					message.addReaction(emoteById).queue();
				}
				synchronized (MESSAGES_LOCKER) {
					messages.add(message.getIdLong());
				}
			}
		}
		if (withEmotes && posts.size() > 0) {
			createOrUpdatePost();
		}
	}

	public void createOrUpdatePost() {
		Long messageWithResult = getMessageWithResult();
		try {
			JDA jda = discordProcessor.writeVoteChannel.getJDA();
			if (messageWithResult < 0) {
				Message messageId = discordProcessor.writeVoteChannel.sendMessage(FerretBotUtils.formResultsGameVoteEntity(gameVoteGameService.getAll(), jda, false, true)).complete(true);
				setMessageWithResult(messageId.getIdLong());
			} else {
				synchronized (MESSAGES_LOCKER) {
					MessageHistory history = discordProcessor.writeVoteChannel.getHistoryAfter(messages.get(messages.size() - 1), 20).complete(true);
					for (Message message : history.getRetrievedHistory()) {
						if (message.getIdLong() == messageWithResult) {
							message.editMessageFormat(FerretBotUtils.formResultsGameVoteEntity(gameVoteGameService.getAll(), jda, false, true)).queue();
							return;
						}
					}
					log.error("No message with results were found!");
				}
			}
		} catch (RateLimitedException ex) {
			log.error("Error with creating/updating results" + ex);
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

	public ArrayList<Long> getVoteMessageIds() {
		synchronized (MESSAGES_LOCKER) {
			return this.messages;
		}
	}

	public Long getMessageWithResult() {
		return messageWithResult;
	}

	public void setMessageWithResult(Long messageWithResult) {
		this.messageWithResult = messageWithResult;
	}
}
