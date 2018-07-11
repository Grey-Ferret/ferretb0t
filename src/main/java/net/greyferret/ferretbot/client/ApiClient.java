package net.greyferret.ferretbot.client;

import com.google.gson.Gson;
import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.Messages;
import net.greyferret.ferretbot.entity.json.twitch.games.TwitchGames;
import net.greyferret.ferretbot.entity.json.twitch.streams.Datum;
import net.greyferret.ferretbot.entity.json.twitch.streams.TwitchStreams;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableConfigurationProperties({ChatConfig.class})
public class ApiClient implements Runnable {
	private static final Logger logger = LogManager.getLogger(ApiClient.class);

	private static final String twitchAPIPrefix = "https://api.twitch.tv/helix/";
	private String channelStatusUrl;
	private String gameInfoUrl;
	private boolean isOn;
	private ChannelStatus currentChannelStatus = ChannelStatus.UNDEFINED;

	public enum ChannelStatus {ONLINE, OFFLINE, UNDEFINED}

	@Autowired
	private ChatConfig chatConfig;

	@PostConstruct
	private void postConstruct() {
		channelStatusUrl = this.twitchAPIPrefix + "streams?user_login=" + chatConfig.getChannel();
		gameInfoUrl = this.twitchAPIPrefix + "games?id=";
		isOn = true;
	}

	@Override
	public void run() {
		while (isOn) {

		}
	}

	public String getChannelStatusMessage() {
		String result = "";
		Connection.Response response;
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Client-ID", chatConfig.getClientId());
			response = Jsoup.connect(channelStatusUrl)
					.method(Connection.Method.GET)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String body = response.body();
			if (StringUtils.isBlank(body)) {
				logger.error("Could not request Channel Status, response was blank");
			} else {
				Gson g = new Gson();
				TwitchStreams json = g.fromJson(body, TwitchStreams.class);
				List<Datum> datum = json.getData();
				if (datum == null || datum.size() == 0) {
					this.currentChannelStatus = ChannelStatus.OFFLINE;
					return result;
				}
				Datum twitchInfo = datum.get(0);
				String streamType = twitchInfo.getType();
				if (streamType.equalsIgnoreCase("live")) {
					if (this.currentChannelStatus.equals(ChannelStatus.OFFLINE)) {
						if (StringUtils.isNotBlank(twitchInfo.getGameId())) {
							String gameId = twitchInfo.getGameId();
							TwitchGames gameInfo = getGameInfo(gameId);
							if (gameInfo == null || gameInfo.getData() == null || gameInfo.getData().size() == 0 || StringUtils.isBlank(gameInfo.getData().get(0).getName())) {
								logger.warn("Stream in JSON was not null, had Stream Type, had Game Id, but could not parse games request");
								result = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
							} else {
								result = Messages.ANNOUNCE_MESSAGE_1 + gameInfo.getData().get(0).getName() + Messages.ANNOUNCE_MESSAGE_2 + chatConfig.getChannel();
							}
						} else {
							logger.warn("Stream in JSON was not null, had Stream Type, but no Game was found");
							result = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
						}
					}
					this.currentChannelStatus = ChannelStatus.ONLINE;
				} else {
					this.currentChannelStatus = ChannelStatus.OFFLINE;
				}
			}
		} catch (IOException e) {
			logger.error("Could not request Channel Status", e);
			return result;
		}
		return result;
	}

	private TwitchGames getGameInfo(String gameId) {
		TwitchGames twitchGames = null;
		Connection.Response response;
		Map<String, String> headers = new HashMap<>();
		headers.put("Client-ID", chatConfig.getClientId());
		final String gameInfoUrlComplete = gameInfoUrl + gameId;
		try {
			response = Jsoup.connect(gameInfoUrlComplete)
					.method(Connection.Method.GET)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String body = response.body();
			Gson gson = new Gson();
			twitchGames = gson.fromJson(body, TwitchGames.class);
		} catch (IOException e) {
			logger.error(e);
			return twitchGames;
		}
		return twitchGames;
	}

	public boolean getChannelStatus() {
		return this.currentChannelStatus == ChannelStatus.ONLINE;
	}
}
