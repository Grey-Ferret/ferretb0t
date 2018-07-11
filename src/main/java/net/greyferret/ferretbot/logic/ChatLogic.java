package net.greyferret.ferretbot.logic;

import net.greyferret.ferretbot.client.ReadyCheckClient;
import net.greyferret.ferretbot.config.BotConfig;
import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.LootsConfig;
import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.service.CommandService;
import net.greyferret.ferretbot.service.ViewerLootsMapService;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import net.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import net.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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

	/***
	 * Logic for chat commands for everyone
	 *
	 * @param event
	 */
	public void proceedCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		if (botConfig.getReadyCheckOn()) {
			if (message.startsWith("!go")) {
				if (message.startsWith("!go remove")) {
					proceedGoRemove(event);
				} else if (message.startsWith("!go size")) {
					proceedGoSize(event);
				} else if (message.startsWith("!go status")) {
					proceedGoStatus(event);
				} else {
					message = message.replaceAll("\\s+", "");
					if (message.equalsIgnoreCase("!go")) {
						proceedGoAdd(event);
					}
				}
			}
		}

		if (botConfig.getCustomCommandsOn()) {
			String[] split = message.split(" ");
			CommandService commandService = context.getBean(CommandService.class);
			commandService.proceedTextCommand(split[0].toLowerCase(), event);
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
					if (split[1].toLowerCase().startsWith("add") || split[1].toLowerCase().startsWith("edit")) {
						if (split.length > 3) {
							if (split[1].toLowerCase().startsWith("add"))
								message = StringUtils.replace(message, "!command add " + split[2] + " ", "");
							if (split[1].toLowerCase().startsWith("edit"))
								message = StringUtils.replace(message, "!command edit " + split[2] + " ", "");
							if (message.toLowerCase().startsWith("!command")) {
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

			if (botConfig.getReadyCheckOn()) {
				if (message.startsWith("!go")) {
					if (message.startsWith("!go select")) {
						proceedGoSelect(event);
					} else if (message.startsWith("!go reset")) {
						resetGoSelect(event);
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

		if (message.startsWith("!go return")) {
			proceedGoReturn(event);
		}
	}

	private void proceedGoStatus(ChannelMessageEventWrapper event) {
		Viewer viewer = viewerService.getViewerByName(event.getLogin());
		if (viewer != null) {
			int goStatus = viewer.getGoStatus();
			if (goStatus == 0) {
				event.sendMessageWithMention(" сейчас не в очереди");
				return;
			}
			if (goStatus == 1) {
				event.sendMessageWithMention(" уже в очереди");
				return;
			}
			if (goStatus == 2) {
				event.sendMessageWithMention(" уже играл. Купи возврат за поинты или дождись обновления очереди!");
				return;
			}
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

	protected void proceedGoAdd(ChannelMessageEventWrapper event) {
		Viewer viewer = viewerService.getViewerByName(event.getLogin());
		if (viewer == null) {
			return;
		}
		viewerService.addToGoList(viewer, event);
	}

	protected void proceedGoRemove(ChannelMessageEventWrapper event) {
		Viewer viewer = viewerService.getViewerByName(event.getLogin());
		if (viewer == null) {
			return;
		}
		viewerService.removeToGoList(viewer, event);
	}

	protected void proceedGoSize(ChannelMessageEventWrapper event) {
		int goListSize = viewerService.goListSize(event);
		int goListBlockedSize = viewerService.goListBlockedSize(event);
		event.sendMessageWithMention("Количество человек в очереди: " + goListSize + ", количество человек что уже играло: " + goListBlockedSize);
	}

	protected void proceedGoSelect(ChannelMessageEventWrapper event) {
		String[] split = StringUtils.split(event.getMessage(), " ");
		int numberOfPeople = 0;
		if (split.length >= 3) {
			try {
				numberOfPeople = Integer.parseInt(split[2]);
			} catch (NumberFormatException ex) {
				logger.error(ex);
			}
			if (numberOfPeople != 0) {
				HashSet<Viewer> viewers = viewerService.selectGoList(numberOfPeople);
				if (viewers.size() == 0) {
					event.sendMessageWithMention(" никого нет в очереди...");
					return;
				}
				ReadyCheckClient readyCheckClient = context.getBean(ReadyCheckClient.class);
				event.sendMessageWithMention("Были выбраны: " + FerretBotUtils.buildMergedViewersNicknames(viewers));
				event.sendMessage(FerretBotUtils.buildMergedViewersNicknamesWithMention(viewers) + " напишите в чат в течение минуты для подтверждения участия!");
				readyCheckClient.addReadyCheckList(viewers);
				readyCheckClient.setNickForReply(event.getLogin());
				Thread readyCheckThread = new Thread(readyCheckClient);
				readyCheckThread.setName("ReadyCheck Thread");
				readyCheckThread.start();
			}
		}
	}

	protected void proceedGoReturn(ChannelMessageEventWrapper event) {
		String[] split = StringUtils.split(event.getMessage(), " ");
		String login = split[2];
		viewerService.returnToGoList(login, event);
	}

	protected void resetGoSelect(ChannelMessageEventWrapper event) {
		viewerService.resetGoList(event);
	}
}
