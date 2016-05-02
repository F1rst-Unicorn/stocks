package de.njsm.stocks.linux.client.network;

import de.njsm.stocks.linux.client.CertificateManager;
import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.frontend.CertificateGenerator;

import java.io.File;
import java.util.logging.Level;

public class TicketHandler {

    protected Configuration c;

    public TicketHandler (Configuration c) {
        this.c = c;
    }

    public void handleTicket(String ticket) {
        try {

        } catch (Exception e){
            c.getLog().log(Level.SEVERE, "TicketHandler: Ticket retrival failed: " + e.getMessage());
        }
    }

    public void verifyServerCa(String fingerprint) {

    }

}
