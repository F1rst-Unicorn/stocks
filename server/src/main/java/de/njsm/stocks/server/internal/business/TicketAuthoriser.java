package de.njsm.stocks.server.internal.business;

import de.njsm.stocks.common.data.Principals;
import de.njsm.stocks.common.data.ServerTicket;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.server.internal.auth.AuthAdmin;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;

public class TicketAuthoriser {

    private static final Logger LOG = LogManager.getLogger(TicketAuthoriser.class);

    private AuthAdmin authAdmin;

    private DatabaseHandler handler;

    private int validityTime;

    public TicketAuthoriser(AuthAdmin authAdmin, DatabaseHandler handler, int validityTime) {
        this.authAdmin = authAdmin;
        this.handler = handler;
        this.validityTime = validityTime;
    }

    public Ticket handleTicket(Ticket ticket) {
        try {
            return handleTicketInternally(ticket);

        } catch (SecurityException |
                IOException e) {
            LOG.warn("Could not handle ticket", e);
            authAdmin.wipeDeviceCredentials(ticket.deviceId);
            return getErrorTicket(ticket);
        } catch (InvalidRequestException e) {
            // don't erase any certificates in this case!
            LOG.warn("Could not handle ticket", e);
            return getErrorTicket(ticket);
        }
    }

    private Ticket handleTicketInternally(Ticket ticket) throws IOException, SecurityException, InvalidRequestException {
        ServerTicket dbTicket = handler.getTicket(ticket.ticket);

        if (! isTicketValid(ticket, dbTicket)) {
            throw new InvalidRequestException("ticket is not valid");
        }

        authAdmin.saveCsr(ticket.deviceId, ticket.pemFile);

        if (! arePrincipalsValid(ticket)) {
            throw new SecurityException("Could not match user for ticket");
        }

        authAdmin.generateCertificate(ticket.deviceId);

        handler.remove(dbTicket);

        ticket.pemFile = authAdmin.getCertificate(ticket.deviceId);
        LOG.info("Authorised new device with ID " + ticket.deviceId);
        return ticket;
    }

    /**
     * Determine whether the ticket has been created
     * by an existing user
     *
     * @param dbTicket The ticket to check for
     * @return true iff the ticket is valid
     */
    private boolean isTicketValid(Ticket ticket, ServerTicket dbTicket) {

        if (dbTicket != null) {
            Date valid_till_date = new Date(dbTicket.creationDate.getTime() + validityTime * 60000);
            Date now = new Date();

            return now.before(valid_till_date) &&
                    dbTicket.deviceId == ticket.deviceId;
        } else {
            LOG.warn("No ticket found for deviceId " + ticket.deviceId);
            return false;
        }

    }

    private boolean arePrincipalsValid(Ticket ticket) throws IOException {
        Principals csrPrincipals = authAdmin.getPrincipals(ticket.deviceId);
        Principals dbPrincipals = handler.getPrincipalsForTicket(ticket.ticket);

        if (dbPrincipals == null) {
            LOG.warn("No principals in DB found");
            return false;
        } else if (! csrPrincipals.equals(dbPrincipals)) {
            LOG.warn("CSR Subject name differs from database! DB:" + dbPrincipals.toString() + " CSR:" +
                    csrPrincipals.toString());
            return false;
        } else {
            return true;
        }
    }

    private Ticket getErrorTicket(Ticket request) {
        return new Ticket(request.deviceId, request.ticket, null);
    }
}
