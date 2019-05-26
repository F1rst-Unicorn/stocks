/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.common.data;

import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
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
