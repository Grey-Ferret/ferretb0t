package dev.greyferret.ferretbot.wrapper;

import lombok.extern.log4j.Log4j2;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Log4j2
public class UserNoticeEventWrapper extends ChatEventMessageBase {
	private UserNoticeEvent event;

	public UserNoticeEventWrapper(UserNoticeEvent event, boolean isDebug, ApplicationContext context) {
		this.event = event;
		this.isDebug = isDebug;
		this.context = context;
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

	public String getLoginVisual() {
		return getTag("display-name");
	}

	@Override
	public String toString() {
		return "UserNoticeEventWrapper{" +
				"context=" + context +
				", event=" + event +
				", isDebug=" + isDebug +
				'}';
	}

	@Override
	public void sendMessageWithMention(String text) {
		this.sendMessageWithMention(text, getLoginVisual());
	}

	@Override
	public void sendMessageWithMentionMe(String text) {
		sendMessageWithMentionMe(text, getLoginVisual());
	}
}
