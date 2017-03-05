package de.njsm.stocks.client.exceptions;

public class InitialisationException extends PrintableException {

    public InitialisationException() {
        super();
    }

    public InitialisationException(String message) {
        super(message);
    }

    public InitialisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitialisationException(Throwable cause) {
        super(cause);
    }

    protected InitialisationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
