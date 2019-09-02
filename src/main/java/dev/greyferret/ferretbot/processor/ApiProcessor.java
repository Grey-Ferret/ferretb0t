package dev.greyferret.ferretbot.processor;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.config.Messages;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.entity.json.twitch.games.TwitchGames;
import dev.greyferret.ferretbot.entity.json.twitch.streams.Datum;
import dev.greyferret.ferretbot.entity.json.twitch.streams.TwitchStreamsJson;
import dev.greyferret.ferretbot.entity.json.twitch.users.Users;
import dev.greyferret.ferretbot.entity.json.twitch.users.follows.Follows;
import dev.greyferret.ferretbot.service.ViewerService;
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
public class ApiProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(ApiProcessor.class);

	private static final String twitchAPIPrefix = "https://api.twitch.tv/helix/";
	private String channelStatusUrl;
	private String gameInfoUrl;
	private String usersInfoUrl;
	private String followerInfoUrl;
	private boolean isOn;
	private ChannelStatus currentChannelStatus = ChannelStatus.UNDEFINED;
	private String _streamerId = "";

	public enum ChannelStatus {ONLINE, OFFLINE, UNDEFINED}

	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private ViewerService viewerService;

	@PostConstruct
	private void postConstruct() {
		channelStatusUrl = this.twitchAPIPrefix + "streams?user_login=" + chatConfig.getChannel();
		gameInfoUrl = this.twitchAPIPrefix + "games?id=";
		usersInfoUrl = this.twitchAPIPrefix + "users?login=";
		followerInfoUrl = this.twitchAPIPrefix + "users/follows?from_id=";
		isOn = true;
	}

	@Override
	public void run() {
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
				TwitchStreamsJson json = g.fromJson(body, TwitchStreamsJson.class);
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

	public String getUserIdByLogin(String login) {
		logger.info("Getting id for Twitch Login " + login);
		Connection.Response response;
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Client-ID", chatConfig.getClientId());
			response = Jsoup.connect(usersInfoUrl + login)
					.method(Connection.Method.GET)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String body = response.body();
			Gson gson = new Gson();
			Users users = gson.fromJson(body, Users.class);
			if (users == null || users.getData() == null) {
				return "";
			} else {
				if (users.getData().size() != 1) {
					logger.warn("There was found more or less than one user by login " + login + ". Result: " + users.getData());
				} else {
					return users.getData().get(0).getId();
				}
			}
		} catch (Exception ex) {
			logger.error("Error while checking for twitch id: " + ex);
			return null;
		}
		return "";
	}

	public boolean isFollower(String login) {
		String followDate = getFollowDate(login);
		return isFollowerByFollowedAtString(followDate);
	}

	public static boolean isFollowerByFollowedAtString(String followedAt) {
		if (StringUtils.isNotBlank(followedAt)) {
			return true;
		}
		return false;
	}

	public synchronized String streamerId() {
		if (StringUtils.isBlank(_streamerId)) {
			_streamerId = getUserIdByLogin(chatConfig.getChannel());
		}
		return _streamerId;
	}

	public String getFollowDate(String login) {
		logger.info("Checking is follower for " + login);
		Connection.Response response;
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Client-ID", chatConfig.getClientId());
			Viewer viewer = viewerService.getViewerByName(login);
			String userId = viewer.getTwitchUserId();
			if (StringUtils.isBlank(userId)) {
				userId = getUserIdByLogin(login);
			}
			if (StringUtils.isBlank(userId)) {
				logger.error("Error while checking for follower: " + login);
			}
			response = Jsoup.connect(followerInfoUrl + userId + "&to_id=" + streamerId())
					.method(Connection.Method.GET)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String body = response.body();
			Gson gson = new Gson();
			Follows follows = gson.fromJson(body, Follows.class);
			if (follows == null || follows.getData() == null || follows.getData().size() == 0) {
				return "";
			} else {
				return follows.getData().get(0).getFollowedAt();
			}
		} catch (Exception ex) {
			logger.error("Error while checking for follower: " + login + "Exception: " + ex);
		}
		return "";
	}

	public boolean getChannelStatus() {
		return this.currentChannelStatus == ChannelStatus.ONLINE;
	}
}
