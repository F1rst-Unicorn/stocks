package de.njsm.stocks.backend.network;

import de.njsm.stocks.common.data.Ticket;

public interface TicketCallback {

    void applyToTicket(Ticket ticket);
}
