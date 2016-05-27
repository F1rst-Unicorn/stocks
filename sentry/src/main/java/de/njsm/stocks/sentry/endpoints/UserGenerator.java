package de.njsm.stocks.sentry.endpoints;

import de.njsm.stocks.sentry.auth.CertificateManager;
import de.njsm.stocks.sentry.data.Ticket;
import de.njsm.stocks.sentry.db.DatabaseHandler;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/uac")
public class UserGenerator {

    protected final DatabaseHandler handler = new DatabaseHandler();
    protected final Logger log = Logger.getLogger("stocks");

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @POST
    @Path("/newuser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Ticket getNewCertificate(Ticket ticket){

        try {

            // check ticket validity
            if (! handler.isTicketValid(ticket.ticket, ticket.deviceId)) {
                throw new Exception("ticket is not valid");
            }

            // save signing request
            String userFileName = String.format("user_%d", ticket.deviceId);
            String csrFileName = String.format(CertificateManager.csrFormatString, userFileName);
            FileOutputStream csrFile = new FileOutputStream(csrFileName);
            IOUtils.write(ticket.pemFile.getBytes(), csrFile);
            csrFile.close();

            // hand ticket and deviceId to database handler
            handler.handleTicket(ticket.ticket, ticket.deviceId);

            // Send answer to client
            String certFileName = String.format(CertificateManager.certFormatString, userFileName);
            ticket.pemFile = IOUtils.toString(new FileInputStream(certFileName));
            log.log(Level.INFO, "sentry: Authorised new device with ID " + ticket.deviceId);
            return ticket;

        } catch (Exception e) {
            log.log(Level.SEVERE, "sentry failed: " + e.getMessage());
            ticket.pemFile = null;
            return ticket;
        }
    }

}
