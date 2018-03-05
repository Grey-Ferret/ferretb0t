package net.greyferret.ferretb0t.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Created by GreyFerret on 15/12/2017.
 */
@Component
@Validated
@ConfigurationProperties(prefix = "main")
public class ApplicationConfig {
	private static final Logger logger = LogManager.getLogger();

	@NotEmpty
	private String debug;
	private boolean isDebug;

	private String getDebug() {
		return debug;
	}

	public boolean isDebug() {
		return this.isDebug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
		if (this.debug.equalsIgnoreCase("true")) {
			logger.info("Application running in DEBUG mode");
			this.isDebug = true;
		} else if (this.debug.equalsIgnoreCase("false")) {
			this.isDebug = false;
		} else {
			logger.fatal("Could not parse 'main.debug' property " + this.debug);
			throw new RuntimeException();
		}
	}
}
