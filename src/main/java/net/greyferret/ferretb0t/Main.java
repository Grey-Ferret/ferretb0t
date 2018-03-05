package net.greyferret.ferretb0t;

import net.greyferret.ferretb0t.config.SpringConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main claSS for running entire FerretB0t
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
public class Main {
	private static final Logger logger = LogManager.getLogger();

	/***
	 * Main method for running jar application
	 *
	 * @param args arguments for authorisations
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(SpringConfig.class);
		context.refresh();
		SpringApplication.run(Main.class);

		logger.info("Bot started!");
		FerretB0t bot = context.getBean(FerretB0t.class);
		Thread botThread = new Thread(bot);
		botThread.setName("Main Thread");
		botThread.start();
	}
}
