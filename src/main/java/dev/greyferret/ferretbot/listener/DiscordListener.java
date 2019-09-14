package dev.greyferret.ferretbot.listener;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.processor.DiscordProcessor;
import dev.greyferret.ferretbot.processor.GameVoteProcessor;
import dev.greyferret.ferretbot.processor.SubVoteProcessor;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class DiscordListener extends ListenerAdapter {
	@Autowired
	private DiscordProcessor discordProcessor;
	@Autowired
	private BotConfig botConfig;
	@Autowired
	private SubVoteProcessor subVoteProcessor;
	@Autowired
	private GameVoteProcessor gameVoteProcessor;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.getMessage().getMember().getUser().getName().equalsIgnoreCase("Dyno")) {
			if (event.isFromType(ChannelType.PRIVATE)) {
				log.info("PRIVATE: " + FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			} else {
				log.info(FerretBotUtils.buildDiscordMessageLog(event.getMessage()));
			}
		}

		if (botConfig.isSubVoteOn() && event.getChannel() == discordProcessor.subsChannel) {
			subVoteProcessor.processSubVoteMessage(event);
		}
		if (botConfig.isSubVoteOn() && event.getChannel() == discordProcessor.readVoteChannel) {
			gameVoteProcessor.processGameVoteMessage(event);
		}
	}
}
