package net.greyferret.ferretbot;

import net.greyferret.ferretbot.client.ApiClient;
import net.greyferret.ferretbot.client.ChatClient;
import net.greyferret.ferretbot.client.DiscordClient;
import net.greyferret.ferretbot.client.LootsClient;
import net.greyferret.ferretbot.config.BotConfig;
import net.greyferret.ferretbot.config.DiscordConfig;
import net.greyferret.ferretbot.config.LootsConfig;
import net.greyferret.ferretbot.listener.DiscordListener;
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
	private LootsClient lootsClient;
	private ChatClient chatClient;
	private DiscordClient discordClient;
	private ApiClient apiClient;
	private Thread lootsThread;
	private Thread chatThread;
	private Thread discordThread;
	private Thread apiThread;

	public FerretBot() {
	}

	@PostConstruct
	private void postConstruct() {
	}

	@Override
	public void run() {
		this.isOn = true;

		this.apiClient = context.getBean(ApiClient.class);
		this.apiThread = new Thread(this.apiClient);
		this.apiThread.setName("Twitch Api Bot");
		this.apiThread.start();

		if (botConfig.getDiscordOn()) {
			annotationConfigApplicationContext.register(DiscordConfig.class);
			annotationConfigApplicationContext.register(DiscordClient.class);
			annotationConfigApplicationContext.register(DiscordListener.class);
			this.discordClient = context.getBean(DiscordClient.class);
			this.discordThread = new Thread(this.discordClient);
			this.discordThread.setName("Discord Bot");
			this.discordThread.start();
		}
		if (botConfig.getLootsOn()) {
			annotationConfigApplicationContext.register(LootsConfig.class);
			annotationConfigApplicationContext.register(LootsClient.class);
			this.lootsClient = context.getBean(LootsClient.class);
			this.lootsThread = new Thread(this.lootsClient);
			this.lootsThread.setName("Loots Bot");
			this.lootsThread.start();
		}
		this.chatClient = context.getBean(ChatClient.class);
		this.chatThread = new Thread(this.chatClient);
		this.chatThread.setName("Chat Bot");
		this.chatThread.start();

		while (isOn) {

		}
	}
}
