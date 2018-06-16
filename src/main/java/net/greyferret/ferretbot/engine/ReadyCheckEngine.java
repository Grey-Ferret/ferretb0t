package net.greyferret.ferretbot.engine;

import net.greyferret.ferretbot.client.FerretChatClient;
import net.greyferret.ferretbot.entity.Viewer;
import net.greyferret.ferretbot.listener.FerretBotChatListener;
import net.greyferret.ferretbot.logic.ChatLogic;
import net.greyferret.ferretbot.service.ViewerService;
import net.greyferret.ferretbot.util.FerretBotUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReadyCheckEngine implements Runnable {
	private static final Logger logger = LogManager.getLogger(ChatLogic.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private ViewerService viewerService;

	private FerretBotChatListener ferretBotChatListener;
	private HashSet<String> acceptedList;
	private String nickToReply;

	@PostConstruct
	private void postConstruct() {
		ferretBotChatListener = context.getBean(FerretBotChatListener.class);
		acceptedList = new HashSet<>();
		nickToReply = "";
	}

	@Override
	public void run() {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		proceedReadyCheckAction();
	}

	private void proceedReadyCheckAction() {
		HashSet<String> timeoutList = new HashSet<>();
		ConcurrentHashMap<String, Boolean> readyCheckMap = ferretBotChatListener.getReadyCheckList();
		for (String key : readyCheckMap.keySet()) {
			Boolean value = readyCheckMap.get(key);
			if (value) {
				acceptedList.add(key);
			} else {
				timeoutList.add(key);
			}
		}
		if (readyCheckMap.size() > 0) {
			FerretChatClient ferretChatClient = context.getBean("FerretChatClient", FerretChatClient.class);
			if (timeoutList.size() > 0) {
				ferretChatClient.sendMessage(StringUtils.join(timeoutList, ", ") + " не подвердили участие");
				HashSet<Viewer> goList = viewerService.selectGoList(timeoutList.size());
				if (goList.size() == 0) {
					if (acceptedList.size() == 0) {
						ferretChatClient.sendMessage("@" + nickToReply + " никого не подтвердил готовность и больше в очереди никого нет...");
						return;
					} else {
						ferretChatClient.sendMessage("@" + nickToReply + " " + StringUtils.join(acceptedList, ", ") + " подвердили участие!");
					}
				}
				ferretChatClient.sendMessage("Были выбраны: " + FerretBotUtils.buildMergedViewersNicknames(goList));
				ferretChatClient.sendMessage(FerretBotUtils.buildMergedViewersNicknamesWithMention(goList) + " напишите в чат в течение минуты для подтверждения участия!");
				addReadyCheckList(goList);
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					logger.error(e);
				}
				proceedReadyCheckAction();
			} else {
				ferretBotChatListener.setReadyCheckList(new HashMap<>());
				ferretChatClient.sendMessage("@" + nickToReply + " ИТОГ: " + StringUtils.join(acceptedList, ", ") + " подвердили участие!");
			}
		}
	}

	public void addReadyCheckList(HashSet<Viewer> viewerList) {
		HashMap<String, Boolean> readyCheckList = new HashMap<>();
		for (Viewer viewer : viewerList) {
			readyCheckList.put(viewer.getLogin(), false);
		}
		ferretBotChatListener.setReadyCheckList(readyCheckList);
	}

	public void setNickForReply(String nickForReply) {
		this.nickToReply = nickForReply;
	}
}
