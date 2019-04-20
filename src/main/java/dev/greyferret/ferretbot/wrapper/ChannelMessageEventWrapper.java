package dev.greyferret.ferretbot.wrapper;

import dev.greyferret.ferretbot.client.FerretChatClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.defaults.element.DefaultUser;
import org.kitteh.irc.client.library.element.Actor;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.element.ServerMessage;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.kitteh.irc.client.library.feature.MessageTagManager;

import java.util.Optional;

public class ChannelMessageEventWrapper {
	private static final Logger logger = LogManager.getLogger(ChannelMessageEventWrapper.class);

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

	public String getLoginVisual() {
		return getTag("display-name");
	}

	public void sendMessageWithMention(String text) {
		sendMessage("@" + getLoginVisual() + " " + text);
	}

	public void sendMessageWithMentionMe(String text) {
		sendMessageMe("@" + getLoginVisual() + " " + text);
	}

	public void sendMessageWithMention(String text, String toWhom) {
		if (StringUtils.isBlank(toWhom)) {
			sendMessageWithMention(text);
		} else {
			boolean removeGavGav = true;
			while (removeGavGav) {
				if (toWhom.startsWith("@")) {
					toWhom = toWhom.substring(1);
				} else {
					removeGavGav = false;
				}
			}
			sendMessage("@" + toWhom + " " + text);
		}
	}

	public void sendMessage(String text) {
		text = StringUtils.removeAll(text, "\n");
		text = StringUtils.removeAll(text, "\r");
		text = StringUtils.removeAll(text, "\0");
		logger.info(text);
		if (!isDebug)
			chatClient.sendMessage(text);
	}

	public void sendMessageMe(String text) {
		text = StringUtils.removeAll(text, "\n");
		text = StringUtils.removeAll(text, "\r");
		text = StringUtils.removeAll(text, "\0");
		logger.info(text);
		if (!isDebug)
			chatClient.sendMessageMe(text);
	}


	public String getTag(String tag) {
		ServerMessage message = event.getServerMessage();
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
