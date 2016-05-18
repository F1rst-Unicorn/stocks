package de.njsm.stocks.sentry.db;

import org.junit.Test;

public class DatabaseHandlerTest {


    public void testTicket() {
        DatabaseHandler h = new DatabaseHandler();

        h.handleTicket("0002", 2);
    }
}
