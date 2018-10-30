package de.njsm.stocks.server.v1.internal.data;

import de.njsm.stocks.server.v1.internal.data.Ticket;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class TicketTest {

    private int idReference;
    private String ticketReference;
    private String pemReference;

    private Ticket uut;

    @Before
    public void setup() {
        idReference = 1;
        ticketReference = Ticket.generateTicket();
        pemReference = "my pem file";

        uut = new Ticket(idReference, ticketReference, pemReference);
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(idReference, uut.deviceId);
        Assert.assertEquals(ticketReference, uut.ticket);
        Assert.assertEquals(pemReference, uut.pemFile);
    }

    @Test
    public void testTicketCreation() {
        String ticket = Ticket.generateTicket();

        Assert.assertEquals(Ticket.TICKET_LENGTH, ticket.length());
        for (int i = 0; i < ticket.length(); i++) {
            Assert.assertTrue(Character.isLetterOrDigit(ticket.charAt(i)));
        }
    }

    @Test
    public void testVisitorCall() {
        StocksDataVisitor<Integer,Integer> input = Utils.getMockVisitor();
        Integer stub = 1;

        int result = uut.accept(input, stub);

        verify(input).ticket(uut, stub);
        assertEquals(2, result);
    }

}
