package de.njsm.stocks.server.v2.business;

public enum StatusCode {

    SUCCESS(0),

    GENERAL_ERROR(1),

    NOT_FOUND(2),

    INVALID_DATA_VERSION(3),

    FOREIGN_KEY_CONSTRAINT_VIOLATION(4),

    DATABASE_UNREACHABLE(5),

    ;


    public int code;

    StatusCode(int code) {
        this.code = code;
    }
}
