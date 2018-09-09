package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.TicketAuthoriser;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/uac")
public class RegistrationEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(RegistrationEndpoint.class);

    private TicketAuthoriser authoriser;

    public RegistrationEndpoint(DatabaseHandler handler,
                                TicketAuthoriser authoriser) {
        super(handler);
        this.authoriser = authoriser;
    }

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @POST
    @Path("/newuser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ClientTicket getNewCertificate(@Context HttpServletRequest request,
                                          ClientTicket ticket){

        LOG.info("Got new certificate request for device id " + ticket.deviceId);
        Validation<StatusCode, String> result = authoriser.handleTicket(ticket);
        String certificate = result.orSuccess((String) null);
        return new ClientTicket(ticket.deviceId, ticket.ticket, certificate);
    }

}
