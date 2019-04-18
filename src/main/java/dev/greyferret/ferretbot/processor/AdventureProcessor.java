package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.client.FerretChatClient;
import dev.greyferret.ferretbot.entity.Adventure;
import dev.greyferret.ferretbot.entity.AdventureResponse;
import dev.greyferret.ferretbot.entity.Adventurer;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.service.AdventureService;
import dev.greyferret.ferretbot.service.ViewerService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

@Component
public class AdventureProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(AdventureProcessor.class);

	@Autowired
	private ViewerService viewerService;
	@Autowired
	private AdventureService adventureService;
	@Autowired
	private StreamElementsAPIProcessor streamElementsAPIProcessor;
	@Autowired
	private FerretChatClient ferretChatClient;

	private boolean isOn = true;
	private AdventureStage adventureStage = AdventureStage.READY;
	private Adventure adventure = null;
	private HashSet<Adventurer> adventurers = new HashSet<>();
	private HashMap<String, AdventureResponse> responses = new HashMap<>();
	private long cost = 20L;
	private int step = 1;
	private int stepsMax = 3;

	@Override
	public void run() {
		long sleepTimer = 60000;
		while (isOn) {
			try {
				Thread.sleep(sleepTimer);
				this.proceedLogic();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	private void proceedLogic() throws InterruptedException {
		logger.info(adventureStage);
		if (adventureStage == AdventureStage.WAITING) {
			logger.info("Waiting Stage, 14 mins waiting");
			Thread.sleep(14 * 60 * 1000);
			logger.info("Ready Stage!");
			adventureStage = AdventureStage.READY;
		} else if (adventureStage == AdventureStage.READY) {

		} else if (adventureStage == AdventureStage.LFG || adventureStage == AdventureStage.PROCEEDING) {
			logger.info("LFG/PROCEEDING Stage, 2 mins waiting");
			Thread.sleep(2 * 60 * 1000);
			logger.info("Answering Stage!");
			adventureStage = AdventureStage.ANSWERING;
			proceedStage();
		} else if (adventureStage == AdventureStage.ANSWERING) {
			logger.info("ANSWERING Stage, 2 mins waiting");
			Thread.sleep(2 * 60 * 1000);
			logger.info("Answering done.");
			if (this.step < stepsMax) {
				adventureStage = AdventureStage.PROCEEDING;
				step++;
			} else {
				adventureStage = AdventureStage.WAITING;
			}
			endStage();
		}
	}

	private void endStage() {
		Random rand = new Random();
		int i = rand.nextInt(responses.size());
		AdventureResponse winningResponse = null;
		int j = 0;
		for (String key : responses.keySet()) {
			winningResponse = responses.get(key);
			if (j == i) {
				break;
			}
			j++;
		}
		ferretChatClient.sendMessage(winningResponse.getResponse());
		int aliveAdventurers = 0;
		String deadMen = "";
		for (Adventurer adventurer : adventurers) {
			if (adventurer.getLives() > 0) {
				if (StringUtils.isNoneBlank(adventurer.getSelectedKey())) {
					if (adventurer.getSelectedKey().equalsIgnoreCase(winningResponse.getKey())) {

					} else {
						adventurer.setLives(adventurer.getLives() - 1);
					}
					if (adventurer.getLives() > 0) {
						aliveAdventurers++;
					} else {
						if (StringUtils.isNotBlank(deadMen)) {
							deadMen = deadMen + ", ";
						}
						deadMen = deadMen + adventurer.getViewer().getLoginVisual();
					}
					adventurer.setSelectedKey("");
				}
			}
		}
		if (step == stepsMax) {
			if (aliveAdventurers > 0) {
				ferretChatClient.sendMessage("У нас есть победители! Победителям начисляется: " + adventurers.size() * cost * 1.5 + " iq.");
				for (Adventurer adventurer : adventurers) {
					if (adventurer.getLives() > 0) {
						streamElementsAPIProcessor.updatePoints(adventurer.getViewer().getLogin(), (long) (adventurers.size() * cost * 1.5));
					}
				}
			} else {
				ferretChatClient.sendMessage("К сожалению все померли, в самом конце. ГГ.");
			}
			adventureStage = AdventureStage.WAITING;
		} else {
			if (aliveAdventurers > 0) {
				ferretChatClient.sendMessage("В живых осталось: " + aliveAdventurers);
				if (StringUtils.isNotBlank(deadMen)) {
					ferretChatClient.sendMessage("Погибают: " + deadMen);
				}
			} else {
				adventureStage = AdventureStage.WAITING;
				ferretChatClient.sendMessage("К сожалению все померли. ГГ.");
			}
		}
	}

	public void setAdventurerResponse(ChannelMessageEventWrapper event) {
		for (Adventurer adventurer : adventurers) {
			if (adventurer.getViewer().getLogin().equalsIgnoreCase(event.getLogin())) {
				if (adventurer.getLives() <= 0) {
					return;
				}
				String message = event.getMessage().toLowerCase();
				message = message.replace("!", "");
				String selectedKey = message.substring(0, 1);
				if (responses.keySet().contains(selectedKey.toLowerCase())) {
					adventurer.setSelectedKey(selectedKey);
					logger.info("For adventurer " + adventurer.getViewer().getLogin() + " set selected option for " + adventurer.getSelectedKey());
				}
				return;
			}
		}
	}

	private void proceedStage() {
		if (step == 1) {
			adventure = adventureService.getStartAdventure();
		} else if (step == stepsMax) {
			adventure = adventureService.getFinalAdventure();
		} else {
			adventure = adventureService.getAdventure();
		}
		ferretChatClient.sendMessage(adventure.getText());
		responses = adventureService.getAdventureResponses(adventure.getId());
		ferretChatClient.sendMessage(FerretBotUtils.formAdventureResponses(responses));
	}

	public void checkAdventure(ChannelMessageEventWrapper event) {
		if (this.adventureStage == AdventureStage.READY) {
			this.startAdventure(event);
		} else if (this.adventureStage == AdventureStage.WAITING) {
			event.sendMessageWithMention("Поход не готов");
		} else if (this.adventureStage == AdventureStage.LFG) {
			event.sendMessageWithMention("Кто-то уже идет в поход! Пиши !иду чтобы принять участие!");
		} else {
			event.sendMessageWithMention("Путешественники уже утопали...");
		}
	}

	public void joinAdventure(ChannelMessageEventWrapper event) {
		if (adventureStage == AdventureStage.ANSWERING || adventureStage == AdventureStage.PROCEEDING) {
			event.sendMessageWithMention("Поезд ушел...");
		} else if (adventureStage == AdventureStage.LFG) {
			Viewer viewer = viewerService.getViewerByName(event.getLogin().toLowerCase());
			Adventurer adventurer = new Adventurer(viewer);
			if (adventurers.contains(adventurer)) {
				event.sendMessageWithMention("Уже в походе! Ждем начала...");
			} else {
//				boolean updated = streamElementsAPIProcessor.updatePoints(event.getLogin(), -1 * this.getCost());
				boolean updated = true;
				if (updated) {
					adventurers.add(adventurer);
					event.sendMessageWithMention("Стоимость уплачена, ждем начала похода.");
				}
			}
		} else {
			event.sendMessageWithMention("Поход не проходит.");
		}
	}

	private void startAdventure(ChannelMessageEventWrapper event) {
		Random rand = new Random();
		this.cost = rand.nextInt(90) + 10L;
		this.adventureStage = AdventureStage.LFG;
		this.step = 1;
		event.sendMessage(event.getLoginVisual() + " собирает в поход! Делай паунс в окно если хочешь принять участие!");
		this.adventurers = new HashSet<>();
		this.responses = new HashMap<>();
		Viewer viewer = viewerService.getViewerByName(event.getLogin().toLowerCase());
		adventurers.add(new Adventurer(viewer));
		this.adventure = adventureService.getStartAdventure();
	}

	public Long getCost() {
		return cost;
	}

	public boolean isAdventureLive() {
		return this.adventure != null;
	}

	public void checkAdventurer(ChannelMessageEventWrapper event) {
		Adventurer adventurer = null;
		for (Adventurer _adventurer : adventurers) {
			if (_adventurer.getViewer().getLogin().equalsIgnoreCase(event.getLogin())) {
				adventurer = _adventurer;
			}
		}
		if (adventurer == null) {
			event.sendMessageWithMention("Вы не участвовали в походе.");
		} else {
			event.sendMessageWithMention("Кол-во жизней: " + adventurer.getLives());
		}
	}

	public enum AdventureStage {
		WAITING,
		LFG,
		ANSWERING,
		READY,
		PROCEEDING
	}
}
