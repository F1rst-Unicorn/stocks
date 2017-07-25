package de.njsm.stocks.error;

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

}
