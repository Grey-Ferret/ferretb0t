package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.config.Messages;
import dev.greyferret.ferretbot.listener.DiscordListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
@EnableConfigurationProperties({ChatConfig.class, DiscordConfig.class})
public class DiscordProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(DiscordProcessor.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private ApplicationConfig applicationConfig;

	private JDA jda;
	public TextChannel announcementChannel;
	public TextChannel testChannel;
	public TextChannel raffleChannel;
	public TextChannel subsChannel;
	public TextChannel subVoteChannel;
	private boolean isOn;
	private ApiProcessor apiProcessor;

	public DiscordProcessor() {
		this.isOn = true;
	}

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
		try {
			JDABuilder builder = new JDABuilder(discordConfig.getToken());
			// Disable parts of the cache
			builder.setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE));
			// Enable the bulk delete event
			builder.setBulkDeleteSplittingEnabled(false);
			// Disable compression (not recommended)
			builder.setCompression(Compression.NONE);
			// Set activity (like "playing Something")
			builder.setActivity(Activity.playing("Planning to take over the world"));
			builder.addEventListeners(context.getBean(DiscordListener.class));
			jda = builder.build();
			jda.awaitReady();
		} catch (LoginException | InterruptedException e) {
			logger.error(e);
		}
		announcementChannel = jda.getTextChannelById(discordConfig.getAnnouncementChannel());
		testChannel = jda.getTextChannelById(discordConfig.getTestChannel());
		raffleChannel = jda.getTextChannelById(discordConfig.getRaffleChannel());
		subsChannel = jda.getTextChannelById(discordConfig.getSubsChannel());
		subVoteChannel = jda.getTextChannelById(discordConfig.getSubVoteChannel());

		apiProcessor = context.getBean(ApiProcessor.class);

		try {
			Thread.sleep(discordConfig.getCheckTime());
			testChannel.sendMessage(Messages.HELLO_MESSAGE).queue();
			while (isOn) {
				String channelStatusMessage = apiProcessor.getChannelStatusMessage();
				if (StringUtils.isNotBlank(channelStatusMessage) && !applicationConfig.isDebug())
					announcementChannel.sendMessage(channelStatusMessage).queue();
				Thread.sleep(discordConfig.getCheckTime());
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	public List<Emote> getAllEmotes() {
		return jda.getEmotes();
	}

	public List<Emote> getPublicEmotes() {
		List<Emote> emotes = getAllEmotes();
		ArrayList<Emote> res = new ArrayList<>();
		for (Emote emote : emotes) {
			if (emote.getRoles() == null || emote.getRoles().size() == 0) {
				res.add(emote);
			}
		}
		return res;
	}
}
