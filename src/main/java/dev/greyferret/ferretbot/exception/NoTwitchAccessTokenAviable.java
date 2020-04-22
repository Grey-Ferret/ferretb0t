package dev.greyferret.ferretbot.exception;

public class NoTwitchAccessTokenAviable extends RuntimeException {
    public NoTwitchAccessTokenAviable(Exception e) {
        super(e);
    }

    public NoTwitchAccessTokenAviable() {
        super();
    }
}
