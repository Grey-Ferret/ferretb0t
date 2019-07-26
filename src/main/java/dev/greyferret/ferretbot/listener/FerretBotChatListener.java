package dev.greyferret.ferretbot.listener;

import dev.greyferret.ferretbot.client.FerretChatClient;
import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.SpringConfig;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.logic.ChatLogic;
import dev.greyferret.ferretbot.processor.*;
import dev.greyferret.ferretbot.service.ViewerService;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import dev.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import net.engio.mbassy.listener.Handler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.time.ZonedDateTime;

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
	private AdventureProcessor adventureProcessor;
	@Autowired
	private ApiProcessor apiProcessor;

	private FerretChatClient ferretChatClient;
	private RaffleProcessor raffleProcessor;
	private StreamElementsAPIProcessor streamElementsAPIProcessor;
	private MTGACardFinderProcessor mtgaCardFinderProcessor;

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
		mtgaCardFinderProcessor = context.getBean(MTGACardFinderProcessor.class);
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
				String vip = eventWrapper.getTag("vip");
				if (vip.equalsIgnoreCase("1"))
					viewerService.setVip(viewer, true);
				else
					viewerService.setVip(viewer, false);
				boolean toUpdateVisual = false;
				if (viewer.getUpdatedVisual() != null) {
					ZonedDateTime zdt = viewer.getUpdatedVisual().plusHours(Viewer.hoursToUpdateVisual);
					if (zdt.isBefore(ZonedDateTime.now(SpringConfig.getZoneId()))) {
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
				ZonedDateTime ageDate = apiProcessor.checkForFreshAcc(viewer.getLogin());
				viewer.setAge(ageDate);
				logger.info("Update incoming for account age for Viewer " + viewer.getLoginVisual());
				ZonedDateTime zdt = ZonedDateTime.now(SpringConfig.getZoneId()).minusDays(2);
				if (ageDate.isAfter(zdt)) {
//					ferretChatClient.sendMessage("/timeout " + viewer.getLogin() + " 120");
//					ferretChatClient.sendMessage("/me Была замечена подозрительная активность от зрителя с ником " + login);
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
		} else if (eventWrapper.getMessage().toLowerCase().length() == 1) {
			adventureProcessor.setAdventurerResponse(eventWrapper, eventWrapper.getMessage().toLowerCase());
		}

		if (botConfig.getMtgaCardsOn()) {
			String mtgText = eventWrapper.getMessage();
			if (mtgText.indexOf("[[") > -1 && mtgText.indexOf("]]") > -1 && mtgText.indexOf("]]") > mtgText.indexOf("[[")) {
				String text = eventWrapper.getMessage().substring(eventWrapper.getMessage().indexOf("[[") + 2, eventWrapper.getMessage().indexOf("]]"));
				mtgaCardFinderProcessor.findCard(text, eventWrapper);
			}
		}

		if (botConfig.getBitsOn()) {
			String bits = eventWrapper.getTag("bits");
			if (StringUtils.isNotBlank(bits)) {
				Long points = NumberUtils.toLong(bits, 0);
				if (points != 0) {
					streamElementsAPIProcessor.updatePoints(eventWrapper.getTag("display-name"), points);
				} else {
					logger.error("points == null/0");
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
					logger.info("Sub Alert triggered for " + msgId);
					logger.info(wrapper);
					chatLogic.proceedSubAlert(wrapper);
				} else if (msgId.equalsIgnoreCase("subgift")) {
					logger.info("Sub Gift Alert triggered for " + msgId);
					logger.info(wrapper);
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