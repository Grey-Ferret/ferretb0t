package dev.greyferret.ferretbot.listener;

import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.logic.ChatLogic;
import dev.greyferret.ferretbot.processor.*;
import dev.greyferret.ferretbot.request.FollowDateByUserIdTwitchRequest;
import dev.greyferret.ferretbot.request.UserIdByLoginTwitchRequest;
import dev.greyferret.ferretbot.service.ViewerService;
import dev.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import dev.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.kitteh.irc.client.library.feature.filter.CommandFilter;
import org.kitteh.irc.client.library.feature.twitch.TwitchListener;
import org.kitteh.irc.client.library.feature.twitch.event.GlobalUserStateEvent;
import org.kitteh.irc.client.library.feature.twitch.event.RoomStateEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Log4j2
@Lazy
public class FerretBotChatListener extends TwitchListener {
	@Value("${main.zone-id}")
	private String zoneId;
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
	@Autowired
	private RaffleProcessor raffleProcessor;
	@Autowired
	private PointsProcessor pointsProcessor;
	@Autowired
	private MTGACardFinderProcessor mtgaCardFinderProcessor;
	@Autowired
	private ChatConfig chatConfig;

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
    }

    @CommandFilter("PRIVMSG")
    @Handler
    public void onPrivMsgEvent(ClientReceiveCommandEvent event) {
        ChannelMessageEventWrapper eventWrapper = new ChannelMessageEventWrapper(event, applicationConfig.isDebug(), context);

        if (botConfig.isRaffleOn()) {
            if (raffleProcessor == null) {
                raffleProcessor = context.getBean(RaffleProcessor.class);
            }
            raffleProcessor.newMessage(eventWrapper.getLogin().toLowerCase());
        }

        String login = eventWrapper.getLogin();
//		chatlog.info(login + ": " + eventWrapper.getMessage());

        Viewer viewer = viewerService.getViewerByName(login);
        if (viewer != null) {
	        viewerService.setSubscriber(viewer, eventWrapper.hasBadge("subscriber") || eventWrapper.hasBadge("founder"));
	        viewerService.setVip(viewer, eventWrapper.hasBadge("vip"));
            boolean toUpdateMeta = false;
            if (viewer.getUpdatedVisual(applicationConfig.getZoneId()) != null) {
                ZonedDateTime zdt = viewer.getUpdatedVisual(applicationConfig.getZoneId()).plusHours(Viewer.hoursToUpdateVisual);
                if (zdt.isBefore(ZonedDateTime.now(ZoneId.of(zoneId)))) {
                    toUpdateMeta = true;
                }
            } else {
                toUpdateMeta = true;
            }
            if (toUpdateMeta) {
                viewerService.updateVisual(viewer, eventWrapper.getLoginVisual());
                HashMap<String, String> params = new HashMap<>();
                params.put("login", viewer.getLogin());
                String userId = apiProcessor.proceedTwitchRequest(new UserIdByLoginTwitchRequest(params, new HashMap(), chatConfig.getClientId()));
                viewer.setTwitchUserId(userId);
                params = new HashMap<>();
                params.put("from_id", userId);
                params.put("to_id", apiProcessor.getStreamerId());
                String followDate = apiProcessor.proceedTwitchRequest(new FollowDateByUserIdTwitchRequest(params, new HashMap(), chatConfig.getClientId()));
                boolean isFollower = !StringUtils.isBlank(followDate);
                viewerService.updateFollowerStatus(viewer, followDate, isFollower);
            }
        } else {
            viewer = viewerService.createViewer(login);
        }

        if (true) {
            boolean antispamCatched = chatLogic.antispamByWords(eventWrapper);
            if (antispamCatched) {
                log.info("Antispam caught following message: " + eventWrapper.getMessage());
                log.info("Ban for author: " + eventWrapper.getLoginVisual());
                eventWrapper.sendMessage("/ban " + eventWrapper.getLoginVisual());
            }
        }

        boolean isBroadcaster = eventWrapper.hasBadge("broadcaster");
        boolean isModerator = eventWrapper.hasBadge("moderator");

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

        if (botConfig.isMtgaCardsOn()) {
            String mtgText = eventWrapper.getMessage();
            if (mtgText.indexOf("[[") > -1 && mtgText.indexOf("]]") > -1 && mtgText.indexOf("]]") > mtgText.indexOf("[[")) {
                String text = eventWrapper.getMessage().substring(eventWrapper.getMessage().indexOf("[[") + 2, eventWrapper.getMessage().indexOf("]]"));
                mtgaCardFinderProcessor.findCard(text, eventWrapper);
            }
        }

        if (botConfig.isBitsOn()) {
            String bits = eventWrapper.getTag("bits");
            if (StringUtils.isNotBlank(bits)) {
                Long points = NumberUtils.toLong(bits, 0);
                if (points != 0) {
                    pointsProcessor.updatePoints(eventWrapper.getLogin(), points);
                } else {
                    log.error("points == null/0");
                }
            }
        }

    }

    @Handler
    private void onUserNoticeEvent(UserNoticeEvent event) {
//		chatlog.info("UserNoticeEvent: " + event);
        UserNoticeEventWrapper wrapper = new UserNoticeEventWrapper(event, applicationConfig.isDebug(), context);
        String msgId = wrapper.getTag("msg-id");

        if (botConfig.isSubAlertOn()) {
            if (StringUtils.isNotBlank(msgId)) {
                if (msgId.equalsIgnoreCase("sub") || msgId.equalsIgnoreCase("resub")) {
                    log.info("Sub Alert triggered for " + msgId);
                    log.info(wrapper.toString());
                    chatLogic.proceedSubAlert(wrapper);
                } else if (msgId.equalsIgnoreCase("subgift")) {
                    log.info("Sub Gift Alert triggered for " + msgId);
                    log.info(wrapper.toString());
                    chatLogic.proceedSubAlert(wrapper, true);
                }
            }
        }
    }

    @Handler
    private void onGlobalUserStateEvent(GlobalUserStateEvent globalUserStateEvent) {
//		chatlog.info("GlobalUserStateEvent: " + globalUserStateEvent);
    }

    @Handler
    private void onRoomStateEvent(RoomStateEvent roomStateEvent) {
//		chatlog.info("RoomStateEvent: " + roomStateEvent);
    }

    @Handler
    private void onUserStateEvent(UserStateEvent userStateEvent) {
//		chatlog.info("UserStateEvent: " + userStateEvent);
    }
}