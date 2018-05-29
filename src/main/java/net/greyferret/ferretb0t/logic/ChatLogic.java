package net.greyferret.ferretb0t.logic;

import net.greyferret.ferretb0t.entity.Viewer;
import net.greyferret.ferretb0t.service.LootsService;
import net.greyferret.ferretb0t.service.ViewerLootsMapService;
import net.greyferret.ferretb0t.service.ViewerService;
import net.greyferret.ferretb0t.wrapper.ChannelMessageEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ChatLogic {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ViewerLootsMapService viewerLootsMapService;
	@Autowired
	private LootsService lootsService;

	public void alias(ChannelMessageEventWrapper event, String message) {
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

	public void repair(ChannelMessageEventWrapper event) {
		Set<String> lootsForRepair = viewerLootsMapService.getRepairList();
		if (lootsForRepair == null || lootsForRepair.size() == 0) {
			event.sendMessage("Nothing to repair! :)");
			return;
		}
		String repairNames = StringUtils.join(lootsForRepair, ", ");
		event.sendMessage("To fix: " + repairNames);
	}

	public void proceedGoAdd(ChannelMessageEventWrapper event) {
		Viewer viewer = viewerService.getViewerByName(event.getLogin());
		if (viewer == null) {
			return;
		}
		viewerService.addToGoList(viewer, event);
	}

	public void proceedGoRemove(ChannelMessageEventWrapper event) {
		Viewer viewer = viewerService.getViewerByName(event.getLogin());
		if (viewer == null) {
			return;
		}
		viewerService.removeToGoList(viewer, event);
	}

	public void proceedGoSelect(ChannelMessageEventWrapper event) {
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

	public void proceedGoReturn(ChannelMessageEventWrapper event) {
		String[] split = StringUtils.split(event.getMessage(), " ");
		String login = split[2];
		viewerService.returnToGoList(login, event);
	}

	public void resetGoSelect(ChannelMessageEventWrapper event) {
		viewerService.resetGoList(event);
	}

	public void aliasDelete(ChannelMessageEventWrapper event, String message) {
		String[] split = StringUtils.split(message, ' ');
		if (split.length == 2) {
			String answer = viewerLootsMapService.deleteViewerLootsMap(split[1]);
			event.sendMessageWithMention(answer);
		}
	}
}
