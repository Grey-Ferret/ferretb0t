package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "websocket")
public class WebsocketConfig {
	private String streamerClientId;
	private String streamerClientSecret;
}
