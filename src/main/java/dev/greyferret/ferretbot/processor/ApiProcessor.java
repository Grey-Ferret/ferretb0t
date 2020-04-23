package dev.greyferret.ferretbot.processor;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.entity.Viewer;
import dev.greyferret.ferretbot.entity.json.twitch.streams.StreamData;
import dev.greyferret.ferretbot.exception.NoTwitchAccessTokenAviable;
import dev.greyferret.ferretbot.request.BaseTwitchRequest;
import dev.greyferret.ferretbot.request.ChannelStatusTwitchRequest;
import dev.greyferret.ferretbot.request.FollowDateByUserIdTwitchRequest;
import dev.greyferret.ferretbot.request.UserIdByLoginTwitchRequest;
import dev.greyferret.ferretbot.security.twitch.AccessTokenJson;
import dev.greyferret.ferretbot.service.ViewerService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class ApiProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
    private ChannelStatus currentChannelStatus = ChannelStatus.UNDEFINED;
    private String _streamerId = "";
    private String accessToken = "";
    private final Object ACCESS_TOKEN_OBJ_LOCK = new Object();

    public enum ChannelStatus {ONLINE, OFFLINE, UNDEFINED}

    @Autowired
    private ChatConfig chatConfig;
    @Autowired
    private ViewerService viewerService;

    @Override
    public void run() {
    }

    public <T> T proceedTwitchRequest(BaseTwitchRequest<T> twitchRequest) {
        boolean completed = false;
        int attempt = 0;

        T res = null;
        while (!completed && attempt < 5) {
            try {
                res = twitchRequest.doRequest(getTwitchToken());
                completed = true;
            } catch (HttpStatusException e) {
                if (e.getStatusCode() == 401) {
                    twitchRequest.updateTwitchToken(getTwitchToken(true));
                } else {
                    log.error("Error while doing Twitch Request {}", twitchRequest.getClass(), e);
                }
                attempt++;
            }
        }
        return res;
    }

    private String getTwitchToken() {
        return getTwitchToken(false);
    }

    private String getTwitchToken(boolean getNewToken) {
        synchronized (ACCESS_TOKEN_OBJ_LOCK) {
            if (getNewToken || StringUtils.isBlank(accessToken)) {
                accessToken = requestTwitchAccessToken();
            }
            return accessToken;
        }
    }

    private String requestTwitchAccessToken() {
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

    public boolean isFollower(String login) {
        String followDate = getFollowDate(login);
        return isFollowerByFollowedAtString(followDate);
    }

    public static boolean isFollowerByFollowedAtString(String followedAt) {
        return StringUtils.isNotBlank(followedAt);
    }

    public synchronized String streamerId() {
        if (StringUtils.isBlank(_streamerId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("login", chatConfig.getChannel());
            _streamerId = proceedTwitchRequest(new UserIdByLoginTwitchRequest(params, new HashMap()));
        }
        return _streamerId;
    }

    public String getFollowDate(String login) {
        Viewer viewer = viewerService.getViewerByName(login);
        String userId = viewer.getTwitchUserId();
        if (StringUtils.isBlank(userId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("login", login);
            userId = proceedTwitchRequest(new UserIdByLoginTwitchRequest(params, new HashMap()));
        }
        if (StringUtils.isBlank(userId)) {
            log.error("Error while checking for follower: " + login);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("from_id", userId);
        params.put("to_id", streamerId());
        return proceedTwitchRequest(new FollowDateByUserIdTwitchRequest(params, new HashMap()));
    }

    public boolean getChannelStatus() {
        return this.currentChannelStatus == ChannelStatus.ONLINE;
    }

    public StreamData getStreamData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_login", chatConfig.getChannel());
        StreamData streamData = proceedTwitchRequest(new ChannelStatusTwitchRequest(params, new HashMap<>()));
        ApiProcessor.ChannelStatus newChannelStatus = ApiProcessor.ChannelStatus.OFFLINE;
        if (streamData != null && streamData.getType().equalsIgnoreCase("live")) {
            newChannelStatus = ApiProcessor.ChannelStatus.ONLINE;
        }
        this.currentChannelStatus = newChannelStatus;

        return streamData;
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
        Thread thread = new Thread(this);
        thread.setName("Api Thread");
        thread.start();
        log.info(thread.getName() + " started");
    }
}
