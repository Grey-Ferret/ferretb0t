package net.greyferret.ferretb0t.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Created by GreyFerret on 15.12.2017.
 */
@Component
@Validated
@ConfigurationProperties(prefix = "discord")
public class DiscordConfig {
	@NotEmpty
	private String token;
	private Long announcementChannel;
	private Long testChannel;
	private Long checkTime;
	private String escapeLogBotId;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getAnnouncementChannel() {
		return announcementChannel;
	}

	public void setAnnouncementChannel(Long announcementChannel) {
		this.announcementChannel = announcementChannel;
	}

	public Long getTestChannel() {
		return testChannel;
	}

	public void setTestChannel(Long testChannel) {
		this.testChannel = testChannel;
	}

	public Long getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Long checkTime) {
		this.checkTime = checkTime;
	}

	public String getEscapeLogBotId() {
		return escapeLogBotId;
	}

	public void setEscapeLogBotId(String escapeLogBotId) {
		this.escapeLogBotId = escapeLogBotId;
	}
}
