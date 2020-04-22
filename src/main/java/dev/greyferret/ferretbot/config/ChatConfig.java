package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by GreyFerret on 15.12.2017.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chat")
public class ChatConfig {
	private String login;
	private String password;
	private String channel;
	private Integer retryMs;
	private String clientId;
	private String clientSecret;
	private Integer usersCheckMins;
	private SubPlan subPlan;

	public String getChannelWithHashTag() {
		return "#" + this.getChannel();
	}

	@Getter
	@Setter
	public static class SubPlan {
		private Long prime;
		private Long five;
		private Long ten;
		private Long twentyFive;
	}
}
