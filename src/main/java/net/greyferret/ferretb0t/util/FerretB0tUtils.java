package net.greyferret.ferretb0t.util;

import net.dv8tion.jda.core.entities.Message;
import net.greyferret.ferretb0t.entity.Loots;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by GreyFerret on 18.12.2017.
 */
public class FerretB0tUtils {
    private static final Logger logger = LogManager.getLogger();

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
    public static String buildAddPointsMessage(String nick, Long points) {
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
    public static String parseLootsAuthor(String authorUnparsed, boolean isGuest) {
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

    public static String buildDiscordMessageLog(Message message) {
        try {
            return message.getAuthor().getName() + " in #" + message.getChannel().getName() + ": " + message.getContentRaw();
        } catch (Exception e) {
            logger.error("Could not build Log based on the following message: " + message, e);
            return "";
        }
    }
}