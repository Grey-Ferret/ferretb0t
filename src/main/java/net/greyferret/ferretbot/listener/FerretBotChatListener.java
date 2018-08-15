package net.greyferret.ferretbot.listener;

import net.engio.mbassy.listener.Handler;
import net.greyferret.ferretbot.client.FerretChatClient;
import net.greyferret.ferretbot.config.ApplicationConfig;
import net.greyferret.ferretbot.config.BotConfig;
import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.logic.ChatLogic;
import net.greyferret.ferretbot.processor.RaffleProcessor;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import net.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import net.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.kitteh.irc.client.library.feature.filter.CommandFilter;
import org.kitteh.irc.client.library.feature.twitch.TwitchListener;
import org.kitteh.irc.client.library.feature.twitch.event.GlobalUserStateEvent;
import org.kitteh.irc.client.library.feature.twitch.event.RoomStateEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.HashSet;
import java.util.regex.Pattern;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@EnableConfigurationProperties({BotConfig.class, ApplicationConfig.class})
public class FerretBotChatListener extends TwitchListener {
	private static final Logger logger = LogManager.getLogger(FerretBotChatListener.class);
	private static final Logger chatLogger = LogManager.getLogger("ChatLogger");

	@Autowired
	private ChatLogic chatLogic;
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private BotConfig botConfig;

	private FerretChatClient ferretChatClient;
	private RaffleProcessor raffleProcessor;

	/**
	 * Creates a new TwitchListener and registers all the Twitch tags.
	 *
	 * @param client the client for which it will be registered
	 */
	public FerretBotChatListener(@Nonnull Client client) {
		super(client);
	}

	@PostConstruct
	private void postConstruct() {
		ferretChatClient = context.getBean("FerretChatClient", FerretChatClient.class);
	}

	@CommandFilter("PRIVMSG")
	@Handler
	public void onPrivMsgEvent(ClientReceiveCommandEvent event) {
		ChannelMessageEventWrapper eventWrapper = new ChannelMessageEventWrapper(event, applicationConfig.isDebug(), ferretChatClient);

		if (botConfig.getRaffleOn()) {
			if (raffleProcessor == null) {
				raffleProcessor = context.getBean(RaffleProcessor.class);
			}
			raffleProcessor.newMessage(eventWrapper.getLogin().toLowerCase());
		}

		String login = eventWrapper.getLogin();
		chatLogger.info(login + ": " + eventWrapper.getMessage());

		if (botConfig.getViewersServiceOn()) {
			Viewer viewer = viewerService.getViewerByName(login);
			if (viewer != null) {
				String subscriber = eventWrapper.getTag("subscriber");
				if (subscriber.equalsIgnoreCase("1"))
					viewerService.setSubscriber(viewer, true);
				else
					viewerService.setSubscriber(viewer, false);
			}
		}


		if (eventWrapper.getMessage().startsWith("!")) {
			chatLogic.proceedCommandLogic(eventWrapper);
			String badges = eventWrapper.getTag("badges");
			boolean isBroadcaster = badges.toLowerCase().contains("broadcaster/1".toLowerCase());
			String userType = eventWrapper.getTag("user-type");
			boolean isModerator = StringUtils.isNotBlank(userType) && userType.equalsIgnoreCase("mod");
			if (isBroadcaster || isModerator) {
				chatLogic.proceedModsCommandLogic(eventWrapper);
				if (isBroadcaster || login.equalsIgnoreCase("greyferret")) {
					chatLogic.proceedAdminCommandLogic(eventWrapper);
				}
			}
		}

		if (botConfig.getBitsOn()) {
			String bits = eventWrapper.getTag("bits");
			if (StringUtils.isNotBlank(bits)) {
				Long points = Long.valueOf(bits);
				if (points != null)
					eventWrapper.sendMessage(FerretBotUtils.buildMessageAddPoints(eventWrapper.getTag("display-name"), points));
				else
					logger.error("points == null");
			}
		}

		String message = eventWrapper.getMessage();
		message = message.replaceAll("([^\\p{L}\\s])", "");
		String[] split = message.split(" ");
		HashSet<String> regexManual = new HashSet<>();
		for (String s : split) {
			if (s.equalsIgnoreCase("без") || s.equalsIgnoreCase("по") || s.equalsIgnoreCase("нет") || s.equalsIgnoreCase("где")) {
				regexManual.add("нет");
			}
			if (Pattern.matches("(в.бк).*", s.toLowerCase())) {
				regexManual.add("вебка");
			}
			if (Pattern.matches("(кастом).*", s.toLowerCase())) {
				regexManual.add("кастомка");
			}
			if (s.equalsIgnoreCase("когда") || s.equalsIgnoreCase("где") || s.equalsIgnoreCase("это") || Pattern.matches("(как..)", s.toLowerCase()) || s.equalsIgnoreCase("что") || s.equalsIgnoreCase("шо")) {
				regexManual.add("когда");
			}
			if (Pattern.matches("(парол).*", s.toLowerCase())) {
				regexManual.add("pass");
			}
		}
		if (regexManual.contains("pass")) {
//			eventWrapper.sendMessageWithMention("Пароль скидывается в чат на твиче.");
		}
		if (regexManual.contains("нет") && regexManual.contains("вебка")) {
//			eventWrapper.sendMessageWithMention("Сегодня без вебки, так как просто адская жара и стример открыл все окна, выключил все лампы и сидит в чем мать родила KappaPride");
		}
		int hourOfCustom = 20;
		int minuteOfCustom = 0;
		if (regexManual.contains("кастомка")) {
			Calendar instance = Calendar.getInstance();
			if (instance.get(Calendar.HOUR_OF_DAY) >= hourOfCustom && instance.get(Calendar.MINUTE) >= minuteOfCustom) {
//				eventWrapper.sendMessageWithMention("Кастомка уже начинается/идёт! Если не успел на эту - не беда, пароль на следующую будет в чате! Кастомки идут до 23:00 по Москве, все кроме первой игры будут в дуэтах.");
				eventWrapper.sendMessageWithMention("Сегодня кастомок не будет BibleThump");
			} else if (regexManual.contains("когда")) {
				Calendar customTime = Calendar.getInstance();
				customTime.set(Calendar.HOUR_OF_DAY, hourOfCustom);
				customTime.set(Calendar.MINUTE, minuteOfCustom);
				long seconds = (customTime.getTimeInMillis() - instance.getTimeInMillis()) / 1000;
				int hours = (int) (seconds / 3600);
				int minutes = (int) ((seconds - hours * 3600) / 60);
				String time;
				if (hours == 0) {
					time = minutes + " минут";
				} else {
					time = hours + " час(а) и " + minutes + " минут";
				}
				String additionalZero = "";
				if (customTime.get(Calendar.MINUTE) <= 9) {
					additionalZero = "0";
				}
//				eventWrapper.sendMessageWithMention("Кастомка будет запущена через " + time + ", в " + customTime.get(Calendar.HOUR_OF_DAY) + ":" + additionalZero + customTime.get(Calendar.MINUTE) + " по Москве. Пароль будет в чате! Играем первую соло, остальные дуо.");
				eventWrapper.sendMessageWithMention("Сегодня кастомок не будет BibleThump");
			}
		}
	}

	@Handler
	private void onUserNoticeEvent(UserNoticeEvent event) {
		chatLogger.info("UserNoticeEvent: " + event);
		UserNoticeEventWrapper wrapper = new UserNoticeEventWrapper(event, applicationConfig.isDebug(), ferretChatClient);
		String msgId = wrapper.getTag("msg-id");

		if (botConfig.getSubAlertOn()) {
			if (StringUtils.isNotBlank(msgId)) {
				if (msgId.equalsIgnoreCase("sub") || msgId.equalsIgnoreCase("resub")) {
					chatLogic.proceedSubAlert(wrapper);
				}

				if (msgId.equalsIgnoreCase("subgift")) {
					chatLogic.proceedSubAlert(wrapper, true);
				}
			}
		}
	}

	@Handler
	private void onGlobalUserStateEvent(GlobalUserStateEvent globalUserStateEvent) {
		chatLogger.info("GlobalUserStateEvent: " + globalUserStateEvent);
	}

	@Handler
	private void onRoomStateEvent(RoomStateEvent roomStateEvent) {
		chatLogger.info("RoomStateEvent: " + roomStateEvent);
	}

	@Handler
	private void onUserStateEvent(UserStateEvent userStateEvent) {
		chatLogger.info("UserStateEvent: " + userStateEvent);
	}
}
