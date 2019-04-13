package dev.greyferret.ferretbot.listener;

import dev.greyferret.ferretbot.processor.ApiProcessor;
import dev.greyferret.ferretbot.processor.RaffleProcessor;
import dev.greyferret.ferretbot.processor.StreamElementsAPIProcessor;
import dev.greyferret.ferretbot.client.FerretChatClient;
import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.logic.ChatLogic;
import dev.greyferret.ferretbot.logic.MTGACardFinder;
import dev.greyferret.ferretbot.service.ViewerService;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import dev.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import net.engio.mbassy.listener.Handler;
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
import java.util.Date;

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
	@Autowired
	private ApiProcessor apiProcessor;

	private FerretChatClient ferretChatClient;
	private RaffleProcessor raffleProcessor;
	private StreamElementsAPIProcessor streamElementsAPIProcessor;

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
		apiProcessor = context.getBean(ApiProcessor.class);
		streamElementsAPIProcessor = context.getBean(StreamElementsAPIProcessor.class);
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
				Calendar cal = Calendar.getInstance();
				boolean toUpdateVisual = false;
				if (viewer.getUpdatedVisual() != null) {
					cal.setTime(viewer.getUpdatedVisual());
					cal.add(Calendar.HOUR, Viewer.hoursToUpdateVisual);
					if (cal.before(Calendar.getInstance())) {
						toUpdateVisual = true;
					}
				} else {
					toUpdateVisual = true;
				}
				if (toUpdateVisual) {
					viewerService.updateVisual(viewer, eventWrapper.getLoginVisual());
				}
			} else {
				viewer = viewerService.createViewer(login);
			}
			if (viewer.getAge() == null) {
				Date ageDate = apiProcessor.checkForFreshAcc(viewer.getLogin());
				viewer.setAge(ageDate);
				logger.info("Update incoming for account age for Viewer " + viewer.getLoginVisual());
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_MONTH, -2);
				if (ageDate.after(c.getTime())) {
					ferretChatClient.sendMessage("/timeout " + viewer.getLogin() + " 120");
					ferretChatClient.sendMessage("/me Была замечена подозрительная активность от зрителя с ником " + login);
				} else {
					viewer.setApproved(true);
					logger.info("Update incoming for approved status for Viewer " + viewer.getLoginVisual());
				}
				viewerService.updateViewer(viewer);
			}
		}

		if (true) {
			boolean antispamCatched = chatLogic.antispamByWords(eventWrapper);
			if (antispamCatched) {
				logger.info("Antispam caught following message: " + eventWrapper.getMessage());
				logger.info("Ban for author: " + eventWrapper.getLoginVisual());
				eventWrapper.sendMessage("/ban " + eventWrapper.getLoginVisual());
			}
		}

		String badges = eventWrapper.getTag("badges");
		boolean isBroadcaster = badges.toLowerCase().contains("broadcaster/1".toLowerCase());
		String userType = eventWrapper.getTag("user-type");
		boolean isModerator = StringUtils.isNotBlank(userType) && userType.equalsIgnoreCase("mod");

		if (eventWrapper.getMessage().startsWith("!")) {
			chatLogic.proceedCommandLogic(eventWrapper);
			if (isBroadcaster || isModerator) {
				chatLogic.proceedModsCommandLogic(eventWrapper);
				if (isBroadcaster || login.equalsIgnoreCase("greyferret")) {
					chatLogic.proceedAdminCommandLogic(eventWrapper);
				}
			}
		}

		if (botConfig.getMtgaCardsOn()) {
			String mtgText = eventWrapper.getMessage();
			if (mtgText.indexOf("[[") > -1 && mtgText.indexOf("]]") > -1 && mtgText.indexOf("]]") > mtgText.indexOf("[[")) {
				String text = eventWrapper.getMessage().substring(eventWrapper.getMessage().indexOf("[[") + 2, eventWrapper.getMessage().indexOf("]]"));
				MTGACardFinder.findCard(text, eventWrapper);
			}
		}

		if (isModerator) {
			if (eventWrapper.getLogin().equalsIgnoreCase("laborantlady")) {
				String message = FerretBotUtils.buildMessage(eventWrapper.getMessage()).toLowerCase();
				if (message.contains("Для получения снаряжения придется отдать мастерской ".toLowerCase())) {
					Long temp = 0L;
					try {
						String _message = StringUtils.substringBetween(message, "мастерской ", " iq.");
						temp = Long.valueOf(_message.trim());
					} catch (Exception ex) {
						logger.warn("Could not extract adventure price. " + message, ex);
					}
					if (temp != 0L) {
						chatLogic.setPointsForAdventure(temp);
					}
				}
			}
		}

		if (botConfig.getBitsOn()) {
			String bits = eventWrapper.getTag("bits");
			if (StringUtils.isNotBlank(bits)) {
				Long points = Long.valueOf(bits);
				if (points != null) {
					streamElementsAPIProcessor.updatePoints(eventWrapper.getTag("display-name"), points);
				} else {
					logger.error("points == null");
				}
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