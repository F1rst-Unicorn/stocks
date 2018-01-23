package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.business.TicketAuthoriser;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
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
                                UserContextFactory contextFactory,
                                TicketAuthoriser authoriser) {
        super(handler, contextFactory);
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
    public Ticket getNewCertificate(@Context HttpServletRequest request,
                                    Ticket ticket){

        LOG.info("Got new certificate request for device id " + ticket.deviceId);
        return authoriser.handleTicket(ticket);
    }

}
