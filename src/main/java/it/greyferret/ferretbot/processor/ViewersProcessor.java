package it.greyferret.ferretbot.processor;

import it.greyferret.ferretbot.client.FerretChatClient;
import it.greyferret.ferretbot.config.BotConfig;
import it.greyferret.ferretbot.config.ChatConfig;
import it.greyferret.ferretbot.entity.Viewer;
import it.greyferret.ferretbot.service.ViewerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
@EnableConfigurationProperties({ChatConfig.class, BotConfig.class})
public class ViewersProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(ViewersProcessor.class);

	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private ApiProcessor apiProcessor;
	@Autowired
	private BotConfig botConfig;

	private boolean isOn;
	private int checkNumber;
	private HashSet<Viewer> viewersToAddPoints;
	private ArrayList<Viewer> viewersToRoll;

	private ViewersProcessor() {
	}

	@PostConstruct
	private void postConstruct() {
		isOn = true;
		apiProcessor = context.getBean(ApiProcessor.class);
		resetViewersToAddPoints();
		viewersToRoll = new ArrayList<>();
	}

	private void resetViewersToAddPoints() {
		checkNumber = 0;
		viewersToAddPoints = new HashSet<>();
	}

	/***
	 * Main run method
	 */
	@Override
	public void run() {
		boolean lastResult = false;
		while (isOn) {
			Integer retryMs;
			if (lastResult == true)
				retryMs = 60000;
			else
				retryMs = 5000;

			try {
				Thread.sleep(retryMs);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			if (botConfig.getViewersPassivePointsOn()) {
				lastResult = checkViewersAndAddPoints();
			} else {
				lastResult = true;
			}
		}
	}

	public void rollSmack(String author) {
		rollSelectedPeople(author, 2);
	}

	private void rollSelectedPeople(String author, int type) {
		ArrayList<Viewer> temp = new ArrayList<>(viewersToAddPoints);
		if (temp != null && temp.size() > 0) {
			viewersToRoll = temp;
		}
		Collections.shuffle(viewersToRoll);
		if (viewersToRoll.size() > 1) {
			Viewer viewer = viewersToRoll.get(0);
			if (!viewer.isSuitableForRaffle()) {
				viewer = viewersToRoll.get(1);
			}
			FerretChatClient ferretChatClient = context.getBean("FerretChatClient", FerretChatClient.class);
			if (viewer != null) {
				if (type == 1) {
					ferretChatClient.sendMessage(author + " по-дружески обнимает " + viewer.getLoginVisual() + " KappaPride");
				} else if (type == 2) {
					ferretChatClient.sendMessage(author + " отвесил подзатыльник " + viewer.getLoginVisual() + " SMOrc");
				}
			}
		}
	}

	public void rollHug(String author) {
		rollSelectedPeople(author, 1);
	}

	private boolean checkViewersAndAddPoints() {
		boolean lastResult;
		boolean isChannelOnline = apiProcessor.getChannelStatus();
		List<String> nicknames = context.getBean("getViewers", ArrayList.class);

		if (nicknames.size() > 1) {
			HashSet<Viewer> viewers = viewerService.checkViewers(nicknames);
			viewersToAddPoints.addAll(viewers);
//				logger.info("User list (" + nicknames.size() + ") was refreshed!");
			checkNumber++;
			if (checkNumber >= chatConfig.getUsersCheckMins()) {
				if (isChannelOnline) {
					viewerService.addPointsForViewers(viewersToAddPoints);
					logger.info("Adding points for being on channel for " + viewersToAddPoints.size() + " users");
				}
				resetViewersToAddPoints();
			}
			lastResult = true;
		} else {
			lastResult = false;
		}
		return lastResult;
	}
}
