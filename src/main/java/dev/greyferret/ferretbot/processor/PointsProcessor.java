package dev.greyferret.ferretbot.processor;

import com.google.gson.Gson;
import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.StreamelementsConfig;
import dev.greyferret.ferretbot.entity.json.streamelements.PointsInfo;
import dev.greyferret.ferretbot.util.FerretBotUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableConfigurationProperties({StreamelementsConfig.class, BotConfig.class, ApplicationConfig.class})
@Log4j2
public class PointsProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private StreamelementsConfig streamelementsConfig;
	@Autowired
	private FerretChatProcessor ferretChatProcessor;
	@Autowired
	private BotConfig botConfig;
	@Autowired
	private ApplicationConfig applicationConfig;

	private static String streamElementsAPIPrefix = "https://api.streamelements.com/kappa/v2/";
	private static String updatePointsUrl = "";
	private static String checkPointsUrl = "";
	private boolean isOn = false;

	@PostConstruct
	private void postConstruct() {
		updatePointsUrl = streamElementsAPIPrefix + "points/" + streamelementsConfig.getChannelId() + '/';
		checkPointsUrl = streamElementsAPIPrefix + "points/" + streamelementsConfig.getChannelId() + '/';
		isOn = true;
	}

	public boolean updatePoints(String nick, Long points) {
		if (botConfig.isStreamElementsIntegrationOn() &&
				StringUtils.isNotBlank(streamelementsConfig.getChannelId()) &&
				StringUtils.isNotBlank(streamelementsConfig.getJwtToken())) {
			return _updatePoints(nick, points);
		} else {
			ferretChatProcessor.sendMessage(FerretBotUtils.buildMessageAddPoints(nick, points));
		}
		return false;
	}

	protected boolean _updatePoints(String nickname, Long points) {
		if (!applicationConfig.isDebug()) {
			if (points < 0) {
				try {
					Long pointsCurrent = checkPoints(nickname);
					if (pointsCurrent != null && points != null && pointsCurrent < Math.abs(points)) {
						return false;
					}
				} catch (Exception ex) {
					log.error("Error while getting current points", ex);
				}
			}
			log.info("Trying to update points for " + nickname + " by " + points);
			if (StringUtils.isBlank(nickname) || points == null) {
				log.error("Could not update points. Nickname/points was blank: " + nickname + '/' + points);
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
					log.error("Updating pts return error code " + statusCode + " for " + nickname + ' ' + points);
					return false;
				}
			} catch (IOException e) {
				log.error(e.toString());
				return false;
			}
			log.info("Successful updated points for " + nickname + " by " + points);
			return true;
		}
		return true;
	}

	public Long checkPoints(String nickname) {
		if (!applicationConfig.isDebug()) {
			log.info("Trying to check points for " + nickname);
			if (StringUtils.isBlank(nickname)) {
				log.error("Could not update points. Nickname was blank: " + nickname);
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
					log.error("Checking pts return error code " + statusCode + " for " + nickname);
					return -1L;
				}
				Gson gson = new Gson();
				PointsInfo pointsInfo = gson.fromJson(response.body(), PointsInfo.class);
				return pointsInfo.getPoints();
			} catch (IOException e) {
				log.error(e.toString());
				return -1L;
			}
		}
		return -1L;
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		Thread thread = new Thread(this);
		thread.setName("StreamElements Thread");
		thread.start();
		log.info(thread.getName() + " started");
	}

	@Override
	public void run() {
	}
}
