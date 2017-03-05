package de.njsm.stocks.client.exceptions;

/**
 * Base class for exceptions with UI error messages
 *
 * All subclasses of this class shall only contain exception messages
 * which can be displayed to the user without modification.
 */
public class PrintableException extends Exception {
    public PrintableException() {
        super();
    }

    public PrintableException(String message) {
        super(message);
    }

    public PrintableException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrintableException(Throwable cause) {
        super(cause);
    }

    protected PrintableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
