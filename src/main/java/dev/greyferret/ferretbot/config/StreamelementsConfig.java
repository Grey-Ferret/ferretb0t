package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by GreyFerret on 20.01.2019
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "streamelements")
public class StreamelementsConfig {
	private String jwtToken;
	private String channelId;
}
