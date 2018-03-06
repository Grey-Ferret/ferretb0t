package net.greyferret.ferretb0t.listener;

import net.engio.mbassy.listener.Handler;
import net.greyferret.ferretb0t.config.ChatConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.defaults.DefaultEventListener;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.channel.UnexpectedChannelLeaveViaPartEvent;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.kitteh.irc.client.library.feature.filter.CommandFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomizedDefaultEventListener extends DefaultEventListener {
	private static final Logger logger = LogManager.getLogger();

	private final Client.WithManagement client;

	@Autowired
	private ChatConfig chatConfig;

	/**
	 * Constructs the listener.
	 *
	 * @param client client
	 */
	public CustomizedDefaultEventListener(Client.WithManagement client) {
		super(client);
		this.client = client;
	}

	@Override
	@CommandFilter("MODE")
	@Handler(priority = Integer.MAX_VALUE - 2)
	public void mode(ClientReceiveCommandEvent event) {

	}

	@Override
	@CommandFilter("PART")
	@Handler(priority = Integer.MAX_VALUE - 2)
	public void part(ClientReceiveCommandEvent event) {
		try {
			if (event.getActor() instanceof User) {
				User user = (User) event.getActor();
//                String userNick = user.getNick();
//                logger.info(userNick + " leaved!");
				String channelName = chatConfig.getChannelWithHashTag();

				//Old functions:
				Optional<Channel> channel = this.client.getChannel(channelName);
				if (!channelName.equalsIgnoreCase(event.getParameters().get(0))) {
					logger.warn("PART message was thrown for channel " + event.getParameters().get(0) + ", instead of " + channelName);
				}
				if (channel.isPresent()) {
					if (event.getActor() instanceof User) {
						boolean isSelf = user.getNick().equals(this.client.getNick());
//                        String partReason = (event.getParameters().size() > 1) ? event.getParameters().get(1) : "";
						String partReason = ""; //no part Reason for Twitch
						ChannelPartEvent partEvent;
						if (isSelf && this.client.getIntendedChannels().contains(channel.get().getName())) {
							partEvent = new UnexpectedChannelLeaveViaPartEvent(this.client, event.getOriginalMessages(), channel.get(), user, partReason);
						} else {
							partEvent = new ChannelPartEvent(this.client, event.getOriginalMessages(), channel.get(), user, partReason);
						}
						this.fire(partEvent);
						this.getTracker().trackUserPart(channel.get().getName(), user.getNick());
						if (isSelf) {
							this.getTracker().unTrackChannel(channel.get().getName());
						}
					} else {
						this.trackException(event, "PART message sent for non-user");
					}
				} else {
					logger.warn("No channel was found for PART message.");
					client.getActorTracker().trackChannel(channelName);
					if (client.getChannel(channelName).isPresent())
						logger.info("FIXED IT YEAH");
				}
			} else
				throw new Exception("Actor with leave message wasn't user " + event.getActor());
		} catch (Exception e) {
			logger.error("Error while building leave message", e);
		}
	}
}
