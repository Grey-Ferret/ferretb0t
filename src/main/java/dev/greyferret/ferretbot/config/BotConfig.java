package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
	private boolean discordOn = false;
	private boolean lootsOn = false;
	private boolean queueOn = false;
	private boolean raffleOn = false;
	private boolean customCommandsOn = false;
	private boolean subAlertOn = false;
	private boolean bitsOn = false;
	private boolean viewersPassivePointsOn = false;
	private boolean streamElementsIntegrationOn = false;
	private boolean mtgaCardsOn = false;
	private boolean subVoteOn = false;
	private boolean discordAnnouncementOn = false;
	private boolean interactiveCommandsOn = false;
}
