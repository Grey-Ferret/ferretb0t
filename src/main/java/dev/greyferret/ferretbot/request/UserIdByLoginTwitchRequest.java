package dev.greyferret.ferretbot.request;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.entity.json.twitch.users.Users;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.util.Map;

@Log4j2
public class UserIdByLoginTwitchRequest extends BaseTwitchRequest<String> {
    public UserIdByLoginTwitchRequest(Map<String, String> params, Map<String, String> headers, String clientId) {
        super(params, headers, clientId);
    }

    @Override
    public String doRequest(String twitchToken) throws HttpStatusException {
        String body = doHttpRequest(twitchToken);
        Users users = new Gson().fromJson(body, Users.class);
        if (users == null || users.getData() == null) {
            return "";
        } else {
            if (users.getData().size() == 1) {
                return users.getData().get(0).getId();
            }
        }
        return "";
    }

    @Override
    protected String getRequestUrl() {
        return "https://api.twitch.tv/helix/users";
    }

    @Override
    protected Connection.Method getMethod() {
        return Connection.Method.GET;
    }
}
