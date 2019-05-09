package de.njsm.stocks.android.error;

import de.njsm.stocks.android.network.server.StatusCode;

public class StatusCodeException extends Exception {

    private StatusCode code;

    public StatusCodeException(StatusCode code) {
        this.code = code;
    }

    public StatusCodeException(String message, StatusCode code) {
        super(message);
        this.code = code;
    }

    public StatusCodeException(String message, Throwable cause, StatusCode code) {
        super(message, cause);
        this.code = code;
    }

    public StatusCodeException(StatusCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public StatusCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, StatusCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public StatusCode getCode() {
        return code;
    }
}
