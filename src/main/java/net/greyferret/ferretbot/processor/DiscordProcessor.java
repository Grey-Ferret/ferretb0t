package net.greyferret.ferretbot.processor;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.DiscordConfig;
import net.greyferret.ferretbot.config.Messages;
import net.greyferret.ferretbot.listener.DiscordListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Component
@EnableConfigurationProperties({ChatConfig.class, DiscordConfig.class})
public class DiscordProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(DiscordProcessor.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private DiscordConfig discordConfig;

	private JDA jda;
	public TextChannel announcementChannel;
	public TextChannel testChannel;
	public TextChannel raffleChannel;
	private boolean isOn;
	private ApiProcessor apiProcessor;

	public DiscordProcessor() {
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
		jda.addEventListener(context.getBean(DiscordListener.class));
		announcementChannel = jda.getTextChannelById(discordConfig.getAnnouncementChannel());
		testChannel = jda.getTextChannelById(discordConfig.getTestChannel());
		raffleChannel = jda.getTextChannelById(discordConfig.getRaffleChannel());

		apiProcessor = context.getBean(ApiProcessor.class);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(discordConfig.getCheckTime());
			testChannel.sendMessage(Messages.HELLO_MESSAGE).queue();
			while (isOn) {
				String channelStatusMessage = apiProcessor.getChannelStatusMessage();
				if (StringUtils.isNotBlank(channelStatusMessage))
					announcementChannel.sendMessage(channelStatusMessage).queue();
				Thread.sleep(discordConfig.getCheckTime());
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
}
