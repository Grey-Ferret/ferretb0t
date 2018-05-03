package net.greyferret.ferretb0t.engine;

import net.greyferret.ferretb0t.client.FerretChatClient;
import net.greyferret.ferretb0t.config.ChatConfig;
import net.greyferret.ferretb0t.config.LootsConfig;
import net.greyferret.ferretb0t.entity.Loots;
import net.greyferret.ferretb0t.service.LootsService;
import net.greyferret.ferretb0t.service.ViewerService;
import net.greyferret.ferretb0t.util.FerretB0tUtils;
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
	private FerretChatClient ferretB0t;
	private boolean isStopped = false;
	private Thread viewersThread;

	public ChatEngine() {
	}

	/***
	 * Main run method
	 */
	@Override
	public void run() {
		ferretB0t = context.getBean(FerretChatClient.class);
		ferretB0t.connect();
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
			String message = FerretB0tUtils.buildAddPointsMessage(loots.getViewerLootsMap().getViewer().getLogin(), lootsConfig.getPointsForLoots());
			viewerService.addPoints(loots.getViewerLootsMap().getViewer().getLogin(), lootsConfig.getPointsForLoots());
			if (StringUtils.isNotBlank(message)) {
				ferretB0t.sendMessage(message);
			}
		}
	}
}