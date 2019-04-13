package dev.greyferret.ferretbot.processor;

import dev.greyferret.ferretbot.client.FerretChatClient;
import dev.greyferret.ferretbot.config.BotConfig;
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
public class ChatProcessor implements Runnable {
	private static final Logger logger = LogManager.getLogger(ChatProcessor.class);

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
	private QueueProcessor queueProcessor;
	private Thread queueThread;

	public ChatProcessor() {
		isOn = true;
	}

	/***
	 * Main run method
	 */
	@Override
	public void run() {
		if (botConfig.getViewersServiceOn()) {
			annotationConfigApplicationContext.register(ViewersProcessor.class);
			ViewersProcessor viewersProcessor = context.getBean(ViewersProcessor.class);
			this.viewersThread = new Thread(viewersProcessor);
			this.viewersThread.setName("Viewers Thread");
			this.viewersThread.start();

			if (botConfig.getQueueOn()) {
				annotationConfigApplicationContext.register(QueueProcessor.class);
				this.queueProcessor = context.getBean(QueueProcessor.class);
				this.queueThread = new Thread(this.queueProcessor);
				this.queueThread.setName("Queue Bot");
				this.queueThread.start();
			}

			if (botConfig.getRaffleOn()) {
				annotationConfigApplicationContext.register(RaffleProcessor.class);
				RaffleProcessor raffleProcessor = context.getBean(RaffleProcessor.class);
				this.raffleThread = new Thread(raffleProcessor);
				this.raffleThread.setName("RaffleDate Thread");
				this.raffleThread.start();
			}
		}

		FerretBot = context.getBean(FerretChatClient.class);
		FerretBot.connect();
	}
}