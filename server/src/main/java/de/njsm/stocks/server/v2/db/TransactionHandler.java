package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;

public interface TransactionHandler {
    StatusCode commit();

    StatusCode rollback();

    StatusCode setReadOnly();
}
