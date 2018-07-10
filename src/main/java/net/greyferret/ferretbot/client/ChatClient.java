package net.greyferret.ferretbot.client;

import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.LootsConfig;
import net.greyferret.ferretbot.entity.Loots;
import net.greyferret.ferretbot.service.LootsService;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Chat bot
 * <p>
 * Created by GreyFerret on 08.12.2017.
 */
@Component
public class ChatClient implements Runnable {
	private static final Logger logger = LogManager.getLogger(ChatClient.class);

	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private LootsConfig lootsConfig;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private LootsService lootsService;
	@Autowired
	private ViewerService viewerService;

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
		FerretBot = context.getBean(FerretChatClient.class);
		FerretBot.connect();
		ViewersClient viewersClient = context.getBean(ViewersClient.class);
		this.viewersThread = new Thread(viewersClient);
		this.viewersThread.setName("Viewers Thread");
		this.viewersThread.start();

		RaffleClient raffleClient = context.getBean(RaffleClient.class);
		this.raffleThread = new Thread(raffleClient);
		this.raffleThread.setName("RaffleDate Thread");
		this.raffleThread.start();

		try {
			while (isOn) {
				Thread.sleep(chatConfig.getRetryMs());
				givePointsForLoots();
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	private void givePointsForLoots() {
		Set<Loots> lootsEntries = lootsService.getUnpaidLoots();
		for (Loots loots : lootsEntries) {
			String message = FerretBotUtils.buildMessageAddPoints(loots.getViewerLootsMap().getViewer().getLogin(), lootsConfig.getPointsForLoots());
			viewerService.addPoints(loots.getViewerLootsMap().getViewer().getLogin(), lootsConfig.getPointsForLoots());
			FerretBot.sendMessage(message);
		}
	}
}