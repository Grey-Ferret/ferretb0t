package net.greyferret.ferretb0t.exception;

/**
 * Main Exception for specific FerretB0t exceptions
 * <p>
 * Created by GreyFerret on 07.12.2017.
 */
public class FerretB0tException extends Exception {
    public FerretB0tException(String s) {
        super(s);
    }

    public FerretB0tException(Exception e) {
        super(e);
    }

    public FerretB0tException(String s, Exception e) {
        super(s, e);
    }
}
