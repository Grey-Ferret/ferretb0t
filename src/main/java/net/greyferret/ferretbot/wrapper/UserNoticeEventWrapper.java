package net.greyferret.ferretbot.wrapper;

import net.greyferret.ferretbot.client.FerretChatClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;

import java.util.Optional;

public class UserNoticeEventWrapper {
	private static final Logger logger = LogManager.getLogger(UserNoticeEventWrapper.class);

	private UserNoticeEvent event;
	private boolean isDebug;
	private FerretChatClient chatClient;

	public UserNoticeEventWrapper(UserNoticeEvent event, boolean isDebug, FerretChatClient chatClient) {
		this.event = event;
		this.isDebug = isDebug;
		this.chatClient = chatClient;
	}

	public String getTag(String tagString) {
		Optional<MessageTag> tag = event.getTag(tagString);
		if (tag.isPresent()) {
			Optional<String> value = tag.get().getValue();
			if (value.isPresent())
				return value.get();
			else
				return "";
		} else {
			return "";
		}
	}

	public void sendMessage(String text) {
		logger.info(text);
		if (!isDebug)
			chatClient.sendMessage(text);
	}
}
