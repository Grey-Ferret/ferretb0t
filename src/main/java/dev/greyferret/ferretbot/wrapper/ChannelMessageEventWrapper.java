package dev.greyferret.ferretbot.wrapper;

import dev.greyferret.ferretbot.listener.FerretBotChatListener;
import dev.greyferret.ferretbot.processor.FerretChatProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.defaults.element.DefaultUser;
import org.kitteh.irc.client.library.element.Actor;
import org.kitteh.irc.client.library.element.MessageTag;
import org.kitteh.irc.client.library.event.client.ClientReceiveCommandEvent;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@Log4j2
public class ChannelMessageEventWrapper {
	private ClientReceiveCommandEvent event;
	private boolean isDebug;
	private ApplicationContext context;

	public ChannelMessageEventWrapper(ClientReceiveCommandEvent event, boolean isDebug, ApplicationContext context) {
		this.isDebug = isDebug;
		this.event = event;
		this.context = context;
	}

	public String getLogin() {
		Actor actor = event.getActor();
		String res = actor.getName();
		try {
			DefaultUser defaultUser = (DefaultUser) actor;
			res = defaultUser.getUserString();
		} catch (Exception ex) {
			log.error("Can't cast Actor to DefaultUser " + actor);
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
		text = RegExUtils.removeAll(text, "\n");
		text = RegExUtils.removeAll(text, "\r");
		text = RegExUtils.removeAll(text, "\0");
		log.info(text);
		if (!isDebug)
			context.getBean(FerretChatProcessor.class).sendMessage(text);
	}

	public void sendMessageMe(String text) {
		text = RegExUtils.removeAll(text, "\n");
		text = RegExUtils.removeAll(text, "\r");
		text = RegExUtils.removeAll(text, "\0");
		log.info(text);
		if (!isDebug)
			context.getBean(FerretChatProcessor.class).sendMessageMe(text);
	}


	public String getTag(String tag) {
		Optional<MessageTag> _tag = this.event.getTag(tag);
		if (!_tag.isPresent()) {
			return "";
		}
		MessageTag messageTag = _tag.get();
		Optional<String> value = messageTag.getValue();
		if (!value.isPresent()) {
			return "";
		}
		return value.get();
	}

	public String getMessage() {
		if (event.getParameters() != null && event.getParameters().size() > 1)
			return event.getParameters().get(1);
		return "";
	}
}
