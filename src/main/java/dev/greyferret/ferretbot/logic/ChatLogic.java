package dev.greyferret.ferretbot.logic;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.processor.*;
import dev.greyferret.ferretbot.service.CommandService;
import dev.greyferret.ferretbot.service.ViewerLootsMapService;
import dev.greyferret.ferretbot.service.ViewerService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import dev.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
@EnableConfigurationProperties({ChatConfig.class, BotConfig.class})
@Log4j2
public class ChatLogic {
	@Value("${main.zone-id}")
	private String zoneId;

	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ViewerLootsMapService viewerLootsMapService;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private BotConfig botConfig;
	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private AdventureProcessor adventureProcessor;
	@Autowired
	private PointsProcessor pointsProcessor;

	private StreamElementsAPIProcessor streamElementsAPIProcessor;
	private MTGACardFinderProcessor mtgaCardFinderProcessor;
	private HashMap<String, ZonedDateTime> recentSubsTimes = new HashMap<>();

	@PostConstruct
	private void postConstruct() {
		streamElementsAPIProcessor = context.getBean(StreamElementsAPIProcessor.class);
		mtgaCardFinderProcessor = context.getBean(MTGACardFinderProcessor.class);
	}

	/***
	 * Logic for chat commands for everyone
	 *
	 * @param event
	 */
	public void proceedCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		String[] split = message.split(" ");

		if (message.toLowerCase().startsWith("!")) {
			if (message.toLowerCase().startsWith("!поход")) {
				adventureProcessor.checkAdventure(event);
			}
			if (message.toLowerCase().startsWith("!проверка")) {
				adventureProcessor.checkAdventurer(event);
			}
			boolean foundCustomLogicCommand = false;
			if (message.toLowerCase().startsWith("!иду") && botConfig.isStreamElementsIntegrationOn()) {
				adventureProcessor.joinAdventure(event);
			} else if (message.length() == 2 || message.length() == 1) {
				String keyword = StringUtils.replace(message.toLowerCase(), "!", "");
				if (keyword.length() == 1) {
					adventureProcessor.setAdventurerResponse(event, keyword);
				}
			}
			if (botConfig.isQueueOn()) {
				QueueProcessor queueProcessor = context.getBean(QueueProcessor.class);
				queueProcessor.proceed(event);
			}
			if (botConfig.isMtgaCardsOn() && (message.toLowerCase().startsWith("!card") || message.toLowerCase().startsWith("!карта"))) {
				if (message.contains(" ") && message.length() > message.indexOf(" ") + 1) {
					String keyword = message.substring(message.indexOf(" ") + 1);
					if (keyword != null && keyword.length() > 0) {
						mtgaCardFinderProcessor.findCard(keyword, event);
					}
				}
			}
			if (message.toLowerCase().startsWith("!обнять")) {
				foundCustomLogicCommand = true;
				ViewersProcessor viewersProcessor = context.getBean(ViewersProcessor.class);
				viewersProcessor.rollHug(event.getLoginVisual());
			}
			if (message.toLowerCase().startsWith("!подарить")) {
				foundCustomLogicCommand = true;
				ViewersProcessor viewersProcessor = context.getBean(ViewersProcessor.class);
				viewersProcessor.rollGift(event.getLoginVisual());
			}
			if (message.toLowerCase().startsWith("!стукнуть")) {
				foundCustomLogicCommand = true;
				ViewersProcessor viewersProcessor = context.getBean(ViewersProcessor.class);
				viewersProcessor.rollSmack(event.getLoginVisual());
			}
			if (!foundCustomLogicCommand && botConfig.isCustomCommandsOn()) {
				CommandService commandService = context.getBean(CommandService.class);
				commandService.proceedTextCommand(split[0].toLowerCase(), event);
			}
		}
	}

	/***
	 * Logic for chat commands for admins
	 *
	 * @param event
	 */
	public void proceedAdminCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		String[] split = StringUtils.split(message, " ");

		if (split[0].toLowerCase().startsWith("!")) {
			if (botConfig.isCustomCommandsOn()) {
				if (split[0].toLowerCase().startsWith("!command")) {
					if (split.length >= 3) {
						if (split.length > 3) {
							if (split[1].toLowerCase().startsWith("add")) {
								message = StringUtils.replace(message, "!command add " + split[2] + " ", "");
								proceedAddOrEditCommand(event, message, split[2]);
							}
							if (split[1].toLowerCase().startsWith("edit")) {
								message = StringUtils.replace(message, "!command edit " + split[2] + " ", "");
								proceedAddOrEditCommand(event, message, split[2]);
							}
							if (split[1].toLowerCase().startsWith("alias")) {
								CommandService commandService = context.getBean(CommandService.class);
								String res = commandService.addCommandAlias(split[2], split[3]);
								event.sendMessageWithMention(res);
							}
						}
						if (split[1].toLowerCase().startsWith("enable") || split[1].toLowerCase().startsWith("disable")) {
							CommandService commandService = context.getBean(CommandService.class);
							String res;
							if (split[1].toLowerCase().startsWith("enable")) {
								res = commandService.enableCommand(split[2]);
							} else {
								res = commandService.disableCommand(split[2]);
							}
							event.sendMessageWithMention(res);
						}
					}
				}
			}
			if (botConfig.isQueueOn()) {
				if (message.toLowerCase().startsWith("!queue")) {
					if (message.toLowerCase().startsWith("!queue add ") && split.length >= 3) {
						QueueProcessor queueProcessor = context.getBean(QueueProcessor.class);
						boolean res = queueProcessor.registerQueue(split[2]);
						if (res) {
							event.sendMessageWithMention("Успешно создано!");
						} else {
							event.sendMessageWithMention("Уже есть очередь с таким названием");
						}
					} else if (message.toLowerCase().startsWith("!queue reset ") && split.length >= 3) {
						QueueProcessor queueProcessor = context.getBean(QueueProcessor.class);
						boolean res = queueProcessor.resetQueue(split[2]);
						if (res) {
							event.sendMessageWithMention("Очередь успешно сброшена!");
						} else {
							event.sendMessageWithMention("Очередь с таким названием не найдена");
						}
					} else if (message.toLowerCase().startsWith("!queue remove ") && split.length >= 3) {
						QueueProcessor queueProcessor = context.getBean(QueueProcessor.class);
						boolean res = queueProcessor.deleteQueue(split[2]);
						if (res) {
							event.sendMessageWithMention("Очередь успешно удалена!");
						} else {
							event.sendMessageWithMention("Очередь с таким названием не найдена");
						}
					}
				} else if (message.toLowerCase().startsWith("!")) {
					String[] split2 = message.split(" ");
					if (split2.length >= 3 && split2[1].equalsIgnoreCase("select")) {
						QueueProcessor queueProcessor = context.getBean(QueueProcessor.class);
						HashSet<Viewer> selected = new HashSet<>();
						try {
							Integer numberOfPeople = NumberUtils.toInt(split2[2], 0);
							selected = queueProcessor.roll(split2[0], numberOfPeople);
						} catch (NumberFormatException e) {
							log.error(e.toString());
						}
						if (selected == null || selected.size() == 0) {

						} else {
							event.sendMessageWithMention("Были выбраны: " + FerretBotUtils.buildMergedViewersNicknamesWithMention(selected));
						}
					}
				}
			}
		}
	}

	private void proceedAddOrEditCommand(ChannelMessageEventWrapper event, String message, String code) {
		CommandService commandService = context.getBean(CommandService.class);
		String res = commandService.addOrEditCommand(code, message);
		event.sendMessageWithMention(res);
	}

	/***
	 * Logic for chat commands for mods
	 *
	 * @param event
	 */
	public void proceedModsCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		if (message.startsWith("!aliasoff")) {
			aliasDelete(event, message);
		} else if (message.startsWith("!alias")) {
			alias(event, message);
		}

		if (message.startsWith("!transfer")) {
			String[] split = StringUtils.split(message, ' ');
			if (split.length == 4 && StringUtils.isNumeric(split[3])) {
				log.info("Points transfer initiated by " + event.getLogin() + ", from " + split[1] + " to " + split[2] + " amount " + split[3]);
				Long sum = Long.parseLong(split[3]);
				boolean updatedPoints = pointsProcessor.updatePoints(split[1], (sum * -1));
				if (!updatedPoints) {
					event.sendMessageWithMention("Недостаточно IQ у первого зрителя!");
				} else {
					viewerService.removePoints(split[1], sum);
					pointsProcessor.updatePoints(split[2], sum);
					viewerService.addPoints(split[2], sum);
					event.sendMessageWithMention("IQ успешно переведены!");
				}
			}
		}

		if (message.startsWith("!repair")) {
			repair(event);
		}

		if (message.startsWith("!approve")) {
			approve(event);
		}
	}

	private void approve(ChannelMessageEventWrapper event) {
		if (event.getMessage().split(" ").length >= 2) {
			String login = event.getMessage().split(" ")[1];
			Viewer viewer = viewerService.getViewerByName(login);
			if (viewer == null) {
				event.sendMessageWithMention("Зритель не был найден. Попробуйте позже..?");
				return;
			}
			viewerService.updateApproved(viewer, true);
			event.sendMessageWithMention("Зритель был отмечен как одобреный. Приятного общения!");
			event.sendMessage("/unban " + login);
		}
	}

	public void proceedSubAlert(UserNoticeEventWrapper wrapper) {
		this.proceedSubAlert(wrapper, false);
	}

	public synchronized void proceedSubAlert(UserNoticeEventWrapper wrapper, boolean isGift) {
		String subPlan = wrapper.getTag("msg-param-sub-plan");
		if (StringUtils.isNotBlank(subPlan)) {
			Long points = null;
			if (subPlan.equalsIgnoreCase("prime")) {
				points = chatConfig.getSubPlan().getPrime();
			} else if (subPlan.equalsIgnoreCase("1000")) {
				points = chatConfig.getSubPlan().getFive();
			} else if (subPlan.equalsIgnoreCase("2000")) {
				points = chatConfig.getSubPlan().getTen();
			} else if (subPlan.equalsIgnoreCase("3000")) {
				points = chatConfig.getSubPlan().getTwentyFive();
			}
			String login = wrapper.getTag("login");
			String loginForThanks;
			if (isGift) {
				loginForThanks = wrapper.getTag("msg-param-recipient-display-name");
			} else {
				loginForThanks = login;
			}
			clearOldRecentSubsTimes();
			if (!isNewSubMessage(login, loginForThanks)) {
				return;
			}
			viewerService.addPoints(login, points);
			if (wrapper.getTag("msg-id").equalsIgnoreCase("resub")) {
				String _subStreak = wrapper.getTag("msg-param-streak-months");
				String _subCumulative = wrapper.getTag("msg-param-cumulative-months");
				Viewer viewer = viewerService.getViewerByName(login);
				Integer subStreak = NumberUtils.toInt(_subStreak, 0);
				if (subStreak != null) {
					viewer.setSubStreak(subStreak);
				}
				Integer subCumulative = NumberUtils.toInt(_subCumulative, 0);
				if (subCumulative != null) {
					viewer.setSubCumulative(subCumulative);
				}
				viewerService.updateViewer(viewer);
			}
			if (!login.equalsIgnoreCase("ananonymousgifter")) {
				pointsProcessor.updatePoints(login, points);
			}
			wrapper.sendMessage("Спасибо за подписку, " + loginForThanks + "!");
		}
	}

	private void clearOldRecentSubsTimes() {
		HashMap<String, ZonedDateTime> newRecentSubsTimes = new HashMap<>();
		recentSubsTimes.keySet().forEach(key -> {
			if (recentSubsTimes.containsKey(key) && recentSubsTimes.get(key).plusDays(1).isAfter(ZonedDateTime.now(ZoneId.of(zoneId)))) {
				newRecentSubsTimes.put(key, recentSubsTimes.get(key));
			}
		});
		recentSubsTimes = newRecentSubsTimes;
	}

	private boolean isNewSubMessage(String login, String loginForThanks) {
		String summedNickName = login.toLowerCase() + loginForThanks.toLowerCase();
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(zoneId));
		if (recentSubsTimes.containsKey(summedNickName)) {
			ZonedDateTime oldTime = recentSubsTimes.get(summedNickName);
			if (currentTime.minusMinutes(1).isBefore(oldTime)) {
				return false;
			}
		}
		recentSubsTimes.put(summedNickName, currentTime);
		return true;
	}

	protected void alias(ChannelMessageEventWrapper event, String message) {
		String[] split = StringUtils.split(message, ' ');
		if (split.length == 3) {
			String answer = viewerLootsMapService.updateAlias(split[1], split[2]);
			log.info("User change ended with following message: " + answer);
			event.sendMessageWithMention(answer);
		} else if (split.length == 2) {
			String answer = viewerLootsMapService.showAliasMessage(split[1]);
			event.sendMessageWithMention(answer);
		}
	}

	protected void aliasDelete(ChannelMessageEventWrapper event, String message) {
		String[] split = StringUtils.split(message, ' ');
		if (split.length == 2) {
			String answer = viewerLootsMapService.deleteViewerLootsMap(split[1]);
			event.sendMessageWithMention(answer);
		}
	}

	protected void repair(ChannelMessageEventWrapper event) {
		Set<String> lootsForRepair = viewerLootsMapService.getRepairList();
		if (lootsForRepair == null || lootsForRepair.size() == 0) {
			event.sendMessage("Nothing to repair! :)");
			return;
		}
		String repairNames = StringUtils.join(lootsForRepair, ", ");
		event.sendMessage("To fix: " + repairNames);
	}

	public boolean antispamByWords(ChannelMessageEventWrapper event) {
		String message = event.getMessage().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		boolean result = false;
		for (String word : tempBanWords) {
			if (message.contains(word.toLowerCase())) {
				result = true;
			}
		}
		return result;
	}

	private ArrayList<String> tempBanWords = new ArrayList<>(Arrays.asList("getViewerspro"));
}