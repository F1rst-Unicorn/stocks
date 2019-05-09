package de.njsm.stocks.android.error;

public class TextResourceException extends PrintableException {

    private int resourceId;

    public TextResourceException(int resourceId) {
        super();
        this.resourceId = resourceId;
    }

    public TextResourceException(String message, int resourceId) {
        super(message);
        this.resourceId = resourceId;
    }

    public TextResourceException(String message, Throwable cause, int resourceId) {
        super(message, cause);
        this.resourceId = resourceId;
    }

    public TextResourceException(Throwable cause, int resourceId) {
        super(cause);
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
