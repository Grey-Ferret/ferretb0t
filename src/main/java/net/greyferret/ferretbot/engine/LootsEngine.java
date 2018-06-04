package net.greyferret.ferretbot.engine;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.greyferret.ferretbot.config.LootsConfig;
import net.greyferret.ferretbot.entity.Loots;
import net.greyferret.ferretbot.entity.json.loots.LootsJson;
import net.greyferret.ferretbot.entity.json.loots.Ok;
import net.greyferret.ferretbot.exception.LootsRunningLootsParsingException;
import net.greyferret.ferretbot.service.LootsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Loots bot
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
@Component
public class LootsEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private LootsConfig lootsConfig;
	@Autowired
	private LootsService lootsService;

	private long timeRetryMS;
	private boolean isOn;
	private Map<String, String> cookies;
	private final String loginUrl = "https://loots.com/pub/auth/login";
	private final String lootsUrl = "https://loots.com/api/v1/me/transactions/tips/broadcaster";

	/***
	 * Constructor with all params for Loots
	 */
	public LootsEngine() {
		this.isOn = true;
		this.cookies = new HashMap<>();
	}

	@PostConstruct
	private void postConstruct() {
		this.timeRetryMS = lootsConfig.getTimer().getDefaultRetryMs();
	}

	public void run() {
		if (cookies == null || cookies.size() == 0) {
			logger.info("No cookies found, starting auth...");
			login();
		}

		try {
			mainLoop();
		} catch (InterruptedException e) {
			logger.error("InterruptedException in LootsEngine.java", e);
		}
	}

	/***
	 * Close for LootsBot
	 */
	public synchronized void close() {
		this.isOn = false;
	}

	/***
	 * Main loop for LootsBot to continue checking for loots
	 *
	 * @throws InterruptedException
	 */
	private synchronized void mainLoop() throws InterruptedException {
		while (isOn) {
			Thread.sleep(this.timeRetryMS);
			Connection.Response response = null;
			try {
				Map<String, String> headers = new HashMap<>();
				headers.put("Host", "loots.com");
				headers.put("Connection", "keep-alive");
				headers.put("loots-Nonce", "1");
				headers.put("Content-Type", "application/json");
				headers.put("Accept", "application/json");
				headers.put("loots-Access-Token-Chroma", lootsConfig.getTokenChroma());
				headers.put("loots-Access-Token", lootsConfig.getToken());
				headers.put("loots-Client-Key", lootsConfig.getKey());
				headers.put("Referer", "https://loots.com/en/account/tips/condensed/completed");
				headers.put("Accept-Encoding", "gzip, deflate, br");
				headers.put("Accept-Language", "en-US,en;q=0.9");
				response = Jsoup.connect(lootsUrl)
						.method(Connection.Method.GET)
						.ignoreContentType(true)
						.headers(headers)
						.cookies(cookies)
						.execute();
			} catch (IOException e) {
				logger.error("Could not request page", e);
				increaseRetry();
			}
			if (response != null) {
				if (response.url().toString().contains("/auth/login")) {
					logger.info("Login page found, starting auth...");
					login();
					continue;
				}
				if (StringUtils.isNotBlank(response.body())) {
					Gson g = new Gson();
					LootsJson lootsJson = null;
					try {
						lootsJson = g.fromJson(response.body(), LootsJson.class);
					} catch (Exception e) {
						increaseRetry();
						logger.error("Exception when parsing JSON", e);
					}
					if (lootsJson != null) {
						Set<Loots> loots = parseLootsJson(lootsJson);
						lootsService.checkOutLoots(loots);
						resetRetry();
					} else {
						increaseRetry();
						logger.warn("No Loots found, but without exceptions");
					}
				}
			}
		}
	}

	/***
	 * Method that parse Json
	 *
	 * @param input Special Entity for Loots Json
	 * @return Parsed Loots
	 */
	private Set<Loots> parseLootsJson(LootsJson input) {
		Set<Loots> res = new HashSet<>();
		List<Ok> okLoots = input.getData().getOk();
		try {
			ArrayList<Object> runningLootsUnparsed = (ArrayList<Object>) input.getData().getRunning();
			if (runningLootsUnparsed.size() > 0) {
				LinkedTreeMap<String, Object> runningLoots = (LinkedTreeMap<String, Object>) runningLootsUnparsed.get(0);
				try {
					res.add(new Loots(runningLoots));
				} catch (LootsRunningLootsParsingException e) {
					logger.error("Could not parse Running Loots", e);
				}
			}
		} catch (Exception e) {
			logger.error("Could not parse running Loots", e);
		}
		List<Ok> newOkLoots = new ArrayList<>();
		for (Ok ok : okLoots) {
			if (!ok.getType().equalsIgnoreCase("tip_auto")) {
				newOkLoots.add(ok);
			}
		}
		okLoots = newOkLoots;
		Set<Loots> lootsList = okLoots.stream().map(Loots::new).collect(Collectors.toSet());
		if (lootsList != null && lootsList.size() > 0) {
			res.addAll(lootsList);
			return res;
		}
		return null;
	}

	/***
	 * Reset retry time after successful retrieving of loots
	 */
	private void resetRetry() {
		this.timeRetryMS = this.lootsConfig.getTimer().getDefaultRetryMs();
	}

	/***
	 * Increasing retry time after any expected error while retrieving of loots
	 */
	private void increaseRetry() {
		if (this.timeRetryMS < this.lootsConfig.getTimer().getMaxRetryMs()) {
			this.timeRetryMS = this.timeRetryMS + this.lootsConfig.getTimer().getAdditionalRetryMs();
		}
	}

	/***
	 * Вход в аккаунт FerretBot.engine
	 */
	private void login() {
		Connection.Response response = null;
		final String body = "{ \"email\": \"" + lootsConfig.getLogin() + "\", \"password\": \"" + lootsConfig.getPassword() + "\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		try {
			logger.info("Auth into Loots...");
			response = Jsoup.connect(loginUrl)
					.headers(headers)
					.requestBody(body)
					.method(Connection.Method.POST)
					.ignoreContentType(true)
					.execute();
		} catch (IOException e) {
			increaseRetry();
			logger.error("Could not login into Loots", e);
		}
		cookies = response.cookies();
	}
}
