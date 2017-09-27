package de.njsm.stocks.sentry.endpoints;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.sentry.auth.CertificateManager;
import de.njsm.stocks.sentry.db.DatabaseHandler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Path("/uac")
public class UserGenerator {

    private static final Logger LOG = LogManager.getLogger(UserGenerator.class);

    private final DatabaseHandler handler;

    public UserGenerator() throws ClassNotFoundException {
        handler = new DatabaseHandler();
    }

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
            FileInputStream input = new FileInputStream(certFileName);
            ticket.pemFile = IOUtils.toString(input);
            input.close();
            LOG.info("Authorised new device with ID " + ticket.deviceId);
            return ticket;

        } catch (Exception e) {
            LOG.warn("Error while ticket handling", e);
            ticket.pemFile = null;
            return ticket;
        }
    }

}
