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
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AdventureProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(AdventureProcessor.class);

	@Autowired
	private ViewerService viewerService;
	@Autowired
	private AdventureService adventureService;
	@Autowired
	private ApiProcessor apiProcessor;
	@Autowired
	private StreamElementsAPIProcessor streamElementsAPIProcessor;
	@Autowired
	private FerretChatClient ferretChatClient;

	private boolean isOn = true;
	private AdventureStage adventureStage = AdventureStage.READY;
	private Adventure adventure = null;
	private HashSet<Adventurer> adventurers = new HashSet<>();
	private HashMap<String, AdventureResponse> responses = new HashMap<>();
	private long cost = 10L;
	private int step = 1;
	private int stepsMax = 5;

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
		logger.info(step);
		if (adventureStage != AdventureStage.READY && adventureStage != AdventureStage.WAITING) {
			for (Adventurer adventurer : adventurers) {
				logger.info(adventurer.toString());
			}
		}
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
			endStage();
			if (adventureStage != AdventureStage.WAITING) {
				if (this.step < stepsMax) {
					adventureStage = AdventureStage.PROCEEDING;
					step++;
				} else {
					adventureStage = AdventureStage.WAITING;
				}
			}
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
		ferretChatClient.sendMessageMe(winningResponse.getResponse());
		int aliveAdventurers = 0;
		String deadMen = "";
		for (Adventurer adventurer : adventurers) {
			if (StringUtils.isNoneBlank(adventurer.getSelectedKey())) {
				if (adventurer.getSelectedKey().equalsIgnoreCase(winningResponse.getKey())) {

				} else {
					deadMen = proceedLosingOption(adventurer, deadMen);
				}
				if (adventurer.getLives() > 0) {
					aliveAdventurers++;
				}
				adventurer.setSelectedKey("");
			} else {
				deadMen = proceedLosingOption(adventurer, deadMen);
				if (adventurer.getLives() > 0) {
					aliveAdventurers++;
				}
				adventurer.setSelectedKey("");
			}
		}
		if (step == stepsMax) {
			if (aliveAdventurers > 0) {
				int prize = calcPrize(aliveAdventurers);
				ferretChatClient.sendMessageMe("У нас есть победители, что одолели лабу! Победителям начисляется: " + prize + " iq.");
				for (Adventurer adventurer : adventurers) {
					if (adventurer.getLives() > 0) {
						streamElementsAPIProcessor.updatePoints(adventurer.getViewer().getLogin(), Long.valueOf(prize));
					}
				}
			} else {
				ferretChatClient.sendMessageMe("К сожалению, поход трагически закончился разгромом в самом последнем этапе...");
			}
			adventureStage = AdventureStage.WAITING;
		} else {
			if (aliveAdventurers > 0) {
				String res = "";
				if (StringUtils.isNotBlank(deadMen)) {
					res = "К сожалению, " + deadMen + " не смогли преодолеть этап. ";
				}
				ferretChatClient.sendMessageMe(res + "А поход продолжают " + aliveAdventurers + " приключенцев.");

			} else {
				adventureStage = AdventureStage.WAITING;
				ferretChatClient.sendMessageMe("К сожалению поход окончился трагично. Повезет в следующий раз!");
			}
		}
	}

	private String proceedLosingOption(Adventurer adventurer, String deadMen) {
		if (adventurer.getLives() == 1) {
			if (StringUtils.isNotBlank(deadMen)) {
				deadMen = deadMen + ", ";
			}
			deadMen = deadMen + adventurer.getViewer().getLoginVisual();
		}
		adventurer.setLives(adventurer.getLives() - 1);
		return deadMen;
	}

	private int calcPrize(int aliveAdventurers) {
		long l = ThreadLocalRandom.current().nextLong((long) 0.5, 2L);
		long calced = cost * adventurers.size() * stepsMax * l / aliveAdventurers / 2;
		logger.info(calced);
		return Math.round(calced);
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
		ferretChatClient.sendMessageMe(adventure.getText());
		responses = adventureService.getAdventureResponses(adventure.getId());
		ferretChatClient.sendMessageMe(FerretBotUtils.formAdventureResponses(responses));
	}

	public void checkAdventure(ChannelMessageEventWrapper event) {
		boolean channelStatus = apiProcessor.getChannelStatus();
		if (!channelStatus) {
			event.sendMessageWithMentionMe("В поход можно идти только когда канал онлайн.");
			return;
		}
		if (this.adventureStage == AdventureStage.READY) {
			this.startAdventure(event);
		} else if (this.adventureStage == AdventureStage.WAITING) {
			event.sendMessageWithMentionMe("Поход не готов, мы в поисках новой лаборотории для рейда.");
		} else if (this.adventureStage == AdventureStage.LFG) {
			event.sendMessageWithMentionMe("Кто-то уже собирает в поход! Пиши !иду чтобы принять участие!");
		} else {
			event.sendMessageWithMentionMe("Путешественники уже утопали...");
		}
	}

	public void joinAdventure(ChannelMessageEventWrapper event) {
		if (adventureStage == AdventureStage.ANSWERING || adventureStage == AdventureStage.PROCEEDING) {
			event.sendMessageWithMentionMe("Поезд ушел...");
		} else if (adventureStage == AdventureStage.LFG) {
			Viewer viewer = viewerService.getViewerByName(event.getLogin().toLowerCase());
			Adventurer adventurer = new Adventurer(viewer);
			if (adventurers.contains(adventurer)) {
				event.sendMessageWithMentionMe("Вас уже записали! Откиньтесь на спинку стула и ждите начала.");
			} else {
				boolean updated = streamElementsAPIProcessor.updatePoints(event.getLogin(), -1 * this.cost);
				if (updated) {
					adventurers.add(adventurer);
					event.sendMessageWithMentionMe("Стоимость уплачена, ждем начала похода.");
				} else {
					event.sendMessageWithMentionMe("У вас не хватает IQ для похода. Требуется " + cost + " IQ.");
				}
			}
		} else {
			event.sendMessageWithMentionMe("Поход не проходит.");
		}
	}

	private void startAdventure(ChannelMessageEventWrapper event) {
		Random rand = new Random();
		this.adventureStage = AdventureStage.LFG;
		this.step = 1;
		//		this.cost = rand.nextInt(90) + 10L;
		event.sendMessageMe(event.getLoginVisual() + " собирает в поход! Для того чтобы принять участие в походе, пишите в чат !иду - стоимость экипировки для похода: " + this.cost + " IQ.");
		this.adventurers = new HashSet<>();
		this.responses = new HashMap<>();
		Viewer viewer = viewerService.getViewerByName(event.getLogin().toLowerCase());
		adventurers.add(new Adventurer(viewer));
		this.adventure = adventureService.getStartAdventure();
	}

	public void checkAdventurer(ChannelMessageEventWrapper event) {
		Adventurer adventurer = null;
		for (Adventurer _adventurer : adventurers) {
			if (_adventurer.getViewer().getLogin().equalsIgnoreCase(event.getLogin())) {
				adventurer = _adventurer;
			}
		}
		if (adventurer == null) {
			event.sendMessageWithMentionMe("Хм... Вас нет в списках похода.");
		} else {
			event.sendMessageWithMentionMe("Количество жизней: " + adventurer.getLives());
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
