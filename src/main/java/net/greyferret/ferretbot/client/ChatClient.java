package net.greyferret.ferretbot.client;

import net.greyferret.ferretbot.config.BotConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Chat bot
 * <p>
 * Created by GreyFerret on 08.12.2017.
 */
@Component
@EnableConfigurationProperties({BotConfig.class})
public class ChatClient implements Runnable {
	private static final Logger logger = LogManager.getLogger(ChatClient.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private BotConfig botConfig;
	@Autowired
	private AnnotationConfigApplicationContext annotationConfigApplicationContext;

	private FerretChatClient FerretBot;
	private boolean isOn;
	private Thread viewersThread;
	private Thread raffleThread;

	public ChatClient() {
		isOn = true;
	}

	/***
	 * Main run method
	 */
	@Override
	public void run() {
		if (botConfig.getViewersServiceOn()) {
			annotationConfigApplicationContext.register(ViewersClient.class);
			ViewersClient viewersClient = context.getBean(ViewersClient.class);
			this.viewersThread = new Thread(viewersClient);
			this.viewersThread.setName("Viewers Thread");
			this.viewersThread.start();

			if (botConfig.getReadyCheckOn()) {
				annotationConfigApplicationContext.register(ReadyCheckClient.class);
			}

			if (botConfig.getRaffleOn()) {
				annotationConfigApplicationContext.register(RaffleClient.class);
				RaffleClient raffleClient = context.getBean(RaffleClient.class);
				this.raffleThread = new Thread(raffleClient);
				this.raffleThread.setName("RaffleDate Thread");
				this.raffleThread.start();
			}
		}

		FerretBot = context.getBean(FerretChatClient.class);
		FerretBot.connect();

		while (isOn) {

		}
	}
}