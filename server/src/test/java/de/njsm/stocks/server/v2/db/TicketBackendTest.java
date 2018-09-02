package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.Config;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v1.internal.db.DatabaseHelper;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import fj.data.Validation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

public class TicketBackendTest {

    private TicketBackend uut;

    private static int resourceCounter = 0;

    @Before
    public void resetDatabase() throws SQLException {
        DatabaseHelper.resetSampleData();

        Config c = new Config(System.getProperties());
        uut = new TicketBackend(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword(),
                "hystrix group ticket" + String.valueOf(resourceCounter));
        resourceCounter++;
    }

    @Test
    public void successfulTicketRetrival() {
        ClientTicket ticket = new ClientTicket(3, "AAAA", "csr");

        Validation<StatusCode, ServerTicket> result = uut.getTicket(ticket);

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ticket.ticket, result.success().ticket);
        Assert.assertEquals(ticket.deviceId, result.success().deviceId);
    }

    @Test
    public void unknownTicketIsReported() {
        ClientTicket ticket = new ClientTicket(3, "unknown", "csr");

        Validation<StatusCode, ServerTicket> result = uut.getTicket(ticket);

        Assert.assertTrue(result.isFail());
        Assert.assertEquals(StatusCode.NOT_FOUND, result.fail());
    }

    @Test
    public void removeATicket() {
        ClientTicket input = new ClientTicket(3, "AAAA", "csr");
        ServerTicket ticket = uut.getTicket(input).success();

        StatusCode result = uut.removeTicket(ticket);

        Assert.assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void removingUnknownTicketIsReported() {
        ServerTicket ticket = new ServerTicket(-1, new Date(), 0, "");

        StatusCode result = uut.removeTicket(ticket);

        Assert.assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void gettingUnknownPrincipalsIsReported() {

        Validation<StatusCode, Principals> result = uut.getPrincipalsForTicket("unknown");

        Assert.assertTrue(result.isFail());
        Assert.assertEquals(StatusCode.NOT_FOUND, result.fail());
    }

    @Test
    public void retrievePrincipalsSuccessfully() {
        Principals expected = new Principals("Alice", "laptop", 2, 3);

        Validation<StatusCode, Principals> result = uut.getPrincipalsForTicket("AAAA");

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(expected, result.success());
    }
}