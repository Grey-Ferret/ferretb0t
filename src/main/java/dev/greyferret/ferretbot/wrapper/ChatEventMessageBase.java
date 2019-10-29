package dev.greyferret.ferretbot.wrapper;

import dev.greyferret.ferretbot.processor.FerretChatProcessor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

@Log4j2
public abstract class ChatEventMessageBase {
	protected boolean isDebug;
	protected ApplicationContext context;

	public abstract void sendMessageWithMention(String text);

	public abstract void sendMessageWithMentionMe(String text);

	public void sendMessageWithMention(String text, String toWhom) {
		if (StringUtils.isBlank(toWhom)) {
			sendMessage(text);
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

	public void sendMessageWithMentionMe(String text, String toWhom) {
		if (StringUtils.isBlank(toWhom)) {
			sendMessageMe(text);
		} else {
			boolean removeGavGav = true;
			while (removeGavGav) {
				if (toWhom.startsWith("@")) {
					toWhom = toWhom.substring(1);
				} else {
					removeGavGav = false;
				}
			}
			sendMessageMe("@" + toWhom + " " + text);
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
}
