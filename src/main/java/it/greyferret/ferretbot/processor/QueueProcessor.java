package it.greyferret.ferretbot.processor;

import it.greyferret.ferretbot.config.ChatConfig;
import it.greyferret.ferretbot.entity.Viewer;
import it.greyferret.ferretbot.logic.ChatLogic;
import it.greyferret.ferretbot.service.ViewerService;
import it.greyferret.ferretbot.util.FerretBotUtils;
import it.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QueueProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(ChatLogic.class);

	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ChatConfig chatConfig;

	private ConcurrentHashMap<String, HashSet<String>> queueMap;
	private boolean isOn;

	@PostConstruct
	private void postConstruct() {
		isOn = true;
		queueMap = new ConcurrentHashMap<>();
		registerQueue("go");
		registerQueue("ksquad");
	}

	@Override
	public void run() {
	}

	public void proceed(ChannelMessageEventWrapper event) {
		if (event.getLogin().equalsIgnoreCase(chatConfig.getChannel())) {
			return;
		}
		if (event.getMessage().length() > 2) {
			String s = event.getMessage().substring(1).toLowerCase();
			String[] split = s.split(" ");
			if (queueMap.containsKey(split[0])) {
				HashSet<String> viewers = queueMap.get(split[0]);
				viewers.add(event.getLogin().toLowerCase());
				queueMap.put(split[0], viewers);
			}
		}
	}

	public HashSet<Viewer> roll(String queueName, int numberOfPeople) {
		queueName = queueName.replaceAll("!", "").toLowerCase();
		if (!queueName.contains(queueName)) {
			return new HashSet<>();
		}
		HashSet<String> names = queueMap.get(queueName);
		HashSet<Viewer> viewers = new HashSet<>();
		for (String name : names) {
			Viewer viewer = viewerService.getViewerByName(name);
			viewers.add(viewer);
		}
		ArrayList<Viewer> viewersToRoll = FerretBotUtils.combineViewerListWithSubluck(viewers, 2);
		HashSet<Viewer> result = new HashSet<>();
		for (int i = 0; i < numberOfPeople; i++) {
			if (viewersToRoll.size() > 0) {
				Collections.shuffle(viewersToRoll);
				Viewer viewer = viewersToRoll.get(0);
				result.add(viewer);
				ArrayList<Viewer> newViewersToRoll = new ArrayList<>();
				for (Viewer viewerToRoll : viewersToRoll) {
					if (!viewer.getLogin().equalsIgnoreCase(viewerToRoll.getLogin())) {
						newViewersToRoll.add(viewerToRoll);
					}
				}
				viewersToRoll = newViewersToRoll;
			}
		}

		HashSet<String> newNames = new HashSet<>();
		for (String viewerToRoll : names) {
			boolean found = false;
			for (Viewer viewer : result) {
				if (viewer.getLogin().equalsIgnoreCase(viewerToRoll)) {
					found = true;
				}
			}
			if (!found) {
				newNames.add(viewerToRoll);
			}
		}
		queueMap.put(queueName, newNames);
		return result;
	}

	public boolean registerQueue(String queueName) {
		queueName = queueName.toLowerCase();
		if (queueMap.containsKey(queueName)) {
			return false;
		} else {
			queueMap.put(queueName, new HashSet<>());
			return true;
		}
	}

	public boolean resetQueue(String queueName) {
		queueName = queueName.toLowerCase();
		if (queueMap.containsKey(queueName)) {
			queueMap.put(queueName, new HashSet<>());
			return true;
		} else {
			return false;
		}
	}

	public boolean deleteQueue(String queueName) {
		queueName = queueName.toLowerCase();
		if (queueMap.containsKey(queueName)) {
			queueMap.remove(queueName);
			return true;
		}
		return false;
	}
}
