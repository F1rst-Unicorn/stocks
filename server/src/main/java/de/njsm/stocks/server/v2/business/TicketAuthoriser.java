package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import de.njsm.stocks.server.v2.business.data.ServerTicket;
import de.njsm.stocks.server.v2.db.TicketBackend;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class TicketAuthoriser extends BusinessObject {

    private static final Logger LOG = LogManager.getLogger(TicketAuthoriser.class);

    private TicketBackend databaseHandler;

    private AuthAdmin authAdmin;

    private int validityTime;

    public TicketAuthoriser(AuthAdmin authAdmin, TicketBackend databaseHandler, int validityTime) {
        this.authAdmin = authAdmin;
        this.databaseHandler = databaseHandler;
        this.validityTime = validityTime;
    }

    public Validation<StatusCode, String> handleTicket(ClientTicket ticket) {

        Validation<StatusCode, ServerTicket> dbTicket = doPrevalidation(ticket);

        if (dbTicket.isFail()) {
            return finishTransaction(Validation.fail(StatusCode.ACCESS_DENIED), databaseHandler);
        }

        authAdmin.saveCsr(ticket.deviceId, ticket.pemFile);

        if (! arePrincipalsValid(ticket)) {
            authAdmin.wipeDeviceCredentials(ticket.deviceId);
            return finishTransaction(Validation.fail(StatusCode.ACCESS_DENIED), databaseHandler);
        }

        return finishTransaction(grantAccess(ticket, dbTicket.success()), databaseHandler);
    }

    private Validation<StatusCode, ServerTicket> doPrevalidation(ClientTicket ticket) {
        Validation<StatusCode, ServerTicket> dbTicketResult = databaseHandler.getTicket(ticket);

        if (dbTicketResult.isFail()) {
            return Validation.fail(StatusCode.ACCESS_DENIED);
        }

        ServerTicket dbTicket = dbTicketResult.success();

        if (isTicketInvalid(ticket, dbTicket)) {
            LOG.warn("ticket is invalid");
            return Validation.fail(StatusCode.ACCESS_DENIED);
        }
        return Validation.success(dbTicket);
    }

    private Validation<StatusCode, String> grantAccess(ClientTicket ticket, ServerTicket dbTicket) {
        authAdmin.generateCertificate(ticket.deviceId);

        StatusCode removeResult = databaseHandler.removeTicket(dbTicket);
        if (removeResult == StatusCode.NOT_FOUND) {
            LOG.error("Could not remove previously found ticket " + dbTicket);
        }

        Validation<StatusCode, String> certificate = authAdmin.getCertificate(ticket.deviceId);
        if (certificate.isFail()) {
            return Validation.fail(certificate.fail());
        }
        LOG.info("Authorised new device with ID " + ticket.deviceId);
        return Validation.success(certificate.success());
    }

    /**
     * Determine whether the ticket has been created
     * by an existing user
     *
     * @param dbTicket The ticket to check for
     * @return true iff the ticket is valid
     */
    private boolean isTicketInvalid(ClientTicket ticket, ServerTicket dbTicket) {
        Date valid_till_date = new Date(dbTicket.creationDate.getTime() + validityTime * 60000);
        Date now = new Date();

        return now.after(valid_till_date) ||
                dbTicket.deviceId != ticket.deviceId;
    }

    private boolean arePrincipalsValid(ClientTicket ticket) {
        Validation<StatusCode, Principals> csrPrincipals = authAdmin.getPrincipals(ticket.deviceId);
        Validation<StatusCode, Principals> dbPrincipals = databaseHandler.getPrincipalsForTicket(ticket.ticket);

        if (dbPrincipals.isFail()) {
            return false;
        } else if (csrPrincipals.isFail()) {
            LOG.warn("No principals in CSR found");
            return false;
        } else if (! csrPrincipals.success().equals(dbPrincipals.success())) {
            LOG.warn("CSR Subject name differs from database! DB:" + dbPrincipals.success().toString()
                    + " CSR:" + csrPrincipals.toString());
            return false;
        } else {
            return true;
        }
    }
}
