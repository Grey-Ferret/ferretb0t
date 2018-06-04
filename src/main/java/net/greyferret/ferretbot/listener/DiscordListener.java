package net.greyferret.ferretbot.listener;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.greyferret.ferretbot.config.DiscordConfig;
import net.greyferret.ferretbot.util.FerretBotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiscordListener extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(DiscordListener.class);

	@Autowired
	private DiscordConfig discordConfig;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.getAuthor().getId().equalsIgnoreCase(discordConfig.getEscapeLogBotId())) {
			if (event.isFromType(ChannelType.PRIVATE)) {
				logger.info("PRIVATE: " + FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			} else {
				logger.info(FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			}
		}
	}
}
