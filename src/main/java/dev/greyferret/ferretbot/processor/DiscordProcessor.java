package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.config.*;
import dev.greyferret.ferretbot.entity.GamevoteChannelCombination;
import dev.greyferret.ferretbot.entity.json.twitch.games.TwitchGames;
import dev.greyferret.ferretbot.entity.json.twitch.streams.StreamData;
import dev.greyferret.ferretbot.listener.DiscordListener;
import dev.greyferret.ferretbot.request.GamesTwitchRequest;
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

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
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
    @Autowired
    private ChatConfig chatConfig;

    private JDA jda;
    public TextChannel announcementChannel;
    public TextChannel testChannel;
    public TextChannel raffleChannel;
    public ArrayList<GamevoteChannelCombination> gameVoteChannelCombinations;
    private boolean isOn;
    private ApiProcessor apiProcessor;
    private ApiProcessor.ChannelStatus currentChannelStatus = ApiProcessor.ChannelStatus.UNDEFINED;

    public DiscordProcessor() {
        this.isOn = true;
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
                StreamData streamData = apiProcessor.getStreamData();
                ApiProcessor.ChannelStatus newChannelStatus = ApiProcessor.ChannelStatus.OFFLINE;
                if (streamData != null && streamData.getType().equalsIgnoreCase("live")) {
                    newChannelStatus = ApiProcessor.ChannelStatus.ONLINE;
                }
                String channelStatusMessage = "";
                if (this.currentChannelStatus.equals(ApiProcessor.ChannelStatus.OFFLINE) && newChannelStatus.equals(ApiProcessor.ChannelStatus.ONLINE)) {
                    if (streamData != null && StringUtils.isNotBlank(streamData.getGameId())) {
                        String gameId = streamData.getGameId();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("id", gameId);
                        TwitchGames gameInfo = apiProcessor.proceedTwitchRequest(new GamesTwitchRequest(params, new HashMap<>()));
                        if (gameInfo == null || gameInfo.getData() == null || gameInfo.getData().isEmpty() || StringUtils.isBlank(gameInfo.getData().get(0).getName())) {
                            log.warn("Stream in JSON was not null, had Stream Type, had Game Id, but could not parse games request");
                            channelStatusMessage = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
                        } else {
                            channelStatusMessage = Messages.ANNOUNCE_MESSAGE_1 + gameInfo.getData().get(0).getName() + Messages.ANNOUNCE_MESSAGE_2 + chatConfig.getChannel();
                        }
                    } else {
                        log.warn("Stream in JSON was not null, had Stream Type, but no Game was found");
                        channelStatusMessage = Messages.ANNOUNCE_MESSAGE_WITHOUT_GAME + chatConfig.getChannel();
                    }
                }
                if (this.currentChannelStatus != newChannelStatus) {
                    log.info("Updated status of channel to {}", newChannelStatus);
                }
                this.currentChannelStatus = newChannelStatus;
                if (botConfig.isDiscordOn() && botConfig.isDiscordAnnouncementOn() && StringUtils.isNotBlank(channelStatusMessage) && !applicationConfig.isDebug())
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
            if (emote.getRoles() == null || emote.getRoles().isEmpty()) {
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
