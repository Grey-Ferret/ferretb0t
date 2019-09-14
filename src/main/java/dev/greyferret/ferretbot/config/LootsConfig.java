package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by GreyFerret on 15.12.2017.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "loots")
public class LootsConfig {
	private String login;
	private String password;
	private Long pointsForLoots;
	private Timer timer;

	@Getter
	@Setter
	public static class Timer {
		private Long additionalRetryMs;
		private Long defaultRetryMs;
		private Long maxRetryMs;
	}
}
