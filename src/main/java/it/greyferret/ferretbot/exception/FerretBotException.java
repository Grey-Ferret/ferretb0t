package it.greyferret.ferretbot.exception;

/**
 * Main Exception for specific FerretBot exceptions
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
public class FerretBotException extends Exception {
	public FerretBotException(String s) {
		super(s);
	}

	public FerretBotException(Exception e) {
		super(e);
	}

	public FerretBotException(String s, Exception e) {
		super(s, e);
	}
}
