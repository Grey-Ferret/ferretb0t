package net.greyferret.ferretbot.engine;

import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.DiscordConfig;
import net.greyferret.ferretbot.config.Messages;
import net.greyferret.ferretbot.entity.json.twitch.games.TwitchGames;
import net.greyferret.ferretbot.entity.json.twitch.streams.Datum;
import net.greyferret.ferretbot.entity.json.twitch.streams.TwitchStreams;
import net.greyferret.ferretbot.listener.DiscordListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiscordEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger(DiscordEngine.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private ChatConfig chatConfig;

	private String channelStatusUrl;
	private String gameInfoUrl;
	private JDA jda;
	public TextChannel announcementChannel;
	public TextChannel testChannel;
	public TextChannel raffleChannel;
	private boolean isOn;
	private ChannelStatus currentChannelStatus = ChannelStatus.ONLINE;
	private static final String twitchAPIPrefix = "https://api.twitch.tv/helix/";

	enum ChannelStatus {ONLINE, OFFLINE}

	public DiscordEngine() {
		this.isOn = true;
	}

	@PostConstruct
	private void postConstruct() {
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(discordConfig.getToken()).buildBlocking();
		} catch (LoginException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		channelStatusUrl = this.twitchAPIPrefix + "streams?user_login=" + chatConfig.getChannel();
		gameInfoUrl = this.twitchAPIPrefix + "games?id=";
		jda.addEventListener(context.getBean(DiscordListener.class));
		announcementChannel = jda.getTextChannelById(discordConfig.getAnnouncementChannel());
		testChannel = jda.getTextChannelById(discordConfig.getTestChannel());
		raffleChannel = jda.getTextChannelById(discordConfig.getRaffleChannel());
	}

	@Override
	public void run() {
		try {
			Thread.sleep(discordConfig.getCheckTime());
			testChannel.sendMessage(Messages.HELLO_MESSAGE).queue();
			while (isOn) {
				String channelStatusMessage = getChannelStatus();
				if (StringUtils.isNotBlank(channelStatusMessage))
					announcementChannel.sendMessage(channelStatusMessage).queue();
				Thread.sleep(discordConfig.getCheckTime());
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	private String getChannelStatus() {
		String result = "";
		Connection.Response response;
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Client-ID", chatConfig.getClientId());
			response = Jsoup.connect(channelStatusUrl)
					.method(Connection.Method.GET)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String body = response.body();
			if (StringUtils.isBlank(body)) {
				logger.error("Could not request Channel Status, response was blank");
			} else {
				Gson g = new Gson();
				TwitchStreams json = g.fromJson(body, TwitchStreams.class);
				List<Datum> datum = json.getData();
				if (datum == null || datum.size() == 0) {
					this.currentChannelStatus = ChannelStatus.OFFLINE;
					return result;
				}
				Datum twitchInfo = datum.get(0);
				String streamType = twitchInfo.getType();
				if (streamType.equalsIgnoreCase("live")) {
					if (this.currentChannelStatus.equals(ChannelStatus.OFFLINE)) {
						if (StringUtils.isNotBlank(twitchInfo.getGameId())) {
							String gameId = twitchInfo.getGameId();
							TwitchGames gameInfo = getGameInfo(gameId);
							if (gameInfo == null || gameInfo.getData() == null || gameInfo.getData().size() == 0 || StringUtils.isBlank(gameInfo.getData().get(0).getName())) {
								logger.warn("Stream in JSON was not null, had Stream Type, had Game Id, but could not parse games request");
								result = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
							} else {
								result = Messages.ANNOUNCE_MESSAGE_1 + gameInfo.getData().get(0).getName() + Messages.ANNOUNCE_MESSAGE_2 + chatConfig.getChannel();
							}
						} else {
							logger.warn("Stream in JSON was not null, had Stream Type, but no Game was found");
							result = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
						}
					}
					this.currentChannelStatus = ChannelStatus.ONLINE;
				} else {
					this.currentChannelStatus = ChannelStatus.OFFLINE;
				}
			}
		} catch (IOException e) {
			logger.error("Could not request Channel Status", e);
			return result;
		}
		return result;
	}

	private TwitchGames getGameInfo(String gameId) {
		TwitchGames twitchGames = null;
		Connection.Response response;
		Map<String, String> headers = new HashMap<>();
		headers.put("Client-ID", chatConfig.getClientId());
		final String gameInfoUrlComplete = gameInfoUrl + gameId;
		try {
			response = Jsoup.connect(gameInfoUrlComplete)
					.method(Connection.Method.GET)
					.ignoreContentType(true)
					.headers(headers)
					.execute();
			String body = response.body();
			Gson gson = new Gson();
			twitchGames = gson.fromJson(body, TwitchGames.class);
		} catch (IOException e) {
			logger.error(e);
			return twitchGames;
		}
		return twitchGames;
	}

	@Bean(name = "isChannelOnline")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public boolean getFerretBot() {
		return this.currentChannelStatus == ChannelStatus.ONLINE;
	}
}
