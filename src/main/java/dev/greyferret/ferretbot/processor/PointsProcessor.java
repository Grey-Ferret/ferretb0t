package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.StreamelementsConfig;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({StreamelementsConfig.class, BotConfig.class})
@Log4j2
public class PointsProcessor extends StreamElementsAPIProcessor {
	@Autowired
	private StreamelementsConfig streamelementsConfig;
	@Autowired
	private FerretChatProcessor ferretChatProcessor;
	@Autowired
	private BotConfig botConfig;

	public boolean updatePoints(String nick, Long points) {
		if (botConfig.isStreamElementsIntegrationOn() &&
				StringUtils.isNotBlank(streamelementsConfig.getChannelId()) &&
				StringUtils.isNotBlank(streamelementsConfig.getJwtToken())) {
			return _updatePoints(nick, points);
		} else {
			ferretChatProcessor.sendMessage(FerretBotUtils.buildMessageAddPoints(nick, points));
		}
		return false;
	}
}
