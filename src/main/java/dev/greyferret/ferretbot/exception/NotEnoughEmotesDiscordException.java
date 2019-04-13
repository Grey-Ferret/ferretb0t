package dev.greyferret.ferretbot.exception;

/**
 * Exception when there was error while parsing Running Loots (loots that is currently showing)
 * <p>
 * Created by GreyFerret on 11.12.2017.
 */
public class NotEnoughEmotesDiscordException extends FerretBotException {
	public NotEnoughEmotesDiscordException(String e) {
		super(e);
	}
}
