package net.greyferret.ferretbot.engine;

import net.greyferret.ferretbot.client.FerretChatClient;
import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.config.LootsConfig;
import net.greyferret.ferretbot.entity.Loots;
import net.greyferret.ferretbot.service.LootsService;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Chat bot
 * <p>
 * Created by GreyFerret on 08.12.2017.
 */
@Component
public class ChatEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger();
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
	private boolean isStopped = false;
	private Thread viewersThread;

	public ChatEngine() {
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

		try {
			while (!isStopped) {
				Thread.sleep(chatConfig.getRetryMs());
				givePoints();
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	private void givePoints() {
		Set<Loots> lootsEntries = lootsService.payForUnpaidLoots();
		for (Loots loots : lootsEntries) {
			String message = FerretBotUtils.buildAddPointsMessage(loots.getViewerLootsMap().getViewer().getLogin(), lootsConfig.getPointsForLoots());
			viewerService.addPoints(loots.getViewerLootsMap().getViewer().getLogin(), lootsConfig.getPointsForLoots());
			if (StringUtils.isNotBlank(message)) {
				FerretBot.sendMessage(message);
			}
		}
	}
}