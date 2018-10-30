package de.njsm.stocks.server.v1.internal.data.visitor;

public class VisitorException extends RuntimeException {

    public VisitorException() {
        super();
    }

    public VisitorException(String message) {
        super(message);
    }

    public VisitorException(String message, Throwable cause) {
        super(message, cause);
    }

    public VisitorException(Throwable cause) {
        super(cause);
    }

    protected VisitorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
