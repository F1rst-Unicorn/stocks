package de.njsm.stocks.client.exceptions;

public class InputException extends Exception {

    public InputException() {
        super();
    }

    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputException(Throwable cause) {
        super(cause);
    }

    protected InputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
