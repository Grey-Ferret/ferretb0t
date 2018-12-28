package it.greyferret.ferretbot.logic;

import it.greyferret.ferretbot.config.BotConfig;
import it.greyferret.ferretbot.config.ChatConfig;
import it.greyferret.ferretbot.config.LootsConfig;
import it.greyferret.ferretbot.entity.Viewer;
import it.greyferret.ferretbot.processor.DiscordProcessor;
import it.greyferret.ferretbot.processor.QueueProcessor;
import it.greyferret.ferretbot.processor.ViewersProcessor;
import it.greyferret.ferretbot.service.CommandService;
import it.greyferret.ferretbot.service.DareService;
import it.greyferret.ferretbot.service.ViewerLootsMapService;
import it.greyferret.ferretbot.service.ViewerService;
import it.greyferret.ferretbot.util.FerretBotUtils;
import it.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import it.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.requests.RestAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@EnableConfigurationProperties({LootsConfig.class, BotConfig.class})
public class ChatLogic {
	private static final Logger logger = LogManager.getLogger(ChatLogic.class);

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
	private DareService dareService;

	/***
	 * Logic for chat commands for everyone
	 *
	 * @param event
	 */
	public void proceedCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		String[] split = message.split(" ");

		if (message.startsWith("!")) {
			boolean foundCustomLogicCommand = false;
			if (botConfig.getQueueOn()) {
				QueueProcessor queueProcessor = context.getBean(QueueProcessor.class);
				queueProcessor.proceed(event);
			}
			if (message.startsWith("!обнять")) {
				foundCustomLogicCommand = true;
				ViewersProcessor viewersProcessor = context.getBean(ViewersProcessor.class);
				viewersProcessor.rollHug(event.getLoginVisual());
			}
			if (message.startsWith("!стукнуть")) {
				foundCustomLogicCommand = true;
				ViewersProcessor viewersProcessor = context.getBean(ViewersProcessor.class);
				viewersProcessor.rollSmack(event.getLoginVisual());
			}
			if (message.startsWith("!желание ")) {
				String[] split1 = StringUtils.split(message, ' ');
				if (split1.length > 1) {
					String categoryString = split1[1];
					Integer category = null;
					if (categoryString.equalsIgnoreCase("простое")) {
						category = 0;
					}
					if (categoryString.equalsIgnoreCase("сложное")) {
						category = 1;
					}
					if (category != null) {
						foundCustomLogicCommand = true;
						String res = dareService.rollDare(category);
						event.sendMessageWithMention(res);
					}
				}
			}
			if (!foundCustomLogicCommand && botConfig.getCustomCommandsOn()) {
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
			if (botConfig.getCustomCommandsOn()) {
				if (split[0].toLowerCase().startsWith("!command")) {
					if (split[1].toLowerCase().startsWith("add") || split[1].toLowerCase().startsWith("edit") || split[1].toLowerCase().startsWith("alias")) {
						if (split.length > 3) {
							if (split[1].toLowerCase().startsWith("add"))
								message = StringUtils.replace(message, "!command add " + split[2] + " ", "");
							if (split[1].toLowerCase().startsWith("edit"))
								message = StringUtils.replace(message, "!command edit " + split[2] + " ", "");
							if (split[1].toLowerCase().startsWith("alias")) {
								CommandService commandService = context.getBean(CommandService.class);
								String res = commandService.addCommandAlias(split[2], split[3]);
								event.sendMessageWithMention(res);
							}
							if (split[1].toLowerCase().startsWith("enable")) {
								CommandService commandService = context.getBean(CommandService.class);
								String res = commandService.enableCommand(split[2]);
								event.sendMessageWithMention(res);
							}
							if (split[1].toLowerCase().startsWith("disable")) {
								CommandService commandService = context.getBean(CommandService.class);
								String res = commandService.disableCommand(split[2]);
								event.sendMessageWithMention(res);
							} else if (message.toLowerCase().startsWith("!command")) {
								event.sendMessageWithMention("Что-то пошло не так...");
							} else {
								CommandService commandService = context.getBean(CommandService.class);
								String res = commandService.addOrEditCommand(split[2], message);
								event.sendMessageWithMention(res);
							}
						}
					}
				}
			}
			if (botConfig.getDiscordOn()) {
				DiscordProcessor discordProcessor = context.getBean(DiscordProcessor.class);
				if (event.getMessage().toLowerCase().startsWith("!votediscord")) {
					ArrayList<Emote> emotes = new ArrayList<>(discordProcessor.getEmotes());
					String[] split2 = StringUtils.split(event.getMessage(), ' ');
					ArrayList<Emote> toAdd = new ArrayList<>();
					boolean skippedFirst = false;
					String resTest = "";
					for (String text : split2) {
						if (!skippedFirst) {
							skippedFirst = true;
						} else {
							Boolean emoteRdy = false;
							Emote emote = null;
							while (!emoteRdy) {
								Collections.shuffle(emotes);
								emote = emotes.get(0);
								if (!toAdd.contains(emote)) {
									emoteRdy = true;
								}
							}
							resTest = resTest + text + " " + emote.getAsMention() + " \n";
							toAdd.add(emote);
						}
					}
					Message complete = discordProcessor.subsChannel.sendMessage(resTest).complete();
					String latestMessageId = complete.getId();
					logger.info(latestMessageId);
					for (Emote e : toAdd) {
						RestAction<Void> voidRestAction = discordProcessor.subsChannel.addReactionById(latestMessageId, e);
						voidRestAction.queue();
					}
				}
			}
			if (botConfig.getQueueOn()) {
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
							Integer numberOfPeople = Integer.valueOf(split2[2]);
							selected = queueProcessor.roll(split2[0], numberOfPeople);
						} catch (NumberFormatException e) {
							logger.error(e);
						}
						if (selected == null || selected.size() == 0) {

						} else {
							event.sendMessageWithMention("Были выбраны: " + FerretBotUtils.buildMergedViewersNicknamesWithMention(selected));
						}
					}
				}
			}
			if (botConfig.getDareOn()) {
				if (message.equalsIgnoreCase("!желание add")) {
					String[] split1 = StringUtils.split(message, ' ');
					if (split1.length >= 4) {
						String categoryString = split1[2];
						Integer category = null;
						if (categoryString.equalsIgnoreCase("простое")) {
							category = 1;
						} else if (categoryString.equalsIgnoreCase("сложное")) {
							category = 2;
						}
						if (category != null) {
							String text = message;
							text = text.replace("!желание add " + categoryString + ' ', "");
							dareService.addOrEditDare(category, text);
							if (category == 0) {
								event.sendMessageWithMention("Простое желание успешно добавлено!");
							} else if (category == 1) {
								event.sendMessageWithMention("Сложное желание успешно добавлено!");
							} else {
								event.sendMessageWithMention("Желание успешно добавлено!");
							}
						}
					}
				}
			}
		}
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
				logger.info("Points transfer initiated by " + event.getLogin() + ", from " + split[1] + " to " + split[2] + " amount " + split[3]);
				Long sum = Long.parseLong(split[3]);
				event.sendMessage(FerretBotUtils.buildRemovePointsMessage(split[1], sum));
				viewerService.removePoints(split[1], sum);
				event.sendMessage(FerretBotUtils.buildMessageAddPoints(split[2], sum));
				viewerService.addPoints(split[2], sum);
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
			event.sendMessage("/unban " + login);
			event.sendMessageWithMention("Зритель был отмечен как одобреный. Приятного общения!");
		}
	}

	public void proceedSubAlert(UserNoticeEventWrapper wrapper) {
		this.proceedSubAlert(wrapper, false);
	}

	public void proceedSubAlert(UserNoticeEventWrapper wrapper, boolean isGift) {
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
			String paymentMessage = FerretBotUtils.buildMessageAddPoints(login, points);
			viewerService.addPoints(login, points);
			if (wrapper.getTag("msg-id").equalsIgnoreCase("resub")) {
				String msgParamMonth = wrapper.getTag("msg-param-months");
				try {
					Integer subStreak = Integer.valueOf(msgParamMonth);
					if (subStreak != null) {
						viewerService.setSubStreak(login, subStreak);
					}
				} catch (NumberFormatException ex) {
					logger.warn("Could not parse sub streak to Integer. Value: " + msgParamMonth, ex);
				}
			}
			wrapper.sendMessage(paymentMessage);
			wrapper.sendMessage("Спасибо за подписку, " + loginForThanks + "!");
		}
	}

	protected void alias(ChannelMessageEventWrapper event, String message) {
		String[] split = StringUtils.split(message, ' ');
		if (split.length == 3) {
			String answer = viewerLootsMapService.updateAlias(split[1], split[2]);
			logger.info("User change ended with following message: " + answer);
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
}
