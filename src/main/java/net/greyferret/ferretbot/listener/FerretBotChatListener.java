package net.greyferret.ferretbot.listener;

import net.engio.mbassy.listener.Handler;
import net.greyferret.ferretbot.client.FerretChatClient;
import net.greyferret.ferretbot.config.ApplicationConfig;
import net.greyferret.ferretbot.engine.RaffleEngine;
import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.logic.ChatLogic;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
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

	private FerretChatClient ferretChatClient;
	private ConcurrentHashMap<String, Boolean> readyCheckList;
	private RaffleEngine raffleEngine;

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
		raffleEngine = context.getBean(RaffleEngine.class);
		readyCheckList = new ConcurrentHashMap<>();
	}

	@CommandFilter("PRIVMSG")
	@Handler
	public void onPrivMsgEvent(ClientReceiveCommandEvent event) {
		ChannelMessageEventWrapper eventWrapper = new ChannelMessageEventWrapper(event, applicationConfig.isDebug(), ferretChatClient);

		raffleEngine.newMessage(eventWrapper.getLogin().toLowerCase());

		String login = eventWrapper.getLogin();
		chatLogger.info(login + ": " + eventWrapper.getMessage());
		if (readyCheckList.size() > 0) {
			for (String s : readyCheckList.keySet()) {
				if (s.equalsIgnoreCase(login)) {
					readyCheckList.put(s, true);
					break;
				}
			}
		}

		Viewer viewer = viewerService.getViewerByName(login);
		if (viewer != null) {
			String subscriber = eventWrapper.getTag("subscriber");
			if (subscriber.equalsIgnoreCase("1"))
				viewerService.setSubscriber(viewer, true);
			else
				viewerService.setSubscriber(viewer, false);
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

		String bits = eventWrapper.getTag("bits");
		if (StringUtils.isNotBlank(bits)) {
			Long points = Long.valueOf(bits);
			if (points != null)
				eventWrapper.sendMessage(FerretBotUtils.buildMessageAddPoints(eventWrapper.getTag("display-name"), points));
			else
				logger.error("points == null");
		}
	}

	@Handler
	private void onUserNoticeEvent(UserNoticeEvent event) {
		chatLogger.info("UserNoticeEvent: " + event);
		UserNoticeEventWrapper wrapper = new UserNoticeEventWrapper(event, applicationConfig.isDebug(), ferretChatClient);
		String msgId = wrapper.getTag("msg-id");

		if (StringUtils.isNotBlank(msgId)) {
			if (msgId.equalsIgnoreCase("sub") || msgId.equalsIgnoreCase("resub")) {
				chatLogic.proceedSubAlert(wrapper);
			}

			if (msgId.equalsIgnoreCase("subgift")) {
				chatLogic.proceedSubAlert(wrapper, true);
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

	public ConcurrentHashMap<String, Boolean> getReadyCheckList() {
		return readyCheckList;
	}

	public void setReadyCheckList(HashMap<String, Boolean> readyCheckList) {
		this.readyCheckList = new ConcurrentHashMap<>();
		this.readyCheckList.putAll(readyCheckList);
	}
}
