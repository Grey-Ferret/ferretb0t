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
import dev.greyferret.ferretbot.exception.NoTwitchAccessTokenAviable;
import dev.greyferret.ferretbot.security.twitch.AccessTokenJson;
import dev.greyferret.ferretbot.service.ViewerService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class ApiProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
    private static final String twitchAPIPrefix = "https://api.twitch.tv/helix/";
    private String channelStatusUrl;
    private String gameInfoUrl;
    private String usersInfoUrl;
    private String followerInfoUrl;
    private boolean isOn;
    private ChannelStatus currentChannelStatus = ChannelStatus.UNDEFINED;
    private String _streamerId = "";
    private String accessToken = "";

    public enum ChannelStatus {ONLINE, OFFLINE, UNDEFINED}

    @Autowired
    private ChatConfig chatConfig;
    @Autowired
    private ViewerService viewerService;

    @PostConstruct
    private void postConstruct() {
        channelStatusUrl = twitchAPIPrefix + "streams?user_login=" + chatConfig.getChannel();
        gameInfoUrl = twitchAPIPrefix + "games?id=";
        usersInfoUrl = twitchAPIPrefix + "users?login=";
        followerInfoUrl = twitchAPIPrefix + "users/follows?from_id=";
        isOn = true;
    }

    @Override
    public void run() {
    }

    public String getTwitchToken() {
        synchronized (accessToken) {
            if (StringUtils.isBlank(accessToken)) {
                accessToken = getTwitchAccessToken();
            }
        }
        return accessToken + "1";
    }

    public String getTwitchAccessToken() {
        String result = "";
        Connection.Response response;
        try {
            Map<String, String> headers = new HashMap<>();
            String tokenUrl = "https://id.twitch.tv/oauth2/token?client_id=" + chatConfig.getClientId()
                    + "&client_secret=" + chatConfig.getClientSecret()
                    + "&grant_type=client_credentials";
            response = Jsoup.connect(tokenUrl)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .headers(headers)
                    .execute();
            String body = response.body();
            if (StringUtils.isBlank(body)) {
                log.error("Could not request Twitch Token, response was blank");
            } else {
                Gson g = new Gson();
                AccessTokenJson json = g.fromJson(body, AccessTokenJson.class);
                return json.getAccessToken();
            }
        } catch (IOException e) {
            log.error("Could not request Channel Status", e);
            throw new NoTwitchAccessTokenAviable(e);
        }
        throw new NoTwitchAccessTokenAviable();
    }

    public String getChannelStatusMessage() {
        String twitchToken = getTwitchToken();
        String result = "";
        Connection.Response response;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + twitchToken);
            response = Jsoup.connect(channelStatusUrl)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .headers(headers)
                    .execute();
            String body = response.body();
            if (StringUtils.isBlank(body)) {
                log.error("Could not request Channel Status, response was blank");
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
                                log.warn("Stream in JSON was not null, had Stream Type, had Game Id, but could not parse games request");
                                result = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
                            } else {
                                result = Messages.ANNOUNCE_MESSAGE_1 + gameInfo.getData().get(0).getName() + Messages.ANNOUNCE_MESSAGE_2 + chatConfig.getChannel();
                            }
                        } else {
                            log.warn("Stream in JSON was not null, had Stream Type, but no Game was found");
                            result = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
                        }
                    }
                    this.currentChannelStatus = ChannelStatus.ONLINE;
                } else {
                    this.currentChannelStatus = ChannelStatus.OFFLINE;
                }
            }
        } catch (IOException e) {
            log.error("Could not request Channel Status", e);
            return result;
        }
        return result;
    }

    private TwitchGames getGameInfo(String gameId) {
		String twitchToken = getTwitchToken();
        TwitchGames twitchGames = null;
        Connection.Response response;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + twitchToken);
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
            log.error(e.toString());
            return twitchGames;
        }
        return twitchGames;
    }

    public String getUserIdByLogin(String login) {
        String twitchToken = getTwitchToken();
        log.info("Getting id for Twitch Login " + login);
        Connection.Response response;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + twitchToken);
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
                    log.warn("There was found more or less than one user by login " + login + ". Result: " + users.getData());
                } else {
                    return users.getData().get(0).getId();
                }
            }
        } catch (Exception ex) {
            log.error("Error while checking for twitch id: " + ex);
            return null;
        }
        return "";
    }

    public boolean isFollower(String login) {
        String followDate = getFollowDate(login);
        return isFollowerByFollowedAtString(followDate);
    }

    public static boolean isFollowerByFollowedAtString(String followedAt) {
        return StringUtils.isNotBlank(followedAt);
    }

    public synchronized String streamerId() {
        if (StringUtils.isBlank(_streamerId)) {
            _streamerId = getUserIdByLogin(chatConfig.getChannel());
        }
        return _streamerId;
    }

    public String getFollowDateByUserId(String userId) {
        String twitchToken = getTwitchToken();
        log.info("Checking is follower for user id " + userId);
        Connection.Response response;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + twitchToken);
            if (StringUtils.isBlank(userId)) {
                log.error("Error while checking for follower  (userId): " + userId);
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
            log.error("Error while checking for follower (userId): " + userId + "Exception: " + ex);
        }
        return "";
    }

    public String getFollowDate(String login) {
        Viewer viewer = viewerService.getViewerByName(login);
        String userId = viewer.getTwitchUserId();
        if (StringUtils.isBlank(userId)) {
            userId = getUserIdByLogin(login);
        }
        if (StringUtils.isBlank(userId)) {
            log.error("Error while checking for follower: " + login);
        }
        return getFollowDateByUserId(userId);
    }

    public boolean getChannelStatus() {
        return this.currentChannelStatus == ChannelStatus.ONLINE;
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
        Thread thread = new Thread(this);
        thread.setName("Api Thread");
        thread.start();
        log.info(thread.getName() + " started");
    }
}
