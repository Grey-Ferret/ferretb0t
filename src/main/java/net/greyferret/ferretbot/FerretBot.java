package net.greyferret.ferretbot;

import net.greyferret.ferretbot.client.ChatClient;
import net.greyferret.ferretbot.client.DiscordClient;
import net.greyferret.ferretbot.client.LootsClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Main class, contains bot chat and loots bots
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
@Component
public class FerretBot implements Runnable {
	private static final Logger logger = LogManager.getLogger(FerretBot.class);

	@Autowired
	private ApplicationContext context;

	private boolean isOn;
	private LootsClient lootsClient;
	private ChatClient chatClient;
	private DiscordClient discordClient;
	private Thread lootsThread;
	private Thread chatThread;
	private Thread discordThread;

	public FerretBot() {
	}

	@PostConstruct
	private void postConstruct() {
		this.lootsClient = context.getBean(LootsClient.class);
		this.chatClient = context.getBean(ChatClient.class);
		this.discordClient = context.getBean(DiscordClient.class);
		this.lootsThread = new Thread(this.lootsClient);
		this.lootsThread.setName("Loots Bot");
		this.chatThread = new Thread(this.chatClient);
		this.chatThread.setName("Chat Bot");
		this.discordThread = new Thread(this.discordClient);
		this.discordThread.setName("Discord Bot");
	}

	@Override
	public void run() {
		this.isOn = true;
		this.discordThread.start();
		this.lootsThread.start();
		this.chatThread.start();

		while (isOn) {

		}
	}
}
