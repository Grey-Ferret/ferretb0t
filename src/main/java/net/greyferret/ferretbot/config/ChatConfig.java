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
}
