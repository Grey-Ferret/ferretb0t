package dev.greyferret.ferretbot.processor;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.StreamelementsConfig;
import dev.greyferret.ferretbot.entity.json.streamelements.PointsInfo;
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
	private static String checkPointsUrl;
	private boolean isOn;

	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private StreamelementsConfig streamelementsConfig;

	@PostConstruct
	private void postConstruct() {
		updatePointsUrl = this.streamElementsAPIPrefix + "points/" + streamelementsConfig.getChannelId() + '/';
		checkPointsUrl = this.streamElementsAPIPrefix + "points/" + streamelementsConfig.getChannelId() + '/';
		isOn = true;
	}

	@Override
	public void run() {
	}

	public boolean updatePoints(String nickname, Long points) {
		if (!applicationConfig.isDebug()) {
			if (points < 0) {
				try {
					Long pointsCurrent = checkPoints(nickname);
					if (pointsCurrent != null && points != null && pointsCurrent < points) {
						return false;
					}
				} catch (Exception ex) {
					logger.error("Error while getting current points", ex);
				}
			}
			logger.info("Trying to update points for " + nickname + " by " + points);
			if (StringUtils.isBlank(nickname) || points == null) {
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
		return true;
	}

	public Long checkPoints(String nickname) {
		if (!applicationConfig.isDebug()) {
			logger.info("Trying to check points for " + nickname);
			if (StringUtils.isBlank(nickname)) {
				logger.error("Could not update points. Nickname was blank: " + nickname);
				return -1L;
			}
			Connection.Response response;
			try {
				Map<String, String> headers = new HashMap<>();
				headers.put("Authorization", "Bearer " + streamelementsConfig.getJwtToken());
				final String _updatePointsUrl = checkPointsUrl + nickname + '/';
				response = Jsoup.connect(_updatePointsUrl)
						.method(Connection.Method.GET)
						.ignoreContentType(true)
						.headers(headers)
						.execute();
				String statusCode = String.valueOf(response.statusCode());
				if (statusCode.startsWith("4") || statusCode.startsWith("5")) {
					logger.error("Checking pts return error code " + statusCode + " for " + nickname);
					return -1L;
				}
				Gson gson = new Gson();
				PointsInfo pointsInfo = gson.fromJson(response.body(), PointsInfo.class);
				return pointsInfo.getPoints();
			} catch (IOException e) {
				logger.error(e);
				return -1L;
			}
		}
		return -1L;
	}
}
