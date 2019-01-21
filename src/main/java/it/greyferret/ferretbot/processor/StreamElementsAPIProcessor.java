package it.greyferret.ferretbot.processor;

import it.greyferret.ferretbot.config.StreamelementsConfig;
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
import java.util.Map;

@Component
@EnableConfigurationProperties({StreamelementsConfig.class})
public class StreamElementsAPIProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(StreamElementsAPIProcessor.class);

	private static final String streamElementsAPIPrefix = "https://api.streamelements.com/kappa/v2/";
	private static String updatePointsUrl;
	private boolean isOn;

	@Autowired
	private StreamelementsConfig streamelementsConfig;

	@PostConstruct
	private void postConstruct() {
		updatePointsUrl = this.streamElementsAPIPrefix + "points/" + streamelementsConfig.getChannelId() + '/';
		isOn = true;
	}

	@Override
	public void run() {
	}

	public boolean updatePoints(String nickname, Long points) {
		if (points != null) {
			return updatePoints(nickname, String.valueOf(points));
		}
		return false;
	}

	public boolean updatePoints(String nickname, String points) {
		logger.info("Trying to update points for " + nickname + " by " + points);
		if (StringUtils.isBlank(nickname) || StringUtils.isBlank(points)) {
			logger.error("Could not update points. Nickname/points was blank: " + nickname + '/' + points);
			return false;
		}
		Connection.Response response;
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Authorization", "Bearer " + streamelementsConfig.getJwtToken());
			final String _updatePointsUrl = updatePointsUrl + nickname + '/' + points;
			response = Jsoup.connect(_updatePointsUrl)
					.method(Connection.Method.PUT)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String statusCode = String.valueOf(response.statusCode());
			if (statusCode.startsWith("4") || statusCode.startsWith("5")) {
				logger.error("Updating pts return error code " + statusCode + " for " + nickname + ' ' + points);
				return false;
			}
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
		logger.info("Successful updated points for " + nickname + " by " + points);
		return true;
	}
}
