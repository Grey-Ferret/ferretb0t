package net.greyferret.ferretbot.wrapper;

import net.greyferret.ferretbot.client.FerretChatClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.defaults.element.DefaultUser;
import org.kitteh.irc.client.library.element.Actor;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.element.ServerMessage;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.kitteh.irc.client.library.feature.MessageTagManager;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class ChannelMessageEventWrapper {
	private static final Logger logger = LogManager.getLogger();

	private ClientReceiveCommandEvent event;
	private boolean isDebug;
	private FerretChatClient chatClient;

	public ChannelMessageEventWrapper(ClientReceiveCommandEvent event, boolean isDebug, FerretChatClient chatClient) {
		this.isDebug = isDebug;
		this.event = event;
		this.chatClient = chatClient;
	}

	public String getLogin() {
		Actor actor = event.getActor();
		String res = actor.getName();
		try {
			DefaultUser defaultUser = (DefaultUser) actor;
			res = defaultUser.getUserString();
		} catch (Exception ex) {
			logger.error("Can't cast Actor to DefaultUser " + actor);
		}
		return res;
	}

	public Calendar getRegistrationDate() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(event.getActor().getCreationTime());
		return c;
	}

	public void sendMessageWithMention(String text) {
		sendMessage("@" + getLogin() + " " + text);
	}

	public void sendMessageWithMention(String text, String toWhom) {
		if (StringUtils.isBlank(toWhom))
			sendMessageWithMention(text);
		else
			sendMessage("@" + toWhom + " " + text);
	}

	public void sendMessage(String text) {
		logger.info(text);
		if (!isDebug)
			chatClient.sendMessage(text);
	}

	public String getTag(String tag) {
		List<ServerMessage> originalMessages = event.getOriginalMessages();
		ServerMessage message = originalMessages.get(0);
		Optional<MessageTag> messageTagOptional = message.getTag(tag);
		if (messageTagOptional.isPresent()) {
			MessageTagManager.DefaultMessageTag messageTag = (MessageTagManager.DefaultMessageTag) messageTagOptional.get();
			Optional<String> value = messageTag.getValue();
			if (value.isPresent())
				return value.get();
			else
				return "";
		}
		return "";
	}

	public String getMessage() {
		if (event.getParameters() != null && event.getParameters().size() > 1)
			return event.getParameters().get(1);
		return "";
	}
}
