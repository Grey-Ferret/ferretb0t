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
@ConfigurationProperties(prefix = "chat")
public class ChatConfig {
	@NotEmpty
	private String login;
	@NotEmpty
	private String password;
	@NotEmpty
	private String channel;
	private Integer retryMs;
	private String clientId;
	private Integer usersCheckMs;
	private Integer usersCheckMsFailed;

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

	public Integer getUsersCheckMs() {
		return usersCheckMs;
	}

	public void setUsersCheckMs(Integer usersCheckMs) {
		this.usersCheckMs = usersCheckMs;
	}

	public Integer getUsersCheckMsFailed() {
		return usersCheckMsFailed;
	}

	public void setUsersCheckMsFailed(Integer usersCheckMsFailed) {
		this.usersCheckMsFailed = usersCheckMsFailed;
	}
}
