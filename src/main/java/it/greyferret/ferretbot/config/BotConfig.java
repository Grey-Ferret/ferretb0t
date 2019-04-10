package it.greyferret.ferretbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@Validated
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
	@NotNull
	private Boolean discordOn;
	@NotNull
	private Boolean lootsOn;
	@NotNull
	private Boolean queueOn;
	@NotNull
	private Boolean raffleOn;
	@NotNull
	private Boolean customCommandsOn;
	@NotNull
	private Boolean subAlertOn;
	@NotNull
	private Boolean bitsOn;
	@NotNull
	private Boolean viewersServiceOn;
	@NotNull
	private Boolean viewersPassivePointsOn;
	@NotNull
	private Boolean streamElementsIntegrationOn;
	@NotNull
	private Boolean mtgaCardsOn;

	public Boolean getQueueOn() {
		return queueOn;
	}

	public void setQueueOn(Boolean queueOn) {
		this.queueOn = queueOn;
	}

	public Boolean getRaffleOn() {
		return raffleOn;
	}

	public void setRaffleOn(Boolean raffleOn) {
		this.raffleOn = raffleOn;
	}

	public Boolean getCustomCommandsOn() {
		return customCommandsOn;
	}

	public void setCustomCommandsOn(Boolean customCommandOn) {
		this.customCommandsOn = customCommandOn;
	}

	public Boolean getSubAlertOn() {
		return subAlertOn;
	}

	public void setSubAlertOn(Boolean subAlertOn) {
		this.subAlertOn = subAlertOn;
	}

	public Boolean getBitsOn() {
		return bitsOn;
	}

	public void setBitsOn(Boolean bitsOn) {
		this.bitsOn = bitsOn;
	}

	public Boolean getViewersServiceOn() {
		return viewersServiceOn;
	}

	public void setViewersServiceOn(Boolean viewersServiceOn) {
		this.viewersServiceOn = viewersServiceOn;
	}

	public Boolean getDiscordOn() {
		return discordOn;
	}

	public void setDiscordOn(Boolean discordOn) {
		this.discordOn = discordOn;
	}

	public Boolean getLootsOn() {
		return lootsOn;
	}

	public void setLootsOn(Boolean lootsOn) {
		this.lootsOn = lootsOn;
	}

	public Boolean getViewersPassivePointsOn() {
		return viewersPassivePointsOn;
	}

	public void setViewersPassivePointsOn(Boolean viewersPassivePointsOn) {
		this.viewersPassivePointsOn = viewersPassivePointsOn;
	}

	public Boolean getStreamElementsIntegrationOn() {
		return streamElementsIntegrationOn;
	}

	public void setStreamElementsIntegrationOn(Boolean streamElementsIntegrationOn) {
		this.streamElementsIntegrationOn = streamElementsIntegrationOn;
	}

	public Boolean getMtgaCardsOn() {
		return mtgaCardsOn;
	}

	public void setMtgaCardsOn(Boolean mtgaCardsOn) {
		this.mtgaCardsOn = mtgaCardsOn;
	}
}
