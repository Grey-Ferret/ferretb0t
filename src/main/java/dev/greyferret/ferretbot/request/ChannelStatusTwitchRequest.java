package dev.greyferret.ferretbot.request;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.entity.json.twitch.streams.StreamData;
import dev.greyferret.ferretbot.entity.json.twitch.streams.TwitchStreamsJson;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.util.List;
import java.util.Map;

@Log4j2
public class ChannelStatusTwitchRequest extends BaseTwitchRequest<StreamData> {
    public ChannelStatusTwitchRequest(Map<String, String> params, Map<String, String> headers, String clientId) {
        super(params, headers, clientId);
    }

    @Override
    public StreamData doRequest(String twitchToken) throws HttpStatusException {
        String body = doHttpRequest(twitchToken);
        if (StringUtils.isBlank(body)) {
            log.error("Could not request Channel Status, response was blank");
        } else {
            TwitchStreamsJson json = new Gson().fromJson(body, TwitchStreamsJson.class);
            List<StreamData> streamData = json.getData();
            if (streamData == null || streamData.isEmpty()) {
                return null;
            }

            return streamData.get(0);
        }
        return null;
    }

    @Override
    protected String getRequestUrl() {
        return "https://api.twitch.tv/helix/streams";
    }

    @Override
    protected Connection.Method getMethod() {
        return Connection.Method.GET;
    }
}
