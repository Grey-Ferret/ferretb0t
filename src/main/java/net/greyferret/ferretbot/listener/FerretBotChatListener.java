package net.greyferret.ferretbot.listener;

import net.engio.mbassy.listener.Handler;
import net.greyferret.ferretbot.client.FerretChatClient;
import net.greyferret.ferretbot.config.ApplicationConfig;
import net.greyferret.ferretbot.config.LootsConfig;
import net.greyferret.ferretbot.logic.ChatLogic;
import net.greyferret.ferretbot.service.CommandService;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import net.greyferret.ferretbot.wrapper.ChannelMessageEventWrapper;
import net.greyferret.ferretbot.wrapper.UserNoticeEventWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.kitteh.irc.client.library.feature.filter.CommandFilter;
import org.kitteh.irc.client.library.feature.twitch.TwitchListener;
import org.kitteh.irc.client.library.feature.twitch.event.GlobalUserStateEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Optional;

@Component
public class FerretBotChatListener extends TwitchListener {
	private static final Logger logger = LogManager.getLogger();
	@Autowired
	private LootsConfig lootsConfig;
	@Autowired
	private CommandService commandService;
	@Autowired
	private ChatLogic chatLogic;
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ApplicationContext context;
	private FerretChatClient ferretChatClient;

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

	@Handler
	private void onGlobalUserStateEvent(GlobalUserStateEvent event) {
		logger.warn("GlobalUserStateEvent " + event);
	}

	@CommandFilter("PRIVMSG")
	@Handler(priority = Integer.MAX_VALUE - 2)
	public void onPrivMsgEvent(ClientReceiveCommandEvent event) {
		ChannelMessageEventWrapper wrapper = new ChannelMessageEventWrapper(event, applicationConfig.isDebug(), ferretChatClient);

		String userType = wrapper.getTag("user-type");
		logger.info(wrapper.getLogin() + ": " + wrapper.getMessage());

		if (wrapper.getMessage().startsWith("!")) {
			proceedCommandLogic(wrapper);
			String badges = wrapper.getTag("badges");
			boolean isBroadcaster = badges.toLowerCase().contains("broadcaster/1".toLowerCase());
			boolean isModerator = StringUtils.isNotBlank(userType) && userType.equalsIgnoreCase("mod");
			if (isBroadcaster || isModerator) {
				proceedModsCommandLogic(wrapper);
			}
		}

		String bits = wrapper.getTag("bits");
		if (StringUtils.isNotBlank(bits)) {
			Long points = Long.valueOf(bits);
			if (points != null)
				wrapper.sendMessage(FerretBotUtils.buildAddPointsMessage(wrapper.getTag("display-name"), points));
			else
				logger.error("Test");
		}
	}

	@Handler
	private void onUserNoticeEvent(UserNoticeEvent event) {
		boolean isSubAlert = false;
		UserNoticeEventWrapper wrapper = new UserNoticeEventWrapper(event, applicationConfig.isDebug(), ferretChatClient);
		String msgId = wrapper.getTag("msg-id");

		if (StringUtils.isNotBlank(msgId)) {
			if (msgId.equalsIgnoreCase("sub") || msgId.equalsIgnoreCase("resub")) {
				isSubAlert = true;
			}
		}
		if (isSubAlert) {
			String subPlan = wrapper.getTag("msg-param-sub-plan");
			if (StringUtils.isNotBlank(subPlan)) {
				Long points = null;
				if (subPlan.equalsIgnoreCase("prime")) {
					points = lootsConfig.getSubPlan().getPrime();
				} else if (subPlan.equalsIgnoreCase("1000")) {
					points = lootsConfig.getSubPlan().getFive();
				} else if (subPlan.equalsIgnoreCase("2000")) {
					points = lootsConfig.getSubPlan().getTen();
				} else if (subPlan.equalsIgnoreCase("3000")) {
					points = lootsConfig.getSubPlan().getTwentyFive();
				}
				String login = wrapper.getTag("login");
				String paymentMessage = FerretBotUtils.buildAddPointsMessage(login, points);
				viewerService.addPoints(login, points);
				wrapper.sendMessage(paymentMessage);
			}
		}
	}

	/***
	 * Logic for chat commands for everyone
	 *
	 * @param event
	 */
	private void proceedCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		if (message.startsWith("!go")) {
			if (message.startsWith("!go remove")) {
				chatLogic.proceedGoRemove(event);
			} else {
				message = message.replaceAll("\\s+", "");
				if (message.equalsIgnoreCase("!go")) {
					chatLogic.proceedGoAdd(event);
				}
			}
		} else {
			String[] split = message.split(" ");
			commandService.proceedTextCommand(split[0], event);
		}
	}

	/***
	 * Logic for chat commands for mods
	 *
	 * @param event
	 */
	private void proceedModsCommandLogic(ChannelMessageEventWrapper event) {
		String message = FerretBotUtils.buildMessage(event.getMessage());
		if (message.startsWith("!aliasoff")) {
			chatLogic.aliasDelete(event, message);
		} else if (message.startsWith("!alias")) {
			chatLogic.alias(event, message);
		}

		if (message.startsWith("!info") || message.startsWith("!test") || message.startsWith("!status")) {
			event.sendMessageWithMention("A marvel of technology - an engine of destruction!");
		}

		if (message.startsWith("!transfer")) {
			String[] split = StringUtils.split(message, ' ');
			if (split.length == 4 && StringUtils.isNumeric(split[3])) {
				logger.info("Points transfer initiated by " + event.getLogin() + ", from " + split[1] + " to " + split[2] + " amount " + split[3]);
				Long sum = Long.parseLong(split[3]);
				event.sendMessage(FerretBotUtils.buildRemovePointsMessage(split[1], sum));
				viewerService.removePoints(split[1], sum);
				event.sendMessage(FerretBotUtils.buildAddPointsMessage(split[2], sum));
				viewerService.addPoints(split[2], sum);
			}
		}

		if (message.startsWith("!repair")) {
			chatLogic.repair(event);
		}

		if (message.startsWith("!go")) {
			if (message.startsWith("!go return")) {
				chatLogic.proceedGoReturn(event);
			}
			if (message.startsWith("!go select")) {
				chatLogic.proceedGoSelect(event);
			}
			if (message.startsWith("!go reset")) {
				chatLogic.resetGoSelect(event);
			}
		}
	}

	@Override
	@CommandFilter("USERSTATE")
	@Handler(priority = Integer.MAX_VALUE - 2)
	public void userState(ClientReceiveCommandEvent event) {
		if (this.ferretChatClient != null && this.ferretChatClient.getEventManager() != null) {
			this.ferretChatClient.getEventManager().callEvent(new UserStateEvent(this.ferretChatClient, event.getOriginalMessages(), this.getChannel(event)));
		}
	}

	@Nonnull
	private Channel getChannel(ClientReceiveCommandEvent event) {
		String channelName = event.getParameters().get(0);
		Optional<Channel> channel = this.ferretChatClient.getChannel(channelName);
		if (!channel.isPresent()) {
			FerretBotUtils.fixClient(this.ferretChatClient, channelName);
		}
		return channel.get();
	}
}
