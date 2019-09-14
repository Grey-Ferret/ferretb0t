package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
	private Boolean discordOn;
	private Boolean lootsOn;
	private Boolean queueOn;
	private Boolean raffleOn;
	private Boolean customCommandsOn;
	private Boolean subAlertOn;
	private Boolean bitsOn;
	private Boolean viewersServiceOn;
	private Boolean viewersPassivePointsOn;
	private Boolean streamElementsIntegrationOn;
	private Boolean mtgaCardsOn;
	private Boolean subVoteOn;
}
