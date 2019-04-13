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
@ConfigurationProperties(prefix = "chat")
public class ChatConfig {
	@NotEmpty
	private String login;
	@NotEmpty
	private String password;
	@NotEmpty
	private String channel;
	@NotNull
	private Integer retryMs;
	@NotEmpty
	private String clientId;
	@NotNull
	private Integer usersCheckMins;
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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getRetryMs() {
		return retryMs;
	}

	public void setRetryMs(Integer retryMs) {
		this.retryMs = retryMs;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getChannelWithHashTag() {
		return "#" + this.getChannel();
	}

	public Integer getUsersCheckMins() {
		return usersCheckMins;
	}

	public void setUsersCheckMins(Integer usersCheckMins) {
		this.usersCheckMins = usersCheckMins;
	}

	public SubPlan getSubPlan() {
		return subPlan;
	}

	public void setSubPlan(SubPlan subPlan) {
		this.subPlan = subPlan;
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
