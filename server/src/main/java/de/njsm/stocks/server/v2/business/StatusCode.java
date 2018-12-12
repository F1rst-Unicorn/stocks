package de.njsm.stocks.server.v2.business;

public enum StatusCode {

    SUCCESS,

    GENERAL_ERROR,

    NOT_FOUND,

    INVALID_DATA_VERSION,

    FOREIGN_KEY_CONSTRAINT_VIOLATION,

    DATABASE_UNREACHABLE,

    ACCESS_DENIED,

    INVALID_ARGUMENT,

    CA_UNREACHABLE,

}