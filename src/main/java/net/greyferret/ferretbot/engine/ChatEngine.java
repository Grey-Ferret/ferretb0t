package net.greyferret.ferretbot.engine;

import net.greyferret.ferretbot.client.FerretChatClient;
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
public class ChatEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger(ChatEngine.class);

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

	public ChatEngine() {
		isOn = true;
	}

	/***
	 * Main run method
	 */
	@Override
	public void run() {
		FerretBot = context.getBean(FerretChatClient.class);
		FerretBot.connect();
		ViewersEngine viewersEngine = context.getBean(ViewersEngine.class);
		this.viewersThread = new Thread(viewersEngine);
		this.viewersThread.setName("Viewers Thread");
		this.viewersThread.start();

		RaffleEngine raffleEngine = context.getBean(RaffleEngine.class);
		this.raffleThread = new Thread(raffleEngine);
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