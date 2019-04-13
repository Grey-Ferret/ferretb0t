package dev.greyferret.ferretbot;

import dev.greyferret.ferretbot.client.FerretChatClient;
import dev.greyferret.ferretbot.config.ChatConfig;
import dev.greyferret.ferretbot.config.DbConfig;
import dev.greyferret.ferretbot.config.SpringConfig;
import dev.greyferret.ferretbot.config.StreamelementsConfig;
import dev.greyferret.ferretbot.listener.FerretBotChatListener;
import dev.greyferret.ferretbot.logic.ChatLogic;
import dev.greyferret.ferretbot.processor.ApiProcessor;
import dev.greyferret.ferretbot.processor.ChatProcessor;
import dev.greyferret.ferretbot.processor.StreamElementsAPIProcessor;
import dev.greyferret.ferretbot.service.SubVoteGameService;
import dev.greyferret.ferretbot.service.ViewerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main claSS for running entire FerretBot
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);

	/***
	 * Main method for running jar application
	 *
	 * @param args arguments for authorisations
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
		annotationConfigApplicationContext.register(DbConfig.class);
		annotationConfigApplicationContext.register(SpringConfig.class);
		annotationConfigApplicationContext.register(FerretBot.class);
		annotationConfigApplicationContext.register(ChatConfig.class);
		annotationConfigApplicationContext.register(ChatProcessor.class);
		annotationConfigApplicationContext.register(FerretChatClient.class);
		annotationConfigApplicationContext.register(FerretBotChatListener.class);
		annotationConfigApplicationContext.register(ChatLogic.class);
		annotationConfigApplicationContext.register(ViewerService.class);
		annotationConfigApplicationContext.register(SubVoteGameService.class);
		annotationConfigApplicationContext.register(StreamelementsConfig.class);
		annotationConfigApplicationContext.register(StreamElementsAPIProcessor.class);
		annotationConfigApplicationContext.register(ApiProcessor.class);
		annotationConfigApplicationContext.refresh();
		SpringApplication.run(Main.class);

		logger.info("Bot started!");
		FerretBot bot = annotationConfigApplicationContext.getBean(FerretBot.class);
		Thread botThread = new Thread(bot);
		botThread.setName("Main Thread");
		botThread.start();
	}
}
