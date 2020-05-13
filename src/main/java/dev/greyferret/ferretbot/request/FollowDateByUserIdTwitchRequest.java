package dev.greyferret.ferretbot.request;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.entity.json.twitch.users.follows.Follows;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.util.Map;

@Log4j2
public class FollowDateByUserIdTwitchRequest extends BaseTwitchRequest<String> {
	public FollowDateByUserIdTwitchRequest(Map<String, String> params, Map<String, String> headers, String clientId) {
        super(params, headers, clientId);
    }

    @Override
    public String doRequest(String twitchToken) throws HttpStatusException {
        String body = doHttpRequest(twitchToken);
        Follows follows = new Gson().fromJson(body, Follows.class);
        if (follows == null || follows.getData() == null || follows.getData().isEmpty()) {
            return "";
        } else {
            return follows.getData().get(0).getFollowedAt();
        }
    }

    @Override
    protected String getRequestUrl() {
        return "https://api.twitch.tv/helix/users/follows";
    }

    @Override
    protected Connection.Method getMethod() {
        return Connection.Method.GET;
    }
}
