package dev.greyferret.ferretbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by GreyFerret on 15.12.2017.
 */
@ConfigurationProperties(prefix = "discord")
@Getter
@Setter
public class DiscordConfig {
	private String token;
	private Long announcementChannel;
	private Long testChannel;
	private List<Long> gameVoteAddChannels;
	private List<Long> gameVoteVoteChannels;
	private List<Long> gameVoteDisableRoles;
	private Long raffleChannel;
	private Long checkTime;
	private List<Long> subVoteAdminId;
	private Map<Long, Integer> baseVoteMap;
}
