package net.greyferret.ferretbot.util;

import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitteh.irc.client.library.Client;

import javax.annotation.Nonnull;

/**
 * Created by GreyFerret on 18.12.2017.
 */
public class FerretBotUtils {
	private static final Logger logger = LogManager.getLogger(FerretBotUtils.class);

	/***
	 * Format message, delete whitespaces
	 *
	 * @param message
	 * @return
	 */
	public static String buildMessage(String message) {
		String temp = message.replaceAll("\\s+", " ");
		return temp;
	}

	/***
	 * Building !bonus message
	 *
	 * @param nick
	 * @param points
	 * @return
	 */
	public static String buildMessageAddPoints(String nick, Long points) {
		if (points == null || StringUtils.isBlank(nick))
			return "";
		return "!bonus " + nick + " " + points;
	}

	/***
	 * Building !bonus message with remove instead
	 *
	 * @param nick
	 * @param points
	 * @return
	 */
	public static String buildRemovePointsMessage(String nick, Long points) {
		if (points == null || StringUtils.isBlank(nick))
			return "";
		return "!bonus " + nick + " -" + points;
	}

	/***
	 * Parsing author name (with special logic for guest)
	 *
	 * @param authorUnparsed
	 * @return parsed name
	 */
	public static String parseLootsAuthor(String authorUnparsed) {
		try {
			return parseLootsAuthor(authorUnparsed, authorUnparsed.toLowerCase().startsWith("guest_".toLowerCase()));
		} catch (Exception e) {
			logger.error("Error while parsing author of loots with name: " + authorUnparsed);
			return authorUnparsed;
		}
	}

	/***
	 * Parsing author name (with special logic for guest)
	 *
	 * @param authorUnparsed
	 * @param isGuest
	 * @return parsed name
	 */
	public static String parseLootsAuthor(@Nonnull String authorUnparsed, boolean isGuest) {
		if (StringUtils.isNotBlank(authorUnparsed)) {
			try {
				if (isGuest) {
					String temp = authorUnparsed.substring(6);
					String[] split = StringUtils.split(temp, "_");
					return StringUtils.deleteWhitespace(split[0]);
				}
			} catch (Exception e) {
				logger.error("Could not parse following name: " + authorUnparsed, e);
			}
			return StringUtils.deleteWhitespace(authorUnparsed);
		}
		logger.error("Author of loots was blank");
		return "";
	}

	public static String buildDiscordMessageLog(Message message) {
		try {
			return message.getAuthor().getName() + " in #" + message.getChannel().getName() + ": " + message.getContentRaw();
		} catch (Exception e) {
			logger.error("Could not build Log based on the following message: " + message, e);
			return "";
		}
	}

	public static boolean fixClient(Client.WithManagement client, String channelName) {
		client.getActorTracker().trackChannel(channelName);
		if (client.getChannel(channelName).isPresent())
			logger.info("FIXED IT YEAH");
		return true;
	}
}