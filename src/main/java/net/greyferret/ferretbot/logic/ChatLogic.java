package net.greyferret.ferretbot.logic;

import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.service.CommandService;
import net.greyferret.ferretbot.service.ViewerLootsMapService;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import net.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ChatLogic {
	private static final Logger logger = LogManager.getLogger(ChatLogic.class);

	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ViewerLootsMapService viewerLootsMapService;
	@Autowired
	private CommandService commandService;

	/***
	 * Logic for chat commands for everyone
	 *
	 * @param event
	 */
	public void proceedCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		if (message.startsWith("!go")) {
			if (message.startsWith("!go remove")) {
				proceedGoRemove(event);
			} else {
				message = message.replaceAll("\\s+", "");
				if (message.equalsIgnoreCase("!go")) {
					proceedGoAdd(event);
				}
			}
		} else {
			String[] split = message.split(" ");
			commandService.proceedTextCommand(split[0], event);
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
							String res = commandService.addOrEditCommand(split[2], message);
							event.sendMessageWithMention(res);
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

		if (message.startsWith("!info") || message.startsWith("!test") || message.startsWith("!status")) {
			event.sendMessageWithMention("A marvel of technology - an engine of destruction!");
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

		if (message.startsWith("!go")) {
			if (message.startsWith("!go return")) {
				proceedGoReturn(event);
			}
			if (message.startsWith("!go select")) {
				proceedGoSelect(event);
			}
			if (message.startsWith("!go reset")) {
				resetGoSelect(event);
			}
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

	protected void proceedGoSelect(ChannelMessageEventWrapper event) {
		String[] split = StringUtils.split(event.getMessage(), " ");
		int numberOfPeople = 0;
		if (split.length >= 3) {
			try {
				numberOfPeople = Integer.parseInt(split[2]);
			} catch (NumberFormatException ex) {

			}
			if (numberOfPeople != 0) {
				viewerService.selectGoList(numberOfPeople, event);
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
