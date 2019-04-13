package dev.greyferret.ferretbot.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by GreyFerret on 15.12.2017.
 */
@Component
@Validated
@ConfigurationProperties(prefix = "loots")
public class LootsConfig {
	@NotEmpty
	private String login;
	@NotEmpty
	private String password;
	@NotNull
	private Long pointsForLoots;
	@NotNull
	private Timer timer;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public Long getPointsForLoots() {
		return pointsForLoots;
	}

	public void setPointsForLoots(Long pointsForLoots) {
		this.pointsForLoots = pointsForLoots;
	}

	public static class Timer {
		@NotNull
		private Long additionalRetryMs;
		@NotNull
		private Long defaultRetryMs;
		@NotNull
		private Long maxRetryMs;

		public Long getAdditionalRetryMs() {
			return additionalRetryMs;
		}

		public void setAdditionalRetryMs(Long additionalRetryMs) {
			this.additionalRetryMs = additionalRetryMs;
		}

		public Long getDefaultRetryMs() {
			return defaultRetryMs;
		}

		public void setDefaultRetryMs(Long defaultRetryMs) {
			this.defaultRetryMs = defaultRetryMs;
		}

		public Long getMaxRetryMs() {
			return maxRetryMs;
		}

		public void setMaxRetryMs(Long maxRetryMs) {
			this.maxRetryMs = maxRetryMs;
		}
	}
}
