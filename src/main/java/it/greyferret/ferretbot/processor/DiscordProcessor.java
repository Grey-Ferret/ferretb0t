package it.greyferret.ferretbot.processor;

import it.greyferret.ferretbot.config.ApplicationConfig;
import it.greyferret.ferretbot.config.ChatConfig;
import it.greyferret.ferretbot.config.DiscordConfig;
import it.greyferret.ferretbot.config.Messages;
import it.greyferret.ferretbot.listener.DiscordListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
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
			jda = new JDABuilder(AccountType.BOT).setToken(discordConfig.getToken()).buildBlocking();
		} catch (LoginException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		jda.addEventListener(context.getBean(DiscordListener.class));
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

	public List<Emote> getEmotes(){
		return jda.getEmotes();
	}
}
