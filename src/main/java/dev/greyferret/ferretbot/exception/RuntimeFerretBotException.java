package dev.greyferret.ferretbot.exception;

/**
 * Created by GreyFerret on 20.12.2017.
 */
public class RuntimeFerretBotException extends RuntimeException {
	public RuntimeFerretBotException(Exception e) {
		super(e);
	}
}
