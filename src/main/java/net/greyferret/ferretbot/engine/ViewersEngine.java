package net.greyferret.ferretbot.engine;

import net.greyferret.ferretbot.config.ChatConfig;
import net.greyferret.ferretbot.service.ViewerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ViewersEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger(ViewersEngine.class);

	@Autowired
	private ChatConfig chatConfig;
	@Autowired
	private ViewerService viewerService;
	@Autowired
	private ApplicationContext context;

	private boolean isOn;

	private ViewersEngine() {
		isOn = true;
	}

	/***
	 * Main run method
	 */
	@Override
	public void run() {
		try {
			boolean lastResult = false;
			while (isOn) {
				Integer retryMs;
				if (lastResult == true)
					retryMs = chatConfig.getUsersCheckMs();
				else
					retryMs = chatConfig.getUsersCheckMsFailed();

				Thread.sleep(retryMs);

				boolean isChannelOnline = context.getBean("isChannelOnline", boolean.class);
				List<String> nicknames = context.getBean("getViewers", ArrayList.class);

				if (nicknames.size() > 1) {
					viewerService.checkViewersAndAddPoints(nicknames, isChannelOnline);
					logger.info("User list (" + nicknames.size() + ") was refreshed!");
					lastResult = true;
				} else {
					lastResult = false;
				}
			}
		} catch (Exception e) {

		}
	}
}
