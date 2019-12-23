package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.ApplicationConfig;
import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.config.Messages;
import dev.greyferret.ferretbot.entity.GamevoteChannelCombination;
import dev.greyferret.ferretbot.listener.DiscordListener;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
@Log4j2
@EnableConfigurationProperties({DiscordConfig.class, BotConfig.class})
public class DiscordProcessor implements Runnable, ApplicationListener<ContextStartedEvent> {
	@Autowired
	private ApplicationContext context;
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private BotConfig botConfig;

	private JDA jda;
	public TextChannel announcementChannel;
	public TextChannel testChannel;
	public TextChannel raffleChannel;
	public ArrayList<GamevoteChannelCombination> gameVoteChannelCombinations;
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
		gameVoteChannelCombinations = new ArrayList<>();
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
			log.error(e.toString());
		}
		announcementChannel = jda.getTextChannelById(discordConfig.getAnnouncementChannel());
		testChannel = jda.getTextChannelById(discordConfig.getTestChannel());
		raffleChannel = jda.getTextChannelById(discordConfig.getRaffleChannel());
		List<Long> gameVoteAddChannelIds = discordConfig.getGameVoteAddChannels();
		List<Long> gameVoteVoteChannelIds = discordConfig.getGameVoteVoteChannels();
		for (int i = 0; i < Math.min(gameVoteAddChannelIds.size(), gameVoteVoteChannelIds.size()); i++) {
			Long addId = gameVoteAddChannelIds.get(i);
			Long voteId = gameVoteVoteChannelIds.get(i);
			if (addId == null || voteId == null) {
				continue;
			}
			gameVoteChannelCombinations.add(new GamevoteChannelCombination(jda.getTextChannelById(addId), jda.getTextChannelById(voteId)));
		}

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
			log.error(e.toString());
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

	@Override
	public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
		if (botConfig.isDiscordOn()) {
			Thread thread = new Thread(this);
			thread.setName("Discord Thread");
			thread.start();
			log.info(thread.getName() + " started");
		} else {
			log.info("Discord is off");
		}
	}

	public JDA getJDA() {
		return jda;
	}

	public GamevoteChannelCombination getGamevoteCombinationByAddChannel(MessageChannel channel) {
		return getGamevoteCombinationByAddChannel(channel.getIdLong());
	}

	public GamevoteChannelCombination getGamevoteCombinationByAddChannel(long channelId) {
		for (GamevoteChannelCombination combination : this.gameVoteChannelCombinations) {
			if (combination.getAddChannelId() == channelId)
				return combination;
		}
		return null;
	}

	public GamevoteChannelCombination getGamevoteCombinationByVoteChannel(MessageChannel channel) {
		return getGamevoteCombinationByVoteChannel(channel.getIdLong());
	}

	public GamevoteChannelCombination getGamevoteCombinationByVoteChannel(long voteChannelId) {
		for (GamevoteChannelCombination combination : this.gameVoteChannelCombinations) {
			if (combination.getVoteChannelId() == voteChannelId)
				return combination;
		}
		return null;
	}
}
