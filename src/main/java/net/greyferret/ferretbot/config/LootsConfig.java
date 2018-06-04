package net.greyferret.ferretbot.config;

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
	@NotEmpty
	private String tokenChroma;
	@NotEmpty
	private String token;
	@NotEmpty
	private String key;
	@NotNull
	private Long pointsForLoots;
	@NotNull
	private Timer timer;
	@NotNull
	private SubPlan subPlan;

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

	public String getTokenChroma() {
		return tokenChroma;
	}

	public void setTokenChroma(String tokenChroma) {
		this.tokenChroma = tokenChroma;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public SubPlan getSubPlan() {
		return subPlan;
	}

	public void setSubPlan(SubPlan subPlan) {
		this.subPlan = subPlan;
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

	public static class SubPlan {
		@NotNull
		private Long prime;
		@NotNull
		private Long five;
		@NotNull
		private Long ten;
		@NotNull
		private Long twentyFive;

		public Long getPrime() {
			return prime;
		}

		public void setPrime(Long prime) {
			this.prime = prime;
		}

		public Long getFive() {
			return five;
		}

		public void setFive(Long five) {
			this.five = five;
		}

		public Long getTen() {
			return ten;
		}

		public void setTen(Long ten) {
			this.ten = ten;
		}

		public Long getTwentyFive() {
			return twentyFive;
		}

		public void setTwentyFive(Long twentyFive) {
			this.twentyFive = twentyFive;
		}
	}
}
