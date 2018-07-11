package net.greyferret.ferretbot.client;

import net.greyferret.ferretbot.entity.Prize;
import net.greyferret.ferretbot.entity.RaffleDate;
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
public class RaffleClient implements Runnable {
	private static final Logger logger = LogManager.getLogger(ViewersClient.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private RaffleService raffleService;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private PrizePoolService prizePoolService;
	@Autowired
	private ApiClient apiClient;

	private boolean isOn;
	private HashMap<String, RaffleViewer> viewers;
	private FerretChatClient ferretChatClient;
	private DiscordClient discordClient;
	private RaffleDate raffleDate;

	@PostConstruct
	private void postConstruct() {
		viewers = new HashMap<>();
		isOn = true;

		apiClient = context.getBean(ApiClient.class);
	}

	@Override
	public void run() {
		ferretChatClient = context.getBean("FerretChatClient", FerretChatClient.class);
		discordClient = context.getBean(DiscordClient.class);
		while (isOn) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			Calendar now = Calendar.getInstance();
			int dateId = now.get(Calendar.DAY_OF_MONTH) + now.get(Calendar.MONTH) * 100 + now.get(Calendar.YEAR) * 10000;
			raffleDate = raffleService.get(dateId);

			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);

			if (hour >= 15 && hour <= 23) {
				int raffleNum = 0;
				if (hour >= 16 && hour <= 22) {
					int add = 1;
					if (minute >= 30) {
						add = 2;
					}
					raffleNum = (hour - 16) * 2 + add;
				} else if (hour == 15 && minute >= 30) {
					raffleNum = 0;
				} else {
					raffleNum = 15;
				}
				HashMap<Integer, Boolean> mapOfRaffles = raffleDate.getMapOfRaffles();
				Boolean raffleDone = mapOfRaffles.get(raffleNum);

				if (!raffleDone) {
					HashSet<Viewer> raffleViewers = new HashSet<>();
					for (RaffleViewer viewer : viewers.values()) {
						if (viewer.ifSuitable()) {
							Viewer viewerByName = viewerService.getViewerByName(viewer.getLogin());
							if (viewerByName != null) {
								raffleViewers.add(viewerByName);
							}
						}
					}

					final int subLuckModifier = 2;
					ArrayList<Viewer> rollList = FerretBotUtils.combineViewerListWithSubluck(raffleViewers, subLuckModifier);
					Collections.shuffle(rollList);
					boolean isChannelOnline = apiClient.getChannelStatus();
					if (isChannelOnline && viewers.size() > 0) {
						Viewer viewer = rollList.get(0);
						rollPresent(viewer);

						mapOfRaffles.put(raffleNum, true);
						raffleDate.setMapOfRaffles(mapOfRaffles);
						raffleService.put(raffleDate);
					}
				}
			}
		}
	}

	private void rollPresent(Viewer viewer) {
		Prize prize = prizePoolService.rollPrize();
		String message;
		if (prize == null) {
			Random rand = new Random();
			int resPts = 50;
			if (rand.nextInt(100) > 66) {
				resPts = 100;
			}
			message = " Зритель " + viewer.getLogin() + " выиграл " + resPts + " поинтов! Поздравляем! ";
		} else {
			message = " Зритель " + viewer.getLogin() + " выиграл " + prize.getName() + "! Поздравляем! ";
		}
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.forLanguageTag("ru"));
		ferretChatClient.sendMessage(message);

		String smileCode = "<a:PepePls:452100407779393536>";
		discordClient.raffleChannel.sendMessage(smileCode + message + smileCode + dateTimeFormatter.format(ldt)).queue();

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
	}

	public void newMessage(String login) {
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

	public void resetMessages() {
		viewers = new HashMap<>();
	}
}
