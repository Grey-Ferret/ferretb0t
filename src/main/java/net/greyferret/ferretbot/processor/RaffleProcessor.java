package net.greyferret.ferretbot.processor;

import net.greyferret.ferretbot.client.FerretChatClient;
import net.greyferret.ferretbot.entity.Prize;
import net.greyferret.ferretbot.entity.Raffle;
import net.greyferret.ferretbot.entity.RaffleViewer;
import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.service.PrizePoolService;
import net.greyferret.ferretbot.service.RaffleService;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class RaffleProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(ViewersProcessor.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private RaffleService raffleService;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private PrizePoolService prizePoolService;
	@Autowired
	private ApiProcessor apiProcessor;

	private boolean isOn;
	private HashMap<String, RaffleViewer> viewers;
	private FerretChatClient ferretChatClient;
	private DiscordProcessor discordProcessor;

	@PostConstruct
	private void postConstruct() {
		viewers = new HashMap<>();
		isOn = true;

		apiProcessor = context.getBean(ApiProcessor.class);
	}

	@Override
	public void run() {
		ferretChatClient = context.getBean("FerretChatClient", FerretChatClient.class);
		discordProcessor = context.getBean(DiscordProcessor.class);
		while (isOn) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			if (apiProcessor.getChannelStatus()) {
				Raffle lastTodayRaffle = raffleService.getLastToday();
				if (lastTodayRaffle == null) {
					rollRaffle();
				} else {
					Calendar lastTodayCal = Calendar.getInstance();
					Calendar cal = Calendar.getInstance();
					lastTodayCal.setTime(lastTodayRaffle.getDate());
					lastTodayCal.add(Calendar.MINUTE, 30);

					if (cal.after(lastTodayCal)) {
						rollRaffle();
					}
				}
			}
		}
	}

	private void rollRaffle() {
		HashSet<Viewer> raffleViewers = new HashSet<>();
		synchronized (viewers) {
			for (RaffleViewer viewer : viewers.values()) {
				if (viewer.ifSuitable()) {
					Viewer viewerByName = viewerService.getViewerByName(viewer.getLogin());
					if (viewerByName != null && viewerByName.isSuitableForRaffle()) {
						raffleViewers.add(viewerByName);
					}
				}
			}

			final int subLuckModifier = 2;
			ArrayList<Viewer> rollList = FerretBotUtils.combineViewerListWithSubluck(raffleViewers, subLuckModifier);
			Collections.shuffle(rollList);
			boolean isChannelOnline = apiProcessor.getChannelStatus();
			if (isChannelOnline && rollList.size() > 0) {
				Viewer viewer = rollList.get(0);
				Prize prize = rollPresent(viewer);
				Raffle raffle = new Raffle(prize, viewer);

				raffleService.put(raffle);
			}
		}
	}

	private Prize rollPresent(Viewer viewer) {
		Prize prize = prizePoolService.rollPrize();
		String message;
		if (prize == null) {
			Random rand = new Random();
			int resPts = 50;
			final int chance = 66;
			if (rand.nextInt(100) > chance) {
				resPts = 100;
				prize = new Prize(resPts + " поинтов", 0, 100 - chance);
			} else {
				prize = new Prize(resPts + " поинтов", 0, chance);
			}
			message = " Зритель " + viewer.getLogin() + " выиграл " + resPts + " поинтов! Поздравляем! ";
		} else {
			message = " Зритель " + viewer.getLogin() + " выиграл " + prize.getName() + "! Поздравляем! ";
		}
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.forLanguageTag("ru"));
		ferretChatClient.sendMessage(message);

		String smileCode = "<a:PepePls:452100407779393536>";
		discordProcessor.raffleChannel.sendMessage(smileCode + message + smileCode + dateTimeFormatter.format(ldt)).queue();

		if (message.contains(" поинтов!")) {
			String[] split = StringUtils.split(message, ' ');
			int i = 0;
			int j = 0;
			for (String s : split) {
				if (s.equalsIgnoreCase("поинтов!")) {
					i = j - 1;
					break;
				}
				j++;
			}
			ferretChatClient.sendMessage(FerretBotUtils.buildMessageAddPoints(viewer.getLogin(), Long.valueOf(split[i])));
		}

		resetMessages();
		return prize;
	}

	public void newMessage(String login) {
		synchronized (viewers) {
			login = login.toLowerCase();
			RaffleViewer raffleViewer;
			if (!viewers.keySet().contains(login)) {
				raffleViewer = new RaffleViewer(login);
			} else {
				raffleViewer = viewers.get(login);
				raffleViewer.addMessageTime(Calendar.getInstance());
			}
			viewers.put(login, raffleViewer);
		}
	}

	public void resetMessages() {
		synchronized (viewers) {
			viewers = new HashMap<>();
		}
	}
}
