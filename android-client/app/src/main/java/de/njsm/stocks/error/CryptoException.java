package de.njsm.stocks.error;

public class CryptoException extends TextResourceException {


    public CryptoException(int resourceId) {
        super(resourceId);
    }

    public CryptoException(String message, int resourceId) {
        super(message, resourceId);
    }

    public CryptoException(String message, Throwable cause, int resourceId) {
        super(message, cause, resourceId);
    }

    public CryptoException(Throwable cause, int resourceId) {
        super(cause, resourceId);
    }
}
