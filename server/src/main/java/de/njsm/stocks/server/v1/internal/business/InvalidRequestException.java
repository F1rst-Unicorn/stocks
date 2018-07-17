package de.njsm.stocks.server.v1.internal.business;

public class InvalidRequestException extends Exception {

    public InvalidRequestException(String message) {
        super(message);
    }

}
