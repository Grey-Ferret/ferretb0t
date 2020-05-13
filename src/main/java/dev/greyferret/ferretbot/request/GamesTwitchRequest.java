package dev.greyferret.ferretbot.request;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.entity.json.twitch.games.TwitchGames;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.util.Map;

@Log4j2
public class GamesTwitchRequest extends BaseTwitchRequest<TwitchGames> {
    public GamesTwitchRequest(Map<String, String> params, Map<String, String> headers, String clientId) {
        super(params, headers, clientId);
    }

    @Override
    public TwitchGames doRequest(String twitchToken) throws HttpStatusException {
        String body = doHttpRequest(twitchToken);
        return new Gson().fromJson(body, TwitchGames.class);
    }

    @Override
    protected String getRequestUrl() {
        return "https://api.twitch.tv/helix/games";
    }

    @Override
    protected Connection.Method getMethod() {
        return Connection.Method.GET;
    }
}
