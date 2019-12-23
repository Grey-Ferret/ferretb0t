package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.entity.GameVoteGame;
import dev.greyferret.ferretbot.entity.GamevoteChannelCombination;
import dev.greyferret.ferretbot.service.GameVoteGameService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
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

	@Value("${discord.delay-to-countdown}")
	private int minsBeforeCountDown;

	public static int gamesPerPost = 10;
	private HashMap<Long, ArrayList<Long>> channelMessageMap = new HashMap<>();
	private HashMap<Long, Long> channelMessageResultIdMap = new HashMap<>();
	private final Object MESSAGES_LOCKER = new Object();
	private HashMap<Long, Long> removeVoteUsers = new HashMap<>();

	private final Object REMOVE_VOTE_USERS_LOCK = new Object();

	@Override
	public void run() {
	}

	public void processGameVoteMessage(MessageReceivedEvent event) {
		GamevoteChannelCombination channelCombination = discordProcessor.getGamevoteCombinationByAddChannel(event.getChannel());
		if (channelCombination == null) {
			return;
		}
		String message = event.getMessage().getContentDisplay();
		if (discordConfig.getSubVoteAdminId().contains(event.getMember().getIdLong())) {
			if (message.toLowerCase().startsWith("!remove")) {
				List<User> mentionedUsers = event.getMessage().getMentionedUsers();
				if (mentionedUsers == null || mentionedUsers.size() == 0) {
					channelCombination.getAddChannel().sendMessage("В сообщении не было найдено упоминаний! Если вы хотели удалить свой вариант как админ, используйте упоминание.").queue();
					return;
				}
				ArrayList<String> mentionedUsersIds = new ArrayList<>();
				for (User mentionedUser : mentionedUsers) {
					mentionedUsersIds.add(mentionedUser.getId());
				}
				String messageToReply = gameVoteGameService.removeGame(channelCombination.getAddChannelId(), mentionedUsersIds);
				channelCombination.getAddChannel().sendMessage(messageToReply).queue();
			} else if (message.equalsIgnoreCase("!reset")) {
				boolean reseted = gameVoteGameService.reset(channelCombination.getAddChannelId());
				resetMessageIds(channelCombination.getAddChannelId());
				if (reseted) {
					event.getMessage().addReaction("\uD83D\uDC4D").queue();
				} else {
					channelCombination.getAddChannel().sendMessage("Что-то пошло не так...").queue();
				}
			} else if (message.equalsIgnoreCase("!publish")) {
				boolean cleared = gameVoteGameService.clearVoters(channelCombination.getAddChannelId());
				resetMessageIds(channelCombination.getAddChannelId());
				gameVoteGameService.saveGameForVote(channelCombination.getAddChannelId());
				postGameVote(channelCombination.getVoteChannel(), channelCombination.getAddChannelId(), true);
				resetUsersRemoveChance();

				CalcVoteResultsProcessor calcVoteResultsProcessor = new CalcVoteResultsProcessor(channelCombination, gameVoteGameService, minsBeforeCountDown);
				Thread calcVoteResultsThread = new Thread(calcVoteResultsProcessor);
				calcVoteResultsThread.start();
			}
		} else {
			// If not admin
			if (message.toLowerCase().startsWith("!remove")) {
				String result = gameVoteGameService.removeGame(channelCombination.getAddChannelId(), event.getMember().getUser().getId());
				channelCombination.getAddChannel().sendMessage(result).queue();
			}
		}
		if (message.toLowerCase().startsWith("!игры")) {
			postGameVote(channelCombination.getAddChannel(), channelCombination.getAddChannelId(), false);
		}
		if (message.toLowerCase().startsWith("!игра")) {
			if (message.indexOf(" ") > -1) {
				String game = message.substring(message.indexOf(" ") + 1);
				game = game.replaceAll("\n|\r\n", " ");
				if (StringUtils.isBlank(game)) {
					channelCombination.getAddChannel().sendMessage("Ошибка получения игры...").queue();
				} else {
					GameVoteGame gameVoteGame = gameVoteGameService.getChannelIdAndByGame(channelCombination.getAddChannelId(), game);
					if (gameVoteGame != null) {
						String _game = gameVoteGame.getGame();
						if (StringUtils.deleteWhitespace(_game).equalsIgnoreCase(StringUtils.deleteWhitespace(game))) {
							if (event.getMember().getUser().getId().equals(gameVoteGame.getUserId())) {
								channelCombination.getAddChannel().sendMessage("Такая игра уже предложена вами!").queue();
							} else {
								channelCombination.getAddChannel().sendMessage("Такая игра уже предложена...").queue();
							}
						}
					} else {
						Long newEmoteId = gameVoteGameService.findNewEmoteId(channelCombination.getAddChannelId(), discordProcessor.getPublicEmotes());
						if (newEmoteId == null) {
							channelCombination.getAddChannel().sendMessage("Игр больше чем смайликов! Обратитесь к админам.");
							return;
						}
						GameVoteGame gameVoteGameToAdd = new GameVoteGame(
								event.getMember().getUser().getId(),
								event.getMember(),
								game,
								newEmoteId,
								channelCombination.getAddChannelId());
						boolean found = gameVoteGameService.addOrUpdate(gameVoteGameToAdd);
						event.getMessage().addReaction("\uD83D\uDC4D").queue();
					}
				}
			}
		}
	}

	private void resetMessageIds(Long channelAddId) {
		channelMessageMap.remove(channelAddId);
		channelMessageResultIdMap.remove(channelAddId);
	}

	private void postGameVote(TextChannel channel, Long textChannelId, boolean withEmotes) {
		ArrayList<ArrayList<GameVoteGame>> posts = new ArrayList<>();
		List<GameVoteGame> subGames = gameVoteGameService.getAllWithTextChannelId(textChannelId);
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
			temp.addAll(subGames.subList(i * gamesPerPost, i * gamesPerPost + to));
			posts.add(temp);
		}
		for (ArrayList<GameVoteGame> games : posts) {
			String text = FerretBotUtils.formGameVoteEntity(games, channel.getJDA(), withEmotes);
			if (StringUtils.isBlank(text)) {
				continue;
			}
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
					ArrayList<Long> messages = channelMessageMap.get(textChannelId);
					if (messages == null) {
						messages = new ArrayList<>();
					}
					messages.add(message.getIdLong());
					channelMessageMap.put(textChannelId, messages);
				}
			}
		}
		if (withEmotes && posts.size() > 0) {
			createOrUpdatePost(discordProcessor.getGamevoteCombinationByAddChannel(textChannelId));
		}
	}

	public void createOrUpdatePost(GamevoteChannelCombination channelCombination) {
		HashMap<Long, Long> messageWithResultMap = getMessageWithResult();
		Long messageWithResult = messageWithResultMap.get(channelCombination.getAddChannelId());
		if (messageWithResult == null) {
			messageWithResult = -1L;
		}
		try {
			JDA jda = discordProcessor.getJDA();
			if (messageWithResult < 0) {
				Message messageId = channelCombination.getVoteChannel().sendMessage(FerretBotUtils.formResultsGameVoteEntity(gameVoteGameService.getAllWithTextChannelId(channelCombination.getAddChannelId()), jda, false, true)).complete(true);
				messageWithResultMap.put(channelCombination.getAddChannelId(), messageId.getIdLong());
				setMessageWithResult(messageWithResultMap);
			} else {
				synchronized (MESSAGES_LOCKER) {
					ArrayList<Long> messages = channelMessageMap.get(channelCombination.getAddChannelId());
					MessageHistory history = channelCombination.getVoteChannel().getHistoryAfter(messages.get(messages.size() - 1), 20).complete(true);
					for (Message message : history.getRetrievedHistory()) {
						if (message.getIdLong() == messageWithResult) {
							message.editMessageFormat(FerretBotUtils.formResultsGameVoteEntity(gameVoteGameService.getAllWithTextChannelId(channelCombination.getAddChannelId()), jda, false, true)).queue();
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

	public ArrayList<Long> getVoteMessageIds(Long addChannelId) {
		synchronized (MESSAGES_LOCKER) {
			return this.channelMessageMap.get(addChannelId);
		}
	}

	public HashMap<Long, Long> getMessageWithResult() {
		return channelMessageResultIdMap;
	}

	public void setMessageWithResult(HashMap<Long, Long> messageWithResult) {
		this.channelMessageResultIdMap = channelMessageResultIdMap;
	}

	public void resetUsersRemoveChance() {
		synchronized (REMOVE_VOTE_USERS_LOCK) {
			removeVoteUsers = new HashMap<>();
		}
	}

	public HashMap<Long, Long> getUsersRemoveChance() {
		synchronized (REMOVE_VOTE_USERS_LOCK) {
			return removeVoteUsers;
		}
	}

	public boolean addUserRemoveChance(Long userId, long emoteId) {
		synchronized (REMOVE_VOTE_USERS_LOCK) {
			if (removeVoteUsers.containsKey(userId)) {
				return false;
			} else {
				removeVoteUsers.put(userId, emoteId);
				return true;
			}
		}
	}
}
