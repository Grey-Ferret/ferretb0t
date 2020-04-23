package dev.greyferret.ferretbot.request;

import lombok.extern.log4j.Log4j2;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public abstract class BaseTwitchRequest<T> {
    Map<String, String> params;
    Map<String, String> additionalHeaders;
    String twitchToken = "";

    public BaseTwitchRequest(Map<String, String> params, Map<String, String> additionalHeaders) {
        this.params = params;
        this.additionalHeaders = additionalHeaders;
    }

    public void updateTwitchToken(String twitchToken) {
        this.twitchToken = twitchToken;
    }

    public abstract T doRequest(String twitchToken) throws HttpStatusException;

    protected String doHttpRequest(String twitchToken) throws HttpStatusException {
        Connection.Response response = null;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + twitchToken);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headers.put(entry.getKey(), entry.getValue());
        }
        URI uri;
        try {
            uri = new URIBuilder(getRequestUrl()).addParameters(parseToNameValuePair(params)).build();
        } catch (URISyntaxException e) {
            log.error("Error building url for request", e);
            return "";
        }
        try {
            response = Jsoup.connect(uri.toString())
                    .method(getMethod())
                    .ignoreContentType(true)
                    .headers(headers)
                    .execute();
        } catch (HttpStatusException e) {
            throw e;
        } catch (IOException e) {
            log.error("Could not request Channel Status for url {}", uri.toString(), e);
        }
        return response == null ? "" : response.body();
    }

    public List<NameValuePair> parseToNameValuePair(Map<String, String> params) {
        ArrayList<NameValuePair> res = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            res.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return res;
    }
    protected abstract String getRequestUrl();

    protected abstract Connection.Method getMethod();

    @Override
    public String toString() {
        return "iBasicTwitchRequest{" +
                "params=" + params +
                ", twitchToken='" + twitchToken + '\'' +
                ", requestUrl='" + getRequestUrl() + '\'' +
                ", method=" + getMethod() +
                '}';
    }
}
