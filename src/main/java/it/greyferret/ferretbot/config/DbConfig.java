package it.greyferret.ferretbot.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Created by GreyFerret on 15.12.2017.
 */
@Component
@Validated
@ConfigurationProperties(prefix = "db")
public class DbConfig {
	@NotEmpty
	private String url;
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
