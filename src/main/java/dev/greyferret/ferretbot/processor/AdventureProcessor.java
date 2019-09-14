package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.entity.Adventure;
import dev.greyferret.ferretbot.entity.AdventureResponse;
import dev.greyferret.ferretbot.entity.Adventurer;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.service.AdventureService;
import dev.greyferret.ferretbot.service.ViewerService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Log4j2
public class AdventureProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private AdventureService adventureService;
	@Autowired
	private ApiProcessor apiProcessor;
	@Autowired
	private FerretChatProcessor ferretChatClient;
	@Autowired
	private PointsProcessor pointsProcessor;

	private boolean isOn = true;
	private AdventureStage adventureStage = AdventureStage.READY;
	private Adventure adventure = null;
	private HashSet<Adventurer> adventurers = new HashSet<>();
	private HashMap<String, AdventureResponse> responses = new HashMap<>();
	private long cost = 10L;
	private int step = 1;
	private int stepsMax = 5;
	private HashSet<Long> pastAdventures = new HashSet<>();

	@Override
	public void run() {
		long sleepTimer = 60000;
		while (isOn) {
			try {
				Thread.sleep(sleepTimer);
				this.proceedLogic();
			} catch (InterruptedException e) {
				log.error(e.toString());
			}
		}
	}

	private void proceedLogic() throws InterruptedException {
		if (adventureStage == AdventureStage.WAITING) {
			log.info("Waiting Stage, 14 mins waiting");
			Thread.sleep(14 * 60 * 1000);
			log.info("Ready Stage!");
			adventureStage = AdventureStage.READY;
		} else if (adventureStage == AdventureStage.READY) {

		} else if (adventureStage == AdventureStage.LFG || adventureStage == AdventureStage.PROCEEDING) {
			log.info("LFG/PROCEEDING Stage, 2 mins waiting");
			Thread.sleep(2 * 60 * 1000);
			log.info("Answering Stage!");
			adventureStage = AdventureStage.ANSWERING;
			proceedStage();
		} else if (adventureStage == AdventureStage.ANSWERING) {
			log.info("ANSWERING Stage, 2 mins waiting");
			Thread.sleep(2 * 60 * 1000);
			log.info("Answering done.");
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
			} else {
				deadMen = proceedLosingOption(adventurer, deadMen);
			}

			if (adventurer.getLives() > 0) {
				aliveAdventurers++;
			}
			adventurer.setSelectedKey("");
		}
		if (step == stepsMax) {
			if (aliveAdventurers > 0) {
				int prize = calcPrize(aliveAdventurers);
				ferretChatClient.sendMessageMe("У нас есть победители, что одолели лабу! Победителям начисляется: " + prize + " iq.");
				HashSet<Viewer> adventurerViewers = new HashSet<>();
				for (Adventurer adventurer : adventurers) {
					if (adventurer.getLives() > 0) {
						pointsProcessor.updatePoints(adventurer.getViewer().getLogin(), Long.valueOf(prize));
						adventurerViewers.add(adventurer.getViewer());
					}
				}
				ferretChatClient.sendMessageMe("Победители: " + FerretBotUtils.buildMergedViewersNicknames(adventurerViewers));
			} else {
				ferretChatClient.sendMessageMe("К сожалению, поход трагически закончился разгромом в самом последнем этапе...");
			}
			adventureStage = AdventureStage.WAITING;
		} else {
			if (aliveAdventurers > 0) {
				String res = "";
				if (StringUtils.isNotBlank(deadMen)) {
					if (StringUtils.split(deadMen, ",").length <= 4) {
						res = "К сожалению, " + deadMen + " не смогли преодолеть этап. ";
					}
				}
				ferretChatClient.sendMessageMe(res + "Поход продолжают " + aliveAdventurers + " приключенцев.");

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
		if (adventurer.getLives() > 0) {
			adventurer.setLives(adventurer.getLives() - 1);
		}
		return deadMen;
	}

	private int calcPrize(int aliveAdventurers) {
		double l = ThreadLocalRandom.current().nextDouble(0.5, 3);
		log.info("Random koef " + l);
		log.info("Cost " + cost);
		log.info("adventurers.size " + adventurers.size());
		log.info("stepsMax " + stepsMax);
		log.info("aliveAdventurers " + aliveAdventurers);
		Double calcedD = cost * adventurers.size() * stepsMax * l / aliveAdventurers / 2;
		long calced = Math.round(calcedD);
		log.info(String.valueOf(calced));
		return Math.round(calced);
	}

	public void setAdventurerResponse(ChannelMessageEventWrapper event, String keyword) {
		for (Adventurer adventurer : adventurers) {
			if (adventurer.getViewer().getLogin().equalsIgnoreCase(event.getLogin())) {
				if (adventurer.getLives() <= 0) {
					return;
				}
				keyword = keyword.toLowerCase();
				if (responses.keySet().contains(keyword)) {
					adventurer.setSelectedKey(keyword);
					log.info("For adventurer " + adventurer.getViewer().getLogin() + " set selected option for " + adventurer.getSelectedKey());
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
			boolean added = false;
			while (!added) {
				adventure = adventureService.getAdventure();
				added = pastAdventures.add(adventure.getId());
			}
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
			event.sendMessageWithMentionMe("Поход не готов, мы в поисках новой лаборатории для рейда.");
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
				boolean updated = pointsProcessor.updatePoints(event.getLogin(), -1 * this.cost);
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
		pastAdventures = new HashSet<>();
		Random rand = new Random();
		this.adventureStage = AdventureStage.LFG;
		this.step = 1;
		this.cost = rand.nextInt(90) + 10L;
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

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		Thread thread = new Thread(this);
		thread.setName("Adventure Thread");
		thread.start();
		log.info(thread.getName() + " started");
	}

	public enum AdventureStage {
		WAITING,
		LFG,
		ANSWERING,
		READY,
		PROCEEDING
	}
}
