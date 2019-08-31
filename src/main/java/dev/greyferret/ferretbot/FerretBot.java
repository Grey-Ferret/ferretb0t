package dev.greyferret.ferretbot;

import dev.greyferret.ferretbot.config.BotConfig;
import dev.greyferret.ferretbot.config.DiscordConfig;
import dev.greyferret.ferretbot.config.LootsConfig;
import dev.greyferret.ferretbot.listener.DiscordListener;
import dev.greyferret.ferretbot.processor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Main class, contains bot chat and loots bots
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
@Component
@EnableConfigurationProperties({BotConfig.class})
public class FerretBot implements Runnable {
	private static final Logger logger = LogManager.getLogger(FerretBot.class);

	@Autowired
	private BotConfig botConfig;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private AnnotationConfigApplicationContext annotationConfigApplicationContext;

	private boolean isOn;
	private LootsProcessor lootsProcessor;
	private ChatProcessor chatClient;
	private DiscordProcessor discordProcessor;
	private ApiProcessor apiProcessor;
	private AdventureProcessor adventureProcessor;
	private Thread lootsThread;
	private Thread chatThread;
	private Thread discordThread;
	private Thread apiThread;
	private Thread adventureThread;

	public FerretBot() {
	}

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
		this.isOn = true;

		this.apiProcessor = context.getBean(ApiProcessor.class);
		this.apiThread = new Thread(this.apiProcessor);
		this.apiThread.setName("Twitch Api Bot");
		this.apiThread.start();

		if (botConfig.getDiscordOn()) {
			annotationConfigApplicationContext.register(DiscordConfig.class);
			annotationConfigApplicationContext.register(DiscordListener.class);
			annotationConfigApplicationContext.register(DiscordProcessor.class);
			if (botConfig.getSubVoteOn()) {
				annotationConfigApplicationContext.register(SubVoteProcessor.class);
			}
			this.discordProcessor = context.getBean(DiscordProcessor.class);
			this.discordThread = new Thread(this.discordProcessor);
			this.discordThread.setName("Discord Bot");
			this.discordThread.start();
		}
		if (botConfig.getLootsOn()) {
			annotationConfigApplicationContext.register(LootsConfig.class);
			annotationConfigApplicationContext.register(LootsProcessor.class);
			this.lootsProcessor = context.getBean(LootsProcessor.class);
			this.lootsThread = new Thread(this.lootsProcessor);
			this.lootsThread.setName("Loots Bot");
			this.lootsThread.start();
		}
		this.chatClient = context.getBean(ChatProcessor.class);
		this.chatThread = new Thread(this.chatClient);
		this.chatThread.setName("Chat Bot");
		this.chatThread.start();
		this.adventureProcessor = context.getBean(AdventureProcessor.class);
		this.adventureThread = new Thread(this.adventureProcessor);
		this.adventureThread.setName("Adventure Thread");
		this.adventureThread.start();
	}
}