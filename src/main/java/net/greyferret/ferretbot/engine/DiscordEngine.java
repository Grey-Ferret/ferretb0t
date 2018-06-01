package net.greyferret.ferretbot.engine;

import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.DiscordConfig;
import net.greyferret.ferretbot.config.Messages;
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
import java.util.Map;

@Component
public class DiscordEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger();
	@Autowired
	private ApplicationContext context;
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private ChatConfig chatConfig;
	private String channelStatusUrl;
	private JDA jda;
	private TextChannel announcementChannel;
	private TextChannel testChannel;
	private boolean stop = false;
	private ChannelStatus currentChannelStatus = ChannelStatus.ONLINE;

	public DiscordEngine() {
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
		channelStatusUrl = "https://api.twitch.tv/kraken/streams/" + chatConfig.getChannel();
		jda.addEventListener(context.getBean(DiscordListener.class));
		announcementChannel = jda.getTextChannelById(discordConfig.getAnnouncementChannel());
		testChannel = jda.getTextChannelById(discordConfig.getTestChannel());
	}

	@Override
	public void run() {
		try {
			Thread.sleep(discordConfig.getCheckTime());
			testChannel.sendMessage(Messages.HELLO_MESSAGE).queue();
			while (!stop) {
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
		Connection.Response response = null;
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
				if (json.getStream() == null) {
					this.currentChannelStatus = ChannelStatus.OFFLINE;
					return result;
				}
				String streamType = json.getStream().getStreamType();
				if (streamType.equalsIgnoreCase("live")) {
					if (this.currentChannelStatus.equals(ChannelStatus.OFFLINE)) {
						if (StringUtils.isNotBlank(json.getStream().getGame())) {
							result = "@here А мы тут запустили стримчик по " + json.getStream().getGame() + "! Приходи и смотри по ссылочке: https://www.twitch.tv/drkiray";
						} else {
							logger.warn("Stream in JSON was not null, had Stream Type, but no Game was found");
							result = "@here А мы тут запустили стримчик! Приходи и смотри по ссылочке: https://www.twitch.tv/drkiray";
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

	@Bean(name = "isChannelOnline")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public boolean getFerretBot() {
		return this.currentChannelStatus == ChannelStatus.ONLINE;
	}

	enum ChannelStatus {ONLINE, OFFLINE}
}
